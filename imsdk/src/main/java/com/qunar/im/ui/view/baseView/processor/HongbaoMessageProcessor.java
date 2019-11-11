package com.qunar.im.ui.view.baseView.processor;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qunar.im.base.jsonbean.HongbaoContent;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.base.util.BinaryUtil;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.core.utils.GlobalConfigManager;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.QunarWebActvity;
import com.qunar.im.ui.view.baseView.HongbaoView;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.ViewPool;
import com.qunar.im.ui.view.bubbleLayout.BubbleLayout;
import com.qunar.im.ui.view.dialog.OpenRedEnvelopDialog;
import com.qunar.im.utils.QtalkStringUtils;

/**
 * Created by saber on 15-12-30.
 */
public class HongbaoMessageProcessor extends DefaultMessageProcessor {

    @Override
    public void processChatView(ViewGroup parent, final IMessageItem item) {
        IMMessage message = item.getMessage();
        try {
            HongbaoContent content = JsonUtils.getGson().fromJson(message.getExt()
                    , HongbaoContent.class);
            String username = CurrentPreference.getInstance().getUserid();
            final StringBuilder sb = new StringBuilder(content.url);
            if (item.getMessage().getType() == ConversitionType.MSG_TYPE_CHAT) {
                String user_id = item.getMessage().getDirection() == IMMessage.DIRECTION_SEND?
                        QtalkStringUtils.parseLocalpart(item.getMessage().getToID()):
                        QtalkStringUtils.parseLocalpart(item.getMessage().getFromID());
                sb.append("&username=").append(username).append("&sign=")
                        .append(BinaryUtil.MD5(username + "00d8c4642c688fd6bfa9a41b523bdb6b"))
                        .append("&company=qunar&")
                        .append("user_id=")
                        .append(user_id)
                        .append("&q_d=")
                        .append(QtalkNavicationService.getInstance().getXmppdomain())
                        .append("&ck=" + CurrentPreference.getInstance().getVerifyKey());
            } else {
                sb.append("&username=").append(username).append("&sign=")
                        .append(BinaryUtil.MD5(username + "00d8c4642c688fd6bfa9a41b523bdb6b"))
                        .append("&company=qunar&")
                        .append("group_id=")
                        .append(QtalkStringUtils.parseBareJid(item.getMessage().getConversationID()))
                        .append("&q_d=")
                        .append(QtalkNavicationService.getInstance().getXmppdomain())
                        .append("&ck=" + CurrentPreference.getInstance().getVerifyKey());
            }
            HongbaoView view = ViewPool.getView(HongbaoView.class,item.getContext());
            view.setOnClickListener((v) -> {
                if(TextUtils.isEmpty(QtalkNavicationService.getInstance().getPayurl())){
                    Uri uri = Uri.parse(sb.toString());
                    Intent intent = new Intent(item.getContext(), QunarWebActvity.class);
                    intent.putExtra(Constants.BundleKey.WEB_FROM,
                            Constants.BundleValue.HONGBAO);
                    intent.putExtra(QunarWebActvity.IS_HIDE_BAR,true);
                    intent.setData(uri);
                    item.getContext().startActivity(intent);
                }else {
                    new OpenRedEnvelopDialog(item.getContext(),content.rid,message.getConversationID(),message.getFromID()).open();
                }
            });
            view.setTitle(content.typestr);
            view.setHongbaoType(content.type);
//            if(message.getMsgType() == MessageType.MSG_HONGBAO_MESSAGE)
//            {
//                view.setIcon(R.drawable.atom_ui_ic_lucky_money_red);
//            }
//            else {
//                view.setIcon(R.drawable.atom_ui_ic_aa_pay);
//            }
            parent.addView(view);
        } catch (Exception ex) {
            TextView textView = ViewPool.getView(TextView.class,item.getContext());
            textView.setText("消息类型错误");
            parent.addView(textView);
        }
    }

    @Override
    public void processBubbleView(BubbleLayout bubbleLayout, IMessageItem item) {
        bubbleLayout.setBubbleAndStrokeColor(ContextCompat.getColor(item.getContext(), R.color.atom_ui_button_dark_red_5800),ContextCompat.getColor(item.getContext(), R.color.translate));
    }
}
