package com.qunar.im.ui.view.baseView;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.jsonbean.LogInfo;
import com.qunar.im.base.module.BaseIMMessage;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.log.LogConstans;
import com.qunar.im.log.LogService;
import com.qunar.im.log.QLog;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.ChatViewAdapter;
import com.qunar.im.ui.view.MyLinearLayout;
import com.qunar.im.ui.view.baseView.processor.MessageProcessor;
import com.qunar.im.ui.view.baseView.processor.ProcessorFactory;

/**
 * Created by zhaokai on 15-9-15.
 */
public class ExtendBaseView extends IMChatBaseView {

    private MyLinearLayout richText;
    private CheckBox chb_share_msg;

    public ExtendBaseView(Context context) {
        super(context);
    }

    public ExtendBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendBaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void startAnimate(int count)
    {
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
        animationSet.setRepeatCount(count);
        animationSet.setRepeatMode(Animation.REVERSE);
        setAnimation(animationSet);
        animationSet.start();
    }

    @Override
    protected void findViewById() {
        super.findViewById();
        richText = findViewById(R.id.rich_text);
        chb_share_msg = findViewById(R.id.chb_share_msg);
    }

    public void setCheckboxEvent(CompoundButton.OnCheckedChangeListener checkboxEvent)
    {
        chb_share_msg.setOnCheckedChangeListener(checkboxEvent);
    }

    public void changeChbStatus(boolean checked)
    {
        chb_share_msg.setChecked(checked);
    }

    public CheckBox getCheckBox()
    {
        return chb_share_msg;
    }

    public void saveId2Chb(IMMessage message)
    {
        chb_share_msg.setTag(message);
    }

    public void changeShareStatus(boolean share)
    {
        if(share) {
            LayoutParams params = (LayoutParams) mRightRoundedImageView.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            params.addRule(RelativeLayout.LEFT_OF, R.id.chb_share_msg);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.addRule(RelativeLayout.ALIGN_PARENT_END, 0);
                params.addRule(RelativeLayout.START_OF, R.id.chb_share_msg);
            }
            mRightRoundedImageView.setLayoutParams(params);
            chb_share_msg.setVisibility(VISIBLE);
        }
        else {
            chb_share_msg.setVisibility(GONE);
            LayoutParams params = (LayoutParams) mRightRoundedImageView.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.LEFT_OF, 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.addRule(RelativeLayout.ALIGN_PARENT_END);
                params.addRule(RelativeLayout.START_OF, 0);
            }
            mRightRoundedImageView.setLayoutParams(params);
        }
    }

    @Override
    public void setMessage(final ChatViewAdapter adapter, Handler handler, final IMMessage message, int position) {
        super.message = message;
        super.position = position;
        super.handler = handler;
        setInVisible();
        int type = ProtoMessageOuterClass.MessageType.MessageTypeText_VALUE;
        MessageProcessor processor = ProcessorFactory.getProcessorMap().get(message.getMsgType());
        if (processor == null) {
            processor = ProcessorFactory.getProcessorMap().get(ProcessorFactory.DEFAULT_PROCESSOR);
        }
        else {
            type = message.getMsgType();
        }
        if (richText.getChildCount() > 0) {
            for (int index = 0; index < richText.getChildCount(); index++) {
                ViewPool.recycleView(richText.getChildAt(index));
            }
            richText.removeAllViews();
            richText.setTag(R.id.imageview, null);
        }
        message.position = BaseIMMessage.MIDDLE;
        processor.processStatusView(statusView);
        if(ProcessorFactory.getMiddleType().contains(type))
        {
            processor.processChatView(richText, this);
            processor.processTimeText(mTimeTextView,this,adapter);
        }
        else {
            if(message.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeGroupNotify_VALUE)
            {
                message.setDirection(IMMessage.DIRECTION_MIDDLE);
            }
            super.setMessage(adapter, handler, message, position);
        }
    }

    @Override
    public void dispatchWindowFocusChanged(boolean hasFocus) {
        try{
            super.dispatchWindowFocusChanged(hasFocus);
        }catch (Exception e){
            Logger.e("dispatchWindowFocusChanged");
            LogInfo logInfo = QLog.build(LogConstans.LogType.CRA,LogConstans.LogSubType.CRASH).eventId("ExtendBaseView").describtion("会话内crash").currentPage("会话页面");
            LogService.getInstance().saveLog(logInfo);
        }
    }
}
