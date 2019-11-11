package com.qunar.im.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.ui.util.FacebookImageUtil;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.base.util.Constants;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.processor.MessageProcessor;
import com.qunar.im.ui.view.baseView.processor.ProcessorFactory;
import com.qunar.im.ui.view.medias.play.PlayVoiceView;
import com.qunar.im.utils.QtalkStringUtils;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhaokai on 15-8-20.
 */
public class ReadToDestroyActivity extends IMBaseActivity {
    private BroadcastReceiver receiver;
    private Intent intent;
    private IMMessage message;
    private MessageProcessor processor;
    private TextView textView;
    private LinearLayout viewgroup;
    private ProgressBar progressBar;
    private ImageView imageView;
    private IMessageItem messageItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_read_to_destroy);
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        textView = (TextView) findViewById(R.id.time);
        viewgroup = (LinearLayout) findViewById(R.id.viewgroup);
        imageView = (ImageView) findViewById(R.id.imageview);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        intent = getIntent();
        receiver = new MsgHasGetReceiver();

        if (intent.hasExtra("message")) {
            message = (IMMessage) intent.getSerializableExtra("message");
            messageItem = new IMessageItem() {

                @Override
                public IMMessage getMessage() {
                    return message;
                }

                @Override
                public int getPosition() {
                    return 0;
                }

                @Override
                public Context getContext() {
                    return ReadToDestroyActivity.this;
                }

                @Override
                public Handler getHandler() {
                    return new Handler();
                }

                @Override
                public ProgressBar getProgressBar() {
                    return progressBar;
                }

                @Override
                public ImageView getErrImageView() {
                    return imageView;
                }

                @Override
                public TextView getStatusView() {
                    return null;
                }
            };
            processor = ProcessorFactory.getProcessorMap().get(message.getMsgType());
            if(processor == null){
                processor = ProcessorFactory.getProcessorMap().get(ProcessorFactory.DEFAULT_PROCESSOR);
            }
            if (message.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeVoice_VALUE) {
                textView.setText(R.string.atom_ui_tip_one_play_destory);
            }
        }
    }


    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
        this.finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(message!=null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Constants.BroadcastFlag.MESSAGE_HAS_FULLY_RECEIVED);
            intentFilter.addAction(PlayVoiceView.PLAY_VOICE_COMPLETE);
            registerReceiver(receiver, intentFilter);
            if (message.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypePhoto_VALUE
                    && message.getBody().contains("[obj type=\"image\"")) {
                SimpleDraweeView v = new SimpleDraweeView(this);
                List<Map<String, String>> list = ChatTextHelper.getObjList(message.getBody());
                if (list != null && list.size() > 0) {
                    String source = list.get(0).get("value");
                    String url = QtalkStringUtils.addFilePathDomain(source, true);
                    if (source != null) {
                        v.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.atom_ui_sharemore_picture));
                        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        viewgroup.addView(v, params);
                        FacebookImageUtil.loadWithCache(url, v, true, new FacebookImageUtil.ImageLoadCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(ReadToDestroyActivity.this, getString(R.string.atom_ui_tip_download_success), Toast.LENGTH_LONG).show();
                                startCounter();
                            }

                            @Override
                            public void onError() {
                                Toast.makeText(ReadToDestroyActivity.this, getString(R.string.atom_ui_tip_download_failed), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            } else {
                processor.processProgressbar(progressBar, messageItem);
                processor.processErrorImageView(imageView, messageItem);
                processor.processChatView(viewgroup, messageItem);
                int count = viewgroup.getChildCount();
                if (message.getMsgType() != ProtoMessageOuterClass.MessageType.MessageTypeVoice_VALUE) {
                    startCounter();
                }
                for (int index = 0; index < count; index++) {
                    View v = viewgroup.getChildAt(index);
                    if (v instanceof TextView) {
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer != null){
            timer.cancel();
        }
    }

    Timer timer;
    private void startCounter() {
        if(timer == null){
            timer = new Timer();
        }
        final TimerTask timerTask = new TimerTask() {
            int scheduleTime = 10;

            @Override
            public synchronized void run() {
                getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(Integer.toString(scheduleTime--) + getString(R.string.atom_ui_tip_after_seconds_destory));
                        if (scheduleTime == 0) {
                            cancel();
                            ReadToDestroyActivity.this.finish();
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    private final class MsgHasGetReceiver extends BroadcastReceiver {
        boolean execute = true;

        @Override
        public void onReceive(Context context, Intent intent) {
            String messageId = intent.getStringExtra(Constants.BundleKey.MESSAGE_ID);
            if (messageId != null && messageId.equals(message.getId())) {
                String action = intent.getAction();
                if (action.equals(Constants.BroadcastFlag.MESSAGE_HAS_FULLY_RECEIVED) && execute) {
                    execute = false;
                    startCounter();
                } else if (action.equals(PlayVoiceView.PLAY_VOICE_COMPLETE)) {
                    ReadToDestroyActivity.this.finish();
                }
            }
        }
    }
}
