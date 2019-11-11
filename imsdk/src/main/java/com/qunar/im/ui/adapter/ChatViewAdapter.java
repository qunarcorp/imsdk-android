package com.qunar.im.ui.adapter;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.facebook.drawee.view.SimpleDraweeView;
import com.orhanobut.logger.Logger;
import com.qunar.im.base.module.BaseIMMessage;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.baseView.IMChatBaseView;
import com.qunar.im.utils.ConnectionUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaokai on 15-8-11.
 */
public class ChatViewAdapter extends BaseAdapter implements IMNotificaitonCenter.NotificationCenterDelegate {
    private static final String TAG = "ChatViewAdapter";

    protected WeakReference<Context> context;
    protected LeftImageClickHandler leftImageClickHandler;
    protected LeftImageLongClickHandler leftImageLongClickHandler;
    protected RightSendFailureClickHandler rightSendFailureClickHandler;
    protected GravatarHandler gravatarHandler;
    protected ContextMenuRegister contextMenuRegister;
    protected String toId;
//    private LinkedHashMap<String, IMChatBaseView> viewsMap;
    protected Handler handler;
    //核心连接管理类
    protected ConnectionUtil connectionUtil;

    public boolean getShowNick() {
        return showNick;
    }

    protected boolean showNick = true;

    protected boolean showReadState= false;
    protected List<IMMessage> messages = new ArrayList<>();

    public ChatViewAdapter() {

    }

    public ChatViewAdapter(Context context, String toId, Handler uiHandler) {
        this(context, toId, uiHandler, true);
    }

    public ChatViewAdapter(Context context, String toId, Handler uiHandler, boolean showNick) {
        this.connectionUtil = ConnectionUtil.getInstance();
        this.showNick = showNick;
        this.context = new WeakReference<Context>(context);
        this.toId = toId;
        this.handler = uiHandler;
        connectionUtil.addEvent(this, QtalkEvent.Chat_Message_Send_State);
        connectionUtil.addEvent(this, QtalkEvent.Chat_Message_Read_State);
        connectionUtil.addEvent(this, QtalkEvent.Chat_Message_Send_Failed);

//        viewsMap = new LinkedHashMap();
    }

    public void setLeftImageLongClickHandler(LeftImageLongClickHandler leftImageLongClickHandler) {
        this.leftImageLongClickHandler = leftImageLongClickHandler;
    }

    public void setLeftImageClickHandler(LeftImageClickHandler leftImageClickHandler) {
        this.leftImageClickHandler = leftImageClickHandler;
    }

    public void setGravatarHandler(GravatarHandler gravatarHandler) {
        this.gravatarHandler = gravatarHandler;
    }

    public void setContextMenuRegister(ContextMenuRegister register) {
        this.contextMenuRegister = register;
    }

    public void setRightSendFailureClickHandler(RightSendFailureClickHandler handler) {
        this.rightSendFailureClickHandler = handler;
    }

    public void setMessages(List<IMMessage> msgs) {
        if (msgs != null) {
            this.messages = msgs;
        } else {
            this.messages.clear();
        }
        this.notifyDataSetChanged();
    }

    public void setShowNick(boolean showNick) {
        this.showNick = showNick;
    }

    public void setShowReadState(boolean showReadState){
        this.showReadState = showReadState;
    }

    public void addNewMsg(IMMessage msg) {
        if (messages.size() > 0) {
            for (int i = messages.size() - 1; i >= messages.size() - 10; i--) {
                if (messages.get(i).equals(msg)) {
                    return;
                }
                if (i == 0) {
                    break;
                }
            }
        }
        this.messages.add(msg);
        this.notifyDataSetChanged();
//        if (msg.getDirection() == 1) {
//            connectionUtil.addEvent(this, msg.getId());
//        }
    }

    public void clearAndAddMsgs(List<IMMessage> msgs) {
//        this.messages.clear();
        setMessages(msgs);
    }

    public void addNewMsgs(List<IMMessage> msgs) {
        this.messages.addAll(msgs);
        this.notifyDataSetChanged();
    }

    public void addOldMsg(List<IMMessage> history) {
        this.messages.addAll(0, history);
        if (messages != null && messages.size() > 0) {
            this.notifyDataSetChanged();
        }
    }

    public void replaceItem(IMMessage message) {
        int i = messages.indexOf(message);
        if (i > -1) {
            messages.set(i, message);
        }
        notifyDataSetChanged();
    }

    public void deleteItem(IMMessage message) {
        for (int i = messages.size() - 1; i >= 0; i--) {
            if (message.getMessageId().equals(messages.get(i).getMessageId())) {
                messages.remove(i);
                this.notifyDataSetChanged();
                return;
            }
        }
    }

    public void updateMessges(String key) {
        for (int i = messages.size() - 1; i >= 0; i--) {
            if (key.equals(messages.get(i).getId())) {
                messages.get(i).setReadState(1);
                this.notifyDataSetChanged();
//                ((PbIMChatBaseView) getView(i,null,null)).setMessage_progress_rightSuccess();
                return;
            }
        }
    }

    public List<IMMessage> getMessages() {
        return messages;
    }

    public IMMessage getFirstMessage() {
        if (messages != null && messages.size() > 0)
            return messages.get(0);
        else return null;
    }

    public IMMessage getLastMessage(){
        if (messages != null && messages.size() > 0)
            return messages.get(messages.size()-1);
        else return null;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public IMMessage getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        IMMessage msg = getItem(position);
        if (msg == null)
            return -1;
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        IMChatBaseView view;
        LogUtil.d(TAG, "getView " + position);
        if (convertView == null) {
            view = (IMChatBaseView) LayoutInflater.from(context.get()).inflate(R.layout.atom_ui_item_chat_extend, null);
            convertView = view;
            view.setGravatarHandler(gravatarHandler);
            view.setLeftImageLongClickHandler(leftImageLongClickHandler);
            view.setLeftImageClickHandler(leftImageClickHandler);
            view.setContextMenuRegister(contextMenuRegister);
            view.setRightSendFailureClickHandler(rightSendFailureClickHandler);
        } else {
            view = (IMChatBaseView) convertView;
        }
//        viewsMap.put(position + "", view);
        final IMMessage message = getItem(position);
        if (message.getDirection() == IMMessage.DIRECTION_RECV &&
                TextUtils.isEmpty(message.getFromID())) {
            message.setFromID(toId);
        }
        try {
            view.setMessage(this, handler, message, position);//调用processer
        } catch (Exception ex) {
            LogUtil.e(TAG, "ERROR", ex);
        }
        view.setNickStatus(showNick
                && message.position == BaseIMMessage.LEFT);
        return convertView;
    }


    public interface LeftImageClickHandler {
        void onLeftImageClickEvent(String jid);
    }

    public interface LeftImageLongClickHandler {
        void onLeftImageLongClickEvent(String from);
    }

    public interface GravatarHandler {
        void requestGravatarEvent(final String jid, final String imageSrc, final SimpleDraweeView view);
    }

    public interface ContextMenuRegister {
        void registerContextMenu(View v);
    }

    public interface RightSendFailureClickHandler {
        void resendMessage(IMMessage message);
    }

    public void releaseViews() {
//        if (viewsMap != null && viewsMap.size() > 0) {
//            for (LinkedHashMap.Entry<String, IMChatBaseView> entry : viewsMap.entrySet()) {
//                if (entry.getValue() != null) {
//                    entry.getValue().removeAllViews();
//                }
//            }
//        }
//        viewsMap.clear();
//        messages.clear();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        connectionUtil.removeEvent(this, QtalkEvent.Chat_Message_Send_State);
        connectionUtil.removeEvent(this, QtalkEvent.Chat_Message_Read_State);
        connectionUtil.removeEvent(this, QtalkEvent.Chat_Message_Send_Failed);
    }

    //TODO 待优化
    @Override
    public void didReceivedNotification(String key, Object... args) {
        switch (key) {

            case QtalkEvent.Chat_Message_Read_State:
                try {
                    IMMessage readMessage = (IMMessage) args[0];
                    for (int i = 0; i < readMessage.getNewReadList().length(); i++) {
                        for (int j = messages.size() - 1; j >= 0; j--) {
                            if (readMessage.getNewReadList().getJSONObject(i).getString("id").replace("consult-","").equals(messages.get(j).getId().replace("consult-",""))) {
                                messages.get(j).setReadState(readMessage.getReadState());
                                this.notifyDataSetChanged();
                                continue;
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


                break;

            case QtalkEvent.Chat_Message_Send_State:
                Logger.i("返回的消息id:" + args[0]);
                IMMessage sendMessage = (IMMessage) args[0];
                for (int i = messages.size() - 1; i >= 0; i--) {
                    if (sendMessage.getMessageId().equals(messages.get(i).getId())) {
                        messages.get(i).setReadState(sendMessage.getReadState());
                        messages.get(i).setMessageState(sendMessage.getMessageState());
//                        ConnectionUtil.getInstance(context.get()).workworldremoveEvent(this, key);
                        this.notifyDataSetChanged();

//                ((PbIMChatBaseView) getView(i,null,null)).setMessage_progress_rightSuccess();
                        return;
                    }
                }
                break;
            case QtalkEvent.Chat_Message_Send_Failed:
                Logger.i("失败返回的消息id:" + args[0]);
                for (int i = messages.size() - 1; i >= 0; i--) {
                    if (args[0].equals(messages.get(i).getId())) {
                        messages.get(i).setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
//                        ConnectionUtil.getInstance(context.get()).workworldremoveEvent(this, key);
                        this.notifyDataSetChanged();
//                ((PbIMChatBaseView) getView(i,null,null)).setMessage_progress_rightSuccess();
                        return;
                    }
                }
                break;
        }
    }
}
