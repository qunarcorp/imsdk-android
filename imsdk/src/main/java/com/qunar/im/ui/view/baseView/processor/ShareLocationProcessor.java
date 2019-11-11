package com.qunar.im.ui.view.baseView.processor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.qunar.im.base.jsonbean.EncryptMsg;
import com.qunar.im.base.jsonbean.ShareLocation;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.LocationActivity;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.MapView;
import com.qunar.im.ui.view.baseView.ViewPool;
import com.qunar.im.ui.view.bubbleLayout.BubbleLayout;
import com.qunar.im.utils.QtalkStringUtils;

/**
 * Created by saber on 15-8-18.
 */
public class ShareLocationProcessor extends DefaultMessageProcessor {
    @Override
    public void processChatView(ViewGroup parent, IMessageItem item) {
        final IMMessage message = item.getMessage();
        final Context context = item.getContext();
        String ext = message.getExt();
        if (message.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeEncrypt_VALUE) {
            EncryptMsg encryptMsg = ChatTextHelper.getEncryptMessageBody(message);
            if (encryptMsg != null) ext = encryptMsg.Content;
        }
        final ShareLocation shareLocation = JsonUtils.getGson().fromJson(ext, ShareLocation.class);
        if (shareLocation.fileUrl == null) {
            shareLocation.fileUrl = "res:///" + R.drawable.atom_ui_share_location;
        } else {
            if (!shareLocation.fileUrl.startsWith("file:///"))
                shareLocation.fileUrl = QtalkStringUtils.addFilePathDomain(shareLocation.fileUrl, true);
        }
        MapView mapView = ViewPool.getView(MapView.class, context);
        mapView.setMapInfo(Uri.parse(shareLocation.fileUrl),
                TextUtils.isEmpty(shareLocation.name) ? shareLocation.adress : shareLocation.name);
        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = message.getType() == ConversitionType.MSG_TYPE_GROUP ?
                        QtalkStringUtils.parseResource(message.getFromID())
                        : QtalkStringUtils.parseLocalpart(message.getFromID());
                //如果是自己发送的话上面得到的userId是空串 其中 fromId是 userId@ejabhost*
                if (message.getDirection() == IMMessage.DIRECTION_SEND) {
                    userId = "";
                }
                showMap(userId, context, shareLocation);
            }
        });
//        sendNotify4Snap(context, message);
        parent.addView(mapView);
    }

    private void showMap(String id, Context context, ShareLocation shareLocation) {
        /**
         *  rewrite by zhaokai
         *  On Dec 1st
         * */
        Bundle bundle = new Bundle();
        bundle.putString("latitude", shareLocation.latitude);
        bundle.putString("longitude", shareLocation.longitude);
        bundle.putString("address", shareLocation.adress);
        bundle.putString("id", id);
        bundle.putString("name", shareLocation.name);
        bundle.putInt(Constants.BundleKey.LOCATION_TYPE, LocationActivity.TYPE_RECEIVE);
        Intent intent = new Intent(context, LocationActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void processBubbleView(BubbleLayout bubbleLayout, IMessageItem item) {
        bubbleLayout.setBubbleColor(ContextCompat.getColor(item.getContext(), R.color.atom_ui_chat_bubble_left_bg));
        bubbleLayout.setStrokeColor(ContextCompat.getColor(item.getContext(),R.color.atom_ui_chat_bubble_left_stoken_color));
    }
}
