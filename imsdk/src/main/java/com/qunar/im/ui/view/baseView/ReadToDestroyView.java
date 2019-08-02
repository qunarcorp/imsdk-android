package com.qunar.im.ui.view.baseView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.jsonbean.AutoDestroyMessageExtention;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.core.manager.IMDatabaseManager;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.ReadToDestroyActivity;
import com.qunar.im.ui.view.baseView.processor.ProcessorFactory;

/**
 * Created by zhaokai on 15-8-20.
 */
public class ReadToDestroyView extends TextView {
    Context context;

    public ReadToDestroyView(Context context) {
        this(context, null);
    }

    public ReadToDestroyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReadToDestroyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * for startActivity()
     *
     * @param context
     */
    public void chanageContext(Context context) {
        this.context = context;
    }

    public void init(final IMMessage message) {
        final String jid = message.getDirection() == IMMessage.DIRECTION_SEND ? message.getToID() : message.getFromID();
        final AutoDestroyMessageExtention extention = JsonUtils.getGson().fromJson(message.getExt(), AutoDestroyMessageExtention.class);
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        int l_r_padding_size = QunarIMApp.getContext().getResources().
                getDimensionPixelSize(R.dimen.atom_ui_chat_item_padding_l_r);
        int t_b_padding_size = QunarIMApp.getContext().getResources().
                getDimensionPixelSize(R.dimen.atom_ui_chat_item_padding);
        setPadding(l_r_padding_size, t_b_padding_size, l_r_padding_size, t_b_padding_size);
        setGravity(Gravity.CENTER);
        if (extention.hasRead == null || "false".equals(extention.hasRead)) {  //没有被读过
            setText(context.getText(R.string.atom_ui_body_read_to_destroy));
            setTextColor(Color.BLACK);
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    extention.hasRead = "true";
                    message.setExt(JsonUtils.getGson().toJson(extention));
//                    IMessageRecordDataModel model = new MessageRecordDataModel();
//                    if (message.getType() == ConversitionType.MSG_TYPE_CHAT) {
//                        model.insertSingleMessage(message, jid);
//                    } else if (message.getType() == ConversitionType.MSG_TYPE_GROUP) {
//                        model.insertMultipleMsg(message, jid);
//                    }

                    IMDatabaseManager.getInstance().InsertChatMessage(message, false);

                    setText(context.getText(R.string.atom_ui_message_has_destroy));
                    setTextColor(Color.GRAY);
                    IMMessage tmpIMMessage = new IMMessage();
                    tmpIMMessage.setId(message.getId());
                    tmpIMMessage.setMessageID(message.getId());
                    tmpIMMessage.setMsgType(extention.msgType);
                    Object obj = ProcessorFactory.getProcessorMap().get(tmpIMMessage.getMsgType());
                    if (obj == null) {
                        tmpIMMessage.setBody(extention.descStr);
                    } else {
                        tmpIMMessage.setBody(extention.message);
                    }
                    tmpIMMessage.setExt(extention.message);
                    tmpIMMessage.setDirection(message.getDirection());
                    tmpIMMessage.setToID(message.getToID());
                    tmpIMMessage.setFromID(message.getFromID());
                    tmpIMMessage.setReadState(MessageStatus.REMOTE_STATUS_CHAT_SUCCESS);
                    setOnClickListener(null);
                    Intent intent = new Intent(context, ReadToDestroyActivity.class);
                    intent.putExtra("message", tmpIMMessage);
                    context.startActivity(intent);
                    change2ReadStatus();
                }
            });
            Drawable drawable = getResources().getDrawable(R.drawable.atom_ui_ic_fire);
            if (drawable != null)
                drawable.setBounds(0, 0, 32, 32);
            setCompoundDrawables(drawable, null, null, null);
        } else {
            change2ReadStatus();
        }
    }

    private void change2ReadStatus() {
        setText(context.getText(R.string.atom_ui_message_has_destroy));
        setTextColor(Color.GRAY);
        setOnClickListener(null);
        Drawable drawable = getResources().getDrawable(R.drawable.atom_ui_ic_fire_msg);
        if (drawable != null)
            drawable.setBounds(0, 0, 32, 32);
        setCompoundDrawables(drawable, null, null, null);
    }


}