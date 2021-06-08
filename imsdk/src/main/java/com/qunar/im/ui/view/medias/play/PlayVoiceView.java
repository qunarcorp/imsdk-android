package com.qunar.im.ui.view.medias.play;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.jsonbean.EncryptMsg;
import com.qunar.im.base.module.BaseIMMessage;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.base.structs.TransitSoundJSON;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.Utils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.ui.R;
import com.qunar.im.ui.events.VoiceViewEvent;
import com.qunar.im.ui.view.baseView.IClearableView;
import com.qunar.im.ui.view.medias.play.interfaces.IMediaPlayerCallback;

import java.io.File;
import java.lang.ref.WeakReference;

import de.greenrobot.event.EventBus;

/**
 * Created by zhaokai on 15-4-30.
 */
public class PlayVoiceView extends AppCompatTextView implements IClearableView {
    private static String TAG = "PlayVoiceView";

    public static String PLAY_VOICE_COMPLETE = "com.qunar.im.complete_play_voice";
    public String playFilePath;
    private MediaPlayerImpl player = null;
    private int duration = -1;
    private int width;
    private IMediaPlayerCallback callback;
    private IMMessage message;
    private WeakReference<Context> context;

    private Handler mHandler = new Handler(Looper.myLooper());

    private TextView statusView;

    public static final int[] drawVoiceIn = new int[]{
            R.drawable.atom_ui_in_voiceplay1,
            R.drawable.atom_ui_in_voiceplay2,
            R.drawable.atom_ui_in_voiceplay3
    };
    public static final int[] drawVoiceOut = new int[]{
            R.drawable.atom_ui_out_voiceplay1,
            R.drawable.atom_ui_out_voiceplay2,
            R.drawable.atom_ui_out_voiceplay3
    };


    public PlayVoiceView(Context context) {
        this(context, null);
    }

    public PlayVoiceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public PlayVoiceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = new WeakReference<Context>(context);
    }

    public void onCreate() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public void startBlinking() {
        Animation fadeIn = new AlphaAnimation(0.2f, 1f);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(1000);

        Animation fadeOut = new AlphaAnimation(1f, 0.2f);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(1000);
        fadeOut.setDuration(1000);

        final AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(fadeIn);
        animationSet.addAnimation(fadeOut);
        animationSet.setRepeatCount(AnimationSet.INFINITE);
        animationSet.setRepeatMode(Animation.REVERSE);
        setAnimation(animationSet);
        animationSet.start();

    }

    private void setSatus(final IMMessage message) {
        this.clearAnimation();
        if (MessageStatus.isProcession(message.getMessageState())) {
            startBlinking();
        }
    }


    /**
     * @return 音频文件长度, 单位 ms,错误时返回-1
     */
    public int getDuration() {
        return duration;
    }

    /**
     * @param playFilePath 设置该段录音的文件路径
     */
    public void init(String playFilePath, IMMessage message, IMediaPlayerCallback callback) {
        init(playFilePath, 1, message, callback);
    }

    public void initStatus(TextView view, IMMessage message) {
        statusView = view;
        if (message.getDirection() == IMMessage.DIRECTION_RECV) {
            String body = null;
            //加密消息  先解密
            if(message.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeEncrypt_VALUE){
                EncryptMsg encryptMsg = ChatTextHelper.getEncryptMessageBody(message);
                if(encryptMsg !=null)
                    body = encryptMsg.Content;
            }else {
                body = message.getBody();
            }
            if (!TextUtils.isEmpty(body)) {
                TransitSoundJSON extJson = JsonUtils.getGson().fromJson(body, TransitSoundJSON.class);
                if (extJson.s != TransitSoundJSON.PLAYED) {
                    if(view != null){
                        view.setVisibility(VISIBLE);
                    }
                }
            }
        }
    }


    /**
     * @param playFilePath 设置该段录音的文件路径
     * @param duration     音频长度,单位 s
     */
    public void init(String playFilePath, int duration, IMMessage message, IMediaPlayerCallback callback) {
        this.duration = duration;
        this.message = message;
        if (thread != null) {
            mHandler.removeCallbacks(thread);
        }


        this.playFilePath = playFilePath;

        player = MediaPlayerImpl.getInstance();
        if (player.msgId.equals(message.getId()) && player.isPlaying()) {
            startAnimation();
        }
        width = getContext().getResources().getDisplayMetrics().widthPixels;

        if (this.duration == -1) {
            this.duration = player.getDuration() / 1000;
        }
        if (this.duration <= 0) {
            this.duration = 1;
        }
        this.callback = callback;
        initDrawableView();
        setSatus(message);
    }


    private void setDrawable() {
        //先还原
        setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        if (message.getDirection() == IMMessage.DIRECTION_SEND) {
            setCompoundDrawablesWithIntrinsicBounds(0, 0, drawVoiceOut[2], 0);
            setGravity(Gravity.LEFT);
        } else {
            setCompoundDrawablesWithIntrinsicBounds(drawVoiceIn[2], 0, 0, 0);
            setGravity(Gravity.RIGHT);
        }
    }

    private void initDrawableView() {
        //根据音频的时间确定应该绘制的宽度
        double MIN_WIDTH_PARENT = 0.25;
        double MAX_WIDTH_PERCENT = 0.75;
        int drawWidth = (int) (MIN_WIDTH_PARENT * width + ((double) duration / 60 * (MAX_WIDTH_PERCENT - MIN_WIDTH_PARENT) * width));
        setDrawable();
        setWidth(drawWidth);
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.atom_ui_text_size_extra_micro));
        setText("");
        setText(String.valueOf(duration) + context.get().getString(R.string.atom_ui_common_prime));
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                player.shutdown();
                if (player.msgId.equals(message.getId()) && player.isPlaying()) {
                    player.stopPlay();
                } else {
                    if (statusView != null && statusView.getVisibility() == VISIBLE) {
                        if(message.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeEncrypt_VALUE)//加密的语音消息 不走队列 直接单个播放
                            playVoice();
                        else player.playList(message.getTime().getTime(), message.getConversationID(),
                                context.get());
                    } else {
                        playVoice();
                    }
                }
            }
        });
    }


    public void playVoice() {
        final File file = new File(playFilePath);
        if (!file.exists())
            return;
        player.reset();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {//设置播放完成的监听器
            @Override
            public void onCompletion(MediaPlayer mp) {
                LogUtil.d(TAG, "onCompletion");
                player.overPlay();
                message.setReadStatus(BaseIMMessage.PREPARE);
                CommonConfig.isPlayVoice = false;
                player.changeNormalCall(context.get());
                if (callback != null) callback.doCompletePlaying();
                Intent intent = new Intent();
                intent.setAction(PLAY_VOICE_COMPLETE);
                intent.putExtra(Constants.BundleKey.MESSAGE_ID, message.getId());
                Utils.sendLocalBroadcast(intent, context.get());
            }
        });
        BackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                player.setResourceFilePath(file.getAbsolutePath(), message.getId());
                if (callback != null)
                    callback.doBeforeStartPlaying();
                player.startPlay();
                if (callback != null)
                    callback.doAfterStartPlaying();
            }
        });
    }

    public void resetMessage() {
        message.setReadStatus(BaseIMMessage.PREPARE);
        setDrawable();
    }

    public void setMessageReading() {
        message.setReadStatus(BaseIMMessage.READING);
    }

    public boolean isSend() {
        return message.getDirection() == IMMessage.DIRECTION_SEND;
    }

    public void onEventMainThread(VoiceViewEvent event) {
        if (event.id.equals(message.getId())) {
            if (this.statusView != null && this.statusView.getVisibility() == VISIBLE) {
                this.statusView.setVisibility(GONE);
            }
            startAnimation();
        }
    }

    public void startAnimation() {
        LogUtil.d(TAG, "start animaation");
        message.setReadStatus(BaseIMMessage.READ);
        thread = new VoiceAnimateThread(message.getId());
        mHandler.post(thread);
    }

    @Override
    public void clear() {
        EventBus.getDefault().unregister(this);
    }

    public class VoiceAnimateThread implements Runnable {
        final String msgId;
        int index = 0;

        public VoiceAnimateThread(String id) {
            msgId = id;
        }

        @Override
        public void run() {
            if (isSend()) {
                setCompoundDrawablesWithIntrinsicBounds(0, 0,
                        PlayVoiceView.drawVoiceOut[index++ % 3], 0);
            } else {
                setCompoundDrawablesWithIntrinsicBounds(PlayVoiceView.drawVoiceIn[index++ % 3], 0,
                        0, 0);
            }
            if (msgId.equals(player.msgId) && player.isPlaying()) {
                setMessageReading();
                mHandler.postDelayed(this, 330);
            } else {
                resetMessage();
            }
        }
    }

    private VoiceAnimateThread thread;
}
