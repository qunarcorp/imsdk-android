package com.qunar.im.ui.view.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.base.protocol.HttpRequestCallback;
import com.qunar.im.base.protocol.PayApi;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.util.Constants;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.core.manager.IMPayManager;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.PersonalInfoActivity;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.utils.ConnectionUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by froyomu on 2019-08-28
 * <p>
 * Describe:拆红包dialog
 */
public class OpenRedEnvelopDialog {

    private AlertDialog dialog;

    private TextView status_text;

    private ImageView button;

    private String fromId;

    private String rid;

    private String xmppid;

    private Context context;

    public OpenRedEnvelopDialog(Context context,String rid,String xmppid,String fromId) {
        this.context = context;
        this.fromId = fromId;
        this.rid = rid;
        this.xmppid = xmppid;
        init();
    }

    private void init(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.RedEnvlopeDialog);
        View content = LayoutInflater.from(context).inflate(R.layout.atom_ui_dialog_open_red_envelop, null, false);
        SimpleDraweeView headerView = content.findViewById(R.id.atom_ui_envelop_user_icon);
        TextView userName = content.findViewById(R.id.atom_ui_envelop_user_name);
        status_text = content.findViewById(R.id.atom_ui_envelop_status);
        content.findViewById(R.id.atom_ui_envelop_img_dialog_close).setOnClickListener((view) -> {
            if(dialog != null && dialog.isShowing()){
                dialog.dismiss();
            }
        });
        button = content.findViewById(R.id.atom_ui_envelop_click);
        button.setOnClickListener((v) -> {
            grap();
        });
        ConnectionUtil.getInstance().getUserCard(fromId, (nick)-> {
            userName.setText(nick.getShowName() + "的红包");
            ProfileUtils.displayGravatarByImageSrc((Activity) context, nick.getHeaderSrc(), headerView,50,50);
        },false,false);
        builder.setView(content);
        dialog = builder.create();
    }

    private boolean isChatRoom(){
        return !TextUtils.isEmpty(xmppid) && xmppid.contains("@conference");
    }

    private void grap(){
        PayApi.red_envelope_grap(xmppid, rid, isChatRoom(), new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response) {
                try{
                    String resultString = Protocol.parseStream(response);
                    if(!TextUtils.isEmpty(resultString)){
                        JSONObject result = new JSONObject(resultString);
                        if(result != null && result.optInt("error_code") == 200){
                            IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.PAY_RED_ENVELOP_CHOICE, Constants.Alipay.RED_ENVELOP_DETAIL,rid);
                            dismiss();
                        }else {
                            toast(context.getString(R.string.atom_ui_fail_to_open));
                            dismiss();
                        }
                    }
                }catch (Exception e){
                    toast(context.getString(R.string.atom_ui_fail_to_open));
                    dismiss();
                }
            }

            @Override
            public void onFailure(Exception e) {
                toast(context.getString(R.string.atom_ui_fail_to_open));
                dismiss();
            }
        });
    }

    public void open(){
        PayApi.red_envelope_open(xmppid, rid, isChatRoom(), new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response){
                try{
                    String resultString = Protocol.parseStream(response);
                    if(!TextUtils.isEmpty(resultString)){
                        JSONObject result = new JSONObject(resultString);
                        if(result != null){
                            if(result.optInt("ret") == 1){
                                JSONObject status =  result.optJSONObject("data").optJSONObject("status");
                                show(status);
                            }else {
                                int errCode = result.optInt("error_code");
                                if(errCode == 4300){//未绑定账户
                                    showBindAccoutDialog();
                                }else {
                                    toast(context.getString(R.string.atom_ui_fail_enve_status));
                                }
                            }
                        }else {
                            toast(context.getString(R.string.atom_ui_fail_enve_status));
                        }
                    }
                }catch (Exception e){
                    toast(context.getString(R.string.atom_ui_fail_enve_status));
                }
            }

            @Override
            public void onFailure(Exception e) {
                toast(context.getString(R.string.atom_ui_fail_enve_status));
            }
        });
    }

    private void dismiss(){
        CommonConfig.mainhandler.post(() -> {
            if(dialog != null && dialog.isShowing()){
                dialog.dismiss();
            }
        });
    }

    private void show(JSONObject status){
        boolean has_power = status.optBoolean("has_power");//是否可以拆红包
        boolean is_expired = status.optBoolean("is_expired");//是否过期
        boolean is_grab = status.optBoolean("is_grab");//是否拆过
        boolean is_out = status.optBoolean("is_out");//是否已抢光
        boolean today_has_power = status.optBoolean("today_has_power");
        CommonConfig.mainhandler.post(() -> {
            button.setVisibility(has_power ? View.VISIBLE : View.GONE);
            if(has_power){
                dialog.show();
            }else if(is_expired){
                status_text.setVisibility(View.VISIBLE);
                status_text.setText("红包已过期");
                dialog.show();
            }else if(is_grab){//查看详情
                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.PAY_RED_ENVELOP_CHOICE, Constants.Alipay.RED_ENVELOP_DETAIL,rid);
            }else if(is_out){
                status_text.setVisibility(View.VISIBLE);
                status_text.setText("红包已被抢光，查看详情>>");
                status_text.setOnClickListener((v) -> {
                    if(dialog != null && dialog.isShowing())
                        dialog.dismiss();

                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.PAY_RED_ENVELOP_CHOICE, Constants.Alipay.RED_ENVELOP_DETAIL,rid);
                });
                dialog.show();
            }else if(today_has_power){
                toast("今日抢同一发送用户红包超限");
            }
        });
    }

    private void toast(String msg){
        CommonConfig.mainhandler.post(() -> {
            Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
        });
    }

    private void showBindAccoutDialog(){
        CommonConfig.mainhandler.post(()->{
            new AlertDialog.Builder(context)
                    .setTitle(R.string.atom_ui_common_prompt)
                    .setMessage(R.string.atom_ui_has_not_bind_alipay)
                    .setPositiveButton(android.R.string.yes, (dialog,which)->{
                        IMPayManager.getInstance().getAlipayLoginParams();
                    })
                    .setNegativeButton(android.R.string.no, (dialog,which)->{

                    })
                    .show();
        });

    }
}
