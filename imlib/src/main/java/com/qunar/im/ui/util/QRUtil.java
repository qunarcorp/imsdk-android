package com.qunar.im.ui.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.orhanobut.logger.Logger;
import com.qunar.im.utils.ConnectionUtil;
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
import com.qunar.im.utils.QtalkStringUtils;
import com.qunar.rn_service.activity.QtalkServiceRNActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by xinbo.wang on 2015/5/15.
 */
public class QRUtil {
    private static final String TAG = QRUtil.class.getSimpleName();
    private static final int QR_HEIGHT = 192;
    private static final int QR_WIDTH = 192;

    public static Bitmap generateQRImageWihtDes(String source, String desc) {
        Bitmap bitmap = generateQRImage(source);
        Canvas canvas = new Canvas();

        return bitmap;
    }


    public static String generateQRBase64(String source){
        return bitmapToBase64(generateQRImage(source));
    }

    public static Bitmap generateQRImage(String source)
    {
        try
        {
            if(TextUtils.isEmpty(source))
                return null;
            Map<EncodeHintType,String> hints = new Hashtable<EncodeHintType,String>();
            hints.put(EncodeHintType.CHARACTER_SET,"utf-8");
            BitMatrix bitMatrix = new QRCodeWriter().encode(source, BarcodeFormat.QR_CODE,QR_WIDTH,QR_HEIGHT,hints);
            int[] pixels = new int[QR_WIDTH*QR_HEIGHT];

            for(int y=0;y<QR_HEIGHT;y++)
            {
                for(int x=0;x<QR_WIDTH;x++)
                {
                    if(bitMatrix.get(x,y))
                    {
                        pixels[y*QR_WIDTH+x] = 0xff000000;
                    }
                    else {
                        pixels[y*QR_WIDTH +x] = 0xffffffff;
                    }
                }
            }

            Bitmap QRBitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            QRBitmap.setPixels(pixels,0,QR_WIDTH,0,0,QR_WIDTH,QR_HEIGHT);
            return QRBitmap;
        } catch (WriterException e) {
            LogUtil.e(TAG,"ERROR",e);
        }
        return null;
    }

    public static String cognitiveQR(Bitmap bitmap)
    {
        if(bitmap==null) return "";
        String decodeStr = "";
        Hashtable<DecodeHintType,Object> hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET,"utf-8");
        hints.put(DecodeHintType.TRY_HARDER,Boolean.TRUE);
        if(bitmap.getWidth()<200)
        {
            Matrix matrix = new Matrix();
            matrix.postScale(2,2);
            bitmap = Bitmap.createBitmap(
                    bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        }
        int[] pixels = new int[bitmap.getWidth()*bitmap.getHeight()];
        bitmap.getPixels(pixels,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
        RGBLuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(),bitmap.getHeight(),pixels);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            Result result = reader.decode(binaryBitmap, hints);
            decodeStr = result.getText();
        } catch (NotFoundException e) {
            Logger.i(TAG + "ERROR=%s", e.getMessage());
        } catch (ChecksumException e) {
            Logger.i(TAG + "ERROR=%s", e.getMessage());
        } catch (FormatException e) {
            Logger.i(TAG + "ERROR=%s", e.getMessage());
        } catch (Exception e) {
            Logger.i(TAG + "ERROR=%s", e.getMessage());
        }
        return decodeStr;
    }

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
                    Intent intent = new Intent(CommonConfig.globalContext, QtalkServiceRNActivity.class);
                    intent.putExtra("UserId", id);
                    intent.putExtra("module", "UserCard");
                    intent.putExtra("Version", "1.0.0");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);


//                    Intent intent =new Intent(context, PersonalInfoActivity.class);
//                    intent.putExtra("jid", id);
//                    context.startActivity(intent);
                }
                else if(uri.getHost().equals("group"))
                {
                    String roomId = QtalkStringUtils.roomId2Jid(uri.getQueryParameter("id"));
                    boolean check = ConnectionUtil.getInstance().checkGroupByJid(roomId);
//                    ChatRoomDataModel dataModel = new ChatRoomDataModel();
//                    ChatRoom chatRoom = new ChatRoom();
//                    chatRoom.setJid(roomId);
//                    chatRoom.setIsJoined(ChatRoom.UNJOINED);
//                    dataModel.selectChatroomById(chatRoom);
                    if (check ) {//如果没有在群里，跳转到群信息

                        Intent intent = new Intent(context, QtalkServiceRNActivity.class);
                        intent.putExtra("module", QtalkServiceRNActivity.GROUPCARD);
                        intent.putExtra("groupId", roomId);
                        intent.putExtra("permissions", ConnectionUtil.getInstance().selectGroupMemberPermissionsByGroupIdAndMemberId(roomId, CurrentPreference.getInstance().getPreferenceUserId()));
                        context.startActivity(intent);


                    } else { //如果已经在群里
//
                        Intent i =new Intent(context, ChatroomInfoActivity.class);
                        i.putExtra("roomId", roomId);
                        // Intent i = ChatActivity_.intent(context).fullname(chatRoom.getName()).isFromChatRoom(true).jid(chatRoom.getJid()).get();
                        context.startActivity(i);
//                        Intent intent = new Intent(context, ChatActivity.class);
//                        intent.putExtra("isFromChatRoom",true);
//                        intent.putExtra("jid", roomId);
//                        context.startActivity(intent);
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


    /**
     * bitmap转为base64
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
