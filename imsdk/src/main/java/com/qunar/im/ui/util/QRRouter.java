package com.qunar.im.ui.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.Utils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.ChatroomInfoActivity;
import com.qunar.im.ui.activity.QunarWebActvity;
import com.qunar.im.ui.activity.RobotInfoActivity;
import com.qunar.im.ui.fragment.QRcodeLoginConfirmFragment;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.QtalkStringUtils;

public class QRRouter {

    public static void handleQRCode(String content, Context context) {
        Uri uri = Uri.parse(content);
        Logger.i("handleQRCode:" + uri.toString());
        String protocol = uri.getScheme();
        if(protocol!=null)
        {
            protocol = protocol.toLowerCase();
            if(protocol.equals(Constants.Config.QR_SCHEMA))
            {
                if(uri.getHost().equals("user"))
                {
                    String id = uri.getQueryParameter("id");
                    Intent intent = ReflectUtil.getQtalkServiceRNActivityIntent(context);
                    if(intent == null){
                        return;
                    }
                    intent.putExtra("UserId", id);
                    intent.putExtra("module", "UserCard");
                    intent.putExtra("Version", "1.0.0");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                else if(uri.getHost().equals("group"))
                {
                    String roomId = QtalkStringUtils.roomId2Jid(uri.getQueryParameter("id"));
                    boolean check = ConnectionUtil.getInstance().checkGroupByJid(roomId);
                    if (check ) {//如果已经在群里

                        Intent intent = ReflectUtil.getQtalkServiceRNActivityIntent(context);
                        if(intent == null){
                            return;
                        }
                        intent.putExtra("module", Constants.RNKey.GROUPCARD);
                        intent.putExtra("groupId", roomId);
                        intent.putExtra("permissions", ConnectionUtil.getInstance().selectGroupMemberPermissionsByGroupIdAndMemberId(roomId, CurrentPreference.getInstance().getPreferenceUserId()));
                        context.startActivity(intent);


                    } else { //如果没有在群里，跳转到群信息
//
                        Intent i =new Intent(context, ChatroomInfoActivity.class);
                        i.putExtra("roomId", roomId);
                        context.startActivity(i);
                    }
                }
                else if(uri.getHost().equals("robot"))
                {
                    String robotId = uri.getQueryParameter("id");
                    String cnt = uri.getQueryParameter("content");
                    String msgType = uri.getQueryParameter("msgType");
                    Intent robotIntent =new Intent(context, RobotInfoActivity.class);
                    robotIntent.putExtra("robotId",robotId);
                    robotIntent.putExtra("content",cnt);
                    robotIntent.putExtra("msgType", msgType);
                    context.startActivity(robotIntent);
                }
            }else if(protocol.equals("qimlogin")) {
                LogUtil.i("authdata", "content = " + content);
                if (uri.getHost().equals("qrcodelogin")) {
                    String qrcodekey = uri.getQueryParameter("k");
                    String v = uri.getQueryParameter("v");
                    String p = uri.getQueryParameter("p");
                    String type = uri.getQueryParameter("type");
                    if (!TextUtils.isEmpty(p) && p.equalsIgnoreCase(CommonConfig.currentPlat)) {
                        Bundle b = new Bundle();
                        b.putString("qrcodekey", qrcodekey);
                        b.putString("v", v);
                        b.putString("type",type);
                        QRcodeLoginConfirmFragment dialog = new QRcodeLoginConfirmFragment();
                        dialog.setArguments(b);
                        dialog.show(((Activity) context).getFragmentManager(), "LoginConfirm");
                        return;
                    } else {
                        if (!TextUtils.isEmpty(p)) {
                            Toast.makeText(context, "请用手机登录" + p + "客户端扫码", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                }
            } else if(protocol.equals("http")||protocol.equals("https"))
            {
                Logger.i("扫码地址:"+protocol+"uri:"+uri);
                Intent intent = new Intent(context, QunarWebActvity.class);
                intent.setData(uri);
                context.startActivity(intent);

            }else if(protocol.equals("qpr")){
                try{
                    Class classHyMain = Class.forName("com.qunar.im.camelhelp.HyMainActivity");
                    Intent intent = new Intent(context, classHyMain);
                    intent.setData(uri);
                    context.startActivity(intent);
                }catch (ClassNotFoundException e){

                }
            }
            return;
        }
        // drop into clipboard
        Utils.dropIntoClipboard(content, context);
        Toast.makeText(context, context.getString(R.string.atom_ui_tip_copied), Toast.LENGTH_LONG).show();
    }
}
