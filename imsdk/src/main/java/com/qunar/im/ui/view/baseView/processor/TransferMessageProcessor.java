package com.qunar.im.ui.view.baseView.processor;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.qunar.im.base.jsonbean.TransferConsult;
import com.qunar.im.base.jsonbean.TransferWebChat;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.TransferMsgView;
import com.qunar.im.ui.view.baseView.ViewPool;
import com.qunar.im.utils.QtalkStringUtils;

/**
 * Created by saber on 16-3-22.
 * 会话转移processor
 */
public class TransferMessageProcessor extends DefaultMessageProcessor {
    @Override
    public void processChatView(ViewGroup parent, IMessageItem item) {
        TransferMsgView transferMsgView = ViewPool.getView(TransferMsgView.class, item.getContext());
        IMMessage message = item.getMessage();
        //暂时只处理收到客服发送的1002消息，展示查看历史记录
        if (!CommonConfig.isQtalk && CurrentPreference.getInstance().isMerchants()) {
            try {
                TransferConsult json =
                        JsonUtils.getGson().fromJson(message.getBody(), TransferConsult.class);
                TransferWebChat transferWebChat = JsonUtils.getGson().fromJson(message.getBody(), TransferWebChat.class);
                if(message.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeTransChatToCustomerService_VALUE) {
                    String from = QtalkStringUtils.parseBareJid(message.getFromID());
                    if(from.equals(CurrentPreference.getInstance().getUserid())){

                    }else{
                        String url = String.format(Constants.TRANSFER_URL,json.u,from,
                                CurrentPreference.getInstance().getUserid(), CurrentPreference.getInstance().getVerifyKey());
                        transferMsgView.initServer(from,
                                json.r,url,json.u);
                        parent.setVisibility(View.VISIBLE);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        parent.addView(transferMsgView,layoutParams);
                    }

                }/*else if(message.getMsgType() == MessageType.TRANSFER_BACK_CUSTOM) {
                    String text = "收到客户反馈成功";

                } else if(message.getMsgType() == MessageType.TRANSFER_BACK_SERVER) {
                    String text = "收到服务返回成功";
                }*/
            }
            catch (Exception e)
            {
                LogUtil.e(TAG,"ERROR",e);
            }
        }/*else {
            try {
                TransferConversation json =
                        JsonUtils.getGson().fromJson(message.getBody(), TransferConversation.class);
                if(message.getMsgType() == MessageType.TRANSFER_TO_SERVER)
                {
                    String from = QtalkStringUtils.parseBareJid(message.getFromID());
                    String url = String.format(Constants.TRANSFER_URL,json.TransId,from,
                            CurrentPreference.getInstance().getUserid(), CommonConfig.verifyKey);
                    transferMsgView.initServer(from,
                            json.TransReson,url,json.TransId);
                }
                else if(message.getMsgType() == MessageType.TRANSFER_TO_CUSTOMER)
                {
                    String from = QtalkStringUtils.parseBareJid(message.getFromID());
                    transferMsgView.initCustomer(from,json.TransId);
                }
                parent.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                parent.addView(transferMsgView,layoutParams);
            }
            catch (Exception e)
            {
                LogUtil.e(TAG,"ERROR",e);
            }
        }*/
    }
}

