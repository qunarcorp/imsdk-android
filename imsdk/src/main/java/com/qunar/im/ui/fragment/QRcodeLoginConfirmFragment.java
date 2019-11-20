package com.qunar.im.ui.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.permission.PermissionDispatcher;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.jsonbean.QRCodeAuthResultJson;
import com.qunar.im.base.jsonbean.QVTResponseResult;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.protocol.LoginAPI;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.utils.GlobalConfigManager;
import com.qunar.im.ui.R;
import com.qunar.im.ui.entity.AuthData;

/**
 * 二维码扫描登录确认页面
 * Created by hubo.hu on 2017/8/31.
 */

public class QRcodeLoginConfirmFragment extends DialogFragment implements View.OnClickListener {
    private static final int SCAN_REQUEST = PermissionDispatcher.getRequestCode();

    private TextView mCloseView, mCancelView, mConfirmView;

    private String qrcodekey;

    ProgressDialog progressDialog;
    Bundle b;
    String loginType;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.atom_ui_activity_qrcodelogin_comfirm_dialog, null);
        mCloseView = (TextView) view.findViewById(R.id.qrcodelogin_dialog_close);
        mCancelView = (TextView) view.findViewById(R.id.qrcodelogin_dialog_cancel);
        mConfirmView = (TextView) view.findViewById(R.id.qrcodelogin_dialog_confirm);
        TextView tip = (TextView) view.findViewById(R.id.qrcodelogin_dialog_tip);

        mCloseView.setOnClickListener(this);
        mCancelView.setOnClickListener(this);
        mConfirmView.setOnClickListener(this);
//        builder.setView(view);

        final Dialog dialog = new Dialog(getActivity(), R.style.style_dialog);
        dialog.setContentView(view);
        dialog.show();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;   //设置宽度充满屏幕
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        b = getArguments();
        loginType = b.getString("type");
        if(!TextUtils.isEmpty(loginType) && "wiki".equals(loginType.toLowerCase())){
            tip.setText(getString(R.string.atom_ui_tip_qrcodelogin_confirm, "Wiki"));
        }else {
            tip.setText(getString(R.string.atom_ui_tip_qrcodelogin_confirm, CommonConfig.currentPlat));
        }
        authData(1, 1);

        return dialog;//builder.create();
    }

    /**
     * 验证信息
     * @param phase  1：已扫码  2：已确认认证或者取消认证
     */
    private void authData(final int phase, final int type){
        qrcodekey = b.getString("qrcodekey");
//        UserVCard vCard = ProfileUtils.getLocalVCard(QtalkStringUtils.userId2Jid(CurrentPreference.getInstance().getUserId()));
        Nick nick = ConnectionUtil.getInstance().getMyselfCard(CurrentPreference.getInstance().getPreferenceUserId());
        AuthData ad = new AuthData();
        ad.v = "1.0";
        ad.t = type + "";
        ad.p = GlobalConfigManager.getAppName();
        if(phase == 1){
            ad.a = nick.getHeaderSrc();// vCard.gravantarUrl;
            ad.u = nick.getName();// vCard.nickname;
        }else if(phase == 2){
            AuthData.Data d = new AuthData.Data();
            if(CommonConfig.isQtalk){
                d.q_ckey = Protocol.getCKEY();//qtalk默认传ckey
//                if(!TextUtils.isEmpty(loginType) && "wiki".equals(loginType.toLowerCase())){//登录wiki
//                    d.q_ckey = Protocol.getCKEY();
//                }else {
//                    if(!QtalkNavicationService.getInstance().getXmppdomain().equals("ejabhost1")){//暂时判断qtalk公共域
//                        d.q_ckey = Protocol.getCKEY();
//                    }else{
//                        Toast.makeText(getActivity(), "暂不支持qtalk扫码登录", Toast.LENGTH_LONG).show();
//                        return;
//                    }
//                }
                ad.d = d;
            }else{
                String json = CurrentPreference.getInstance().getQvt();
                QVTResponseResult qvtResponseResult = JsonUtils.getGson().fromJson(json, QVTResponseResult.class);
                if(type == 1){
                    d.q = qvtResponseResult.data.qcookie;
                    d.v = qvtResponseResult.data.vcookie;
                    d.t = qvtResponseResult.data.tcookie;
                }
                ad.d = d;
            }
        }
        String authdata = JsonUtils.getGson().toJson(ad);
        Logger.i("authdata:" + authdata);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.atom_ui_tip_validating));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
//                dismiss();
            }
        });
        progressDialog.show();
        LoginAPI.QChatQRLoginAuth(qrcodekey, authdata, phase, new ProtocolCallback.UnitCallback<QRCodeAuthResultJson>() {
            @Override
            public void onCompleted(final QRCodeAuthResultJson qChatLoginResult) {
                CommonConfig.mainhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Logger.i("QRcodeLoginConfirmFragment  Success  resultString = " + qChatLoginResult);
                        if (progressDialog != null && progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        if(phase == 2 && type == 1){
                            Toast.makeText(getActivity(), getString(R.string.atom_ui_tip_login_successful), Toast.LENGTH_LONG).show();
                            dismiss();
                            return;
                        }else if(phase == 2 && type == 4){
                            Toast.makeText(getActivity(), R.string.atom_ui_tip_login_cancel, Toast.LENGTH_LONG).show();
                            dismiss();
                            return;
                        }
                        Toast.makeText(getActivity(), R.string.atom_ui_tip_verify_success, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(String errMsg) {
                CommonConfig.mainhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog != null && progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        Toast.makeText(getActivity(), R.string.atom_ui_tip_verify_failed, Toast.LENGTH_LONG).show();
                        dismiss();
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.qrcodelogin_dialog_close || i == R.id.qrcodelogin_dialog_cancel) {
            authData(2, 4);
        }else if(i == R.id.qrcodelogin_dialog_confirm){//确认
            authData(2, 1);
        }
    }
}