package com.qunar.im.ui.view.baseView.processor;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.qunar.im.base.jsonbean.ButtonMessageJson;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.ui.presenter.views.IChatView;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.ui.view.baseView.ButtonMessageView;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.ViewPool;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.DeviceUtil;
import com.qunar.im.utils.HttpUtil;

import java.util.List;
import java.util.Map;

public class BottomButtonMessageProcessor extends TextMessageProcessor {

    Context context;
//    private IMMessage message;
    @Override
    public void processChatView(ViewGroup parent, final IMessageItem item) {
//        super.processChatView(parent, item);
        final ButtonMessageView view = ViewPool.getView(ButtonMessageView.class, item.getContext());
        context = item.getContext();
        final IMMessage message = item.getMessage();
        String ex = message.getExt();
        final ButtonMessageJson bmj = JsonUtils.getGson().fromJson(ex, ButtonMessageJson.class);
        String realBody = bmj.getContent();
        List<Map<String, String>> list = ChatTextHelper.getObjList(realBody);
        view.setBottomContent(bmj.getMiddleContent());
        view.setLeftBtn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request(message,true, bmj, view);
            }
        });
        view.setMiddleBtn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request(message,false, bmj, view);
            }
        });
        view.setRightBtn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getContext() instanceof IChatView) {
                    ((IChatView) item.getContext()).setInputMsg("教小拿 ");
                }
            }
        });
        LinearLayout body = view.getContentLayout();
        LinearLayout btnGroup = view.getBtnGroup();
        ViewGroup.LayoutParams lp = btnGroup.getLayoutParams();
//
        btnGroup.setLayoutParams(lp);
        int width = DeviceUtil.getWindowWidthPX(item.getContext());
        lp.width = (int) (width * 0.6);
        body.removeAllViews();
        setTextOrImageView(list, body, item.getContext(), item.getMessage());

        if(MessageStatus.isExistStatus(message.getReadState(),MessageStatus.REMOTE_STATUS_CHAT_OPERATION)){
            view.showBottom(false);
        }else{
            view.showBottom(true);
        }
        parent.setVisibility(View.VISIBLE);
        parent.addView(view);
    }


    public void request(final IMMessage message, boolean isOk, ButtonMessageJson json, final ButtonMessageView view) {

        String url = json.getUrl();
        Map<String, String> map = json.getRequestPost();
        if(isOk){
            map.put("isOk", "yes");
        }else{
            map.put("isOk", "no");
        }

        HttpUtil.robotConfirm(url, map, new ProtocolCallback.UnitCallback<Boolean>() {
            @Override
            public void onCompleted(final Boolean aBoolean) {

                if (aBoolean) {
                    //如果成功 通知其他段更新操作符
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            view.showBottom(!aBoolean);
                            ConnectionUtil.getInstance().sendMessageOperation(message.getFromID(),MessageStatus.STATUS_SINGLE_OPERATION+"",message.getMessageId());
                            message.setReadState(message.getReadStatus()|MessageStatus.STATUS_SINGLE_OPERATION);
                            IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Read_State, message);
                        }
                    });

                }
            }

            @Override
            public void onFailure(String errMsg) {
                view.showBottom(false);
            }
        });
    }

}
