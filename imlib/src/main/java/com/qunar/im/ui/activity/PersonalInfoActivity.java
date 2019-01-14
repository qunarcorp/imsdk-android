package com.qunar.im.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.qunar.im.base.jsonbean.GeneralJson;
import com.qunar.im.base.presenter.IBuddyPresenter;
import com.qunar.im.base.presenter.ICheckFriendPresenter;
import com.qunar.im.base.presenter.IEditMyProfilePresenter;
import com.qunar.im.base.presenter.IPersonalInfoPresenter;
import com.qunar.im.base.presenter.factory.PersonalInfoFactory;
import com.qunar.im.base.presenter.impl.BuddyPresenter;
import com.qunar.im.base.presenter.impl.EditMyProfilePresenter;
import com.qunar.im.base.presenter.views.DeleteBuddyView;
import com.qunar.im.base.presenter.views.ICheckFriendsView;
import com.qunar.im.base.presenter.views.ICommentView;
import com.qunar.im.base.presenter.views.IGravatarView;
import com.qunar.im.base.presenter.views.IMyProfileView;
import com.qunar.im.base.presenter.views.IPersonalInfoView;
import com.qunar.im.base.protocol.OpsAPI;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.ProfileUtils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.permission.PermissionCallback;
import com.qunar.im.permission.PermissionDispatcher;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;
import com.qunar.im.utils.QtalkStringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by xinbo.wang on 2015/3/24.
 */
public class PersonalInfoActivity extends SwipeBackActivity implements IMyProfileView,
        IPersonalInfoView, ICheckFriendsView, IGravatarView, ICommentView, PermissionCallback, View.OnClickListener, OnMenuItemClickListener ,DefaultHardwareBackBtnHandler {
    public static final int REQUEST_GRANT_CAMERA = PermissionDispatcher.getRequestCode();
    public static final int REQUEST_GRANT_LOCAL = PermissionDispatcher.getRequestCode();
    public static final int REQUEST_GRANT_CALL = PermissionDispatcher.getRequestCode();

    public static final String JID = "jid";
    public static final String IS_HIDE_BTN = "isHideBtn";
    public static final String REAL_USER = "realUser";
    public static final String HOT_LINE = "hotLine";

    public static final String TAG = "PersonalInfoActivity";

    //头像
    SimpleDraweeView user_gravatar;
    //名称
    TextView nickname;
    //签名
    TextView signature;
    //用户id
    TextView user_id;
    //部门
    TextView tv_organizational_structure;
    //查看电话
    TextView phonenumberCheck;
    //添加好友
    TextView add_buddy;
    //发送消息
    TextView send_message;
    //评论
    RelativeLayout comment_txt;
    //手机号框
    RelativeLayout phone_layout;
    //rn卡片
    ReactRootView mrn;
    LinearLayout ll_3;

    //备注
    LinearLayout remark_layout;
    TextView remark_text;

    //    SimpleDraweeView user_gravatar;
//    LinearLayout ll_dept, bottom_container, ll_markup_container, ll_bottom;
//    RelativeLayout rl_header;
//    TextView user_id, dept_name, sign;
//    TextView operation_btn, cloud_record_of_chat, dial, tv_markup;
//    TextView my_qrcode;
//    TextView comment_txt;
    ProgressDialog progressDialog;
    HandlePersonalEvent handlePersonalEvent = new HandlePersonalEvent();

    boolean isHideBtn;
    String jid;
    //热线的真实用户
    String realUser;
    String mood;
    //默认为0
    String hotLine ;
    private String fullName;
    boolean isFriend;

    IPersonalInfoPresenter personalInfoPresenter;
    IEditMyProfilePresenter editMyProfilePresenter;
    ReactInstanceManager mReactInstanceManager;

    private String selGravatarPath;
    AlertDialog mDialog;
    String markup;

    String markupName;//备注

    @Override
    public void responsePermission(int requestCode, boolean granted) {
        if (!granted) return;
        if (requestCode == REQUEST_GRANT_CAMERA) {
            changeGravantarFromCamera();
        } else if (requestCode == REQUEST_GRANT_LOCAL) {
            changeGravantarFromLocal();
        } else if (requestCode == REQUEST_GRANT_CALL) {

            OpsAPI.getUserMobilePhoneNumber(CurrentPreference.getInstance().getUserid(), QtalkStringUtils.parseLocalpart(jid), new ProtocolCallback.UnitCallback<GeneralJson>() {
                @Override
                public void onCompleted(final GeneralJson generalJson) {
                    getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            int code;
                            try {
                                code = (int) Double.parseDouble(generalJson.errcode.toString());
                            } catch (Exception e) {
                                LogUtil.e(TAG, "ERROR", e);
                                code = -1;
                            }
                            if (code != 0) {
                                Toast.makeText(PersonalInfoActivity.this, generalJson.msg, Toast.LENGTH_SHORT).show();
                            } else if (generalJson.data == null) {
                                Toast.makeText(PersonalInfoActivity.this, R.string.atom_ui_get_phone_deny, Toast.LENGTH_SHORT).show();
                            } else {
                                Intent mIntent = new Intent(Intent.ACTION_DIAL);
                                mIntent.setData(Uri.parse("tel:" + generalJson.data.get("phone")));
                                startActivity(mIntent);
                            }
                        }
                    });
                }

                @Override
                public void onFailure(String errMsg) {
                    getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PersonalInfoActivity.this, R.string.atom_ui_tip_check_network, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }


    @Override
    public void onCreate(Bundle saveInstancedState) {
        super.onCreate(saveInstancedState);
        setContentView(R.layout.atom_ui_activity_personal_buddy_info);
        bindViews();
        injectExtras(getIntent());
        personalInfoPresenter = PersonalInfoFactory.getPersonalPresenter();
        editMyProfilePresenter = new EditMyProfilePresenter();
        initViews();
        EventBus.getDefault().register(handlePersonalEvent);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        injectExtras(intent);
    }

    private void injectExtras(Intent intent) {

        Bundle extras_ = intent.getExtras();
        if (extras_ != null) {
            if (extras_.containsKey(JID)) {
                jid = extras_.getString(JID);
            }
            if (extras_.containsKey(IS_HIDE_BTN)) {
                isHideBtn = extras_.getBoolean(IS_HIDE_BTN);
            }
            if (extras_.containsKey(REAL_USER)) {
                realUser = extras_.getString(REAL_USER);
            }
            if (extras_.containsKey(HOT_LINE)) {
                hotLine = extras_.getString(HOT_LINE);
            }else{
                hotLine ="0";
            }
        }
    }


    private void bindViews() {

        ll_3 = (LinearLayout) findViewById(R.id.ll_3);
        user_gravatar = (com.facebook.drawee.view.SimpleDraweeView) findViewById(R.id.user_gravatar);
        user_id = (TextView) findViewById(R.id.user_id);
        tv_organizational_structure = (TextView) findViewById(R.id.tv_organizational_structure);
        nickname = (TextView) findViewById(R.id.nickname);
        signature = (TextView) findViewById(R.id.signature);
//        phonenumberCheck = (TextView) findViewById(R.id.phone_number_check);
        add_buddy = (TextView) findViewById(R.id.add_buddy);
        send_message = (TextView) findViewById(R.id.send_message);
        comment_txt = (RelativeLayout) findViewById(R.id.rl_3);
//        phone_layout = (RelativeLayout) findViewById(R.id.phone_layout);
//        mrn = (ReactRootView) findViewById(R.id.mrn);
//        tv_markup.setOnClickListener(this);
        add_buddy.setOnClickListener(this);
        send_message.setOnClickListener(this);
//        phonenumberCheck.setOnClickListener(this);

        remark_layout = (LinearLayout) findViewById(R.id.remark_layout);
        remark_layout.setOnClickListener(this);

        remark_text = (TextView) findViewById(R.id.remark_text);

    }

    void initViews() {

        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        actionBar.setBackgroundResource(R.drawable.atom_ui_gradient_linear_actionbar_selector);
        setNewActionBar(actionBar);
//        actionBar.getLeftButton().setBackground(null);
        personalInfoPresenter.setGravatarView(this);
        personalInfoPresenter.setPersonalInfoView(this);
        personalInfoPresenter.setCommentView(this);
        editMyProfilePresenter.setPersonalInfoView(this);

        markupName = CurrentPreference.getInstance().getMarkupNames().get(jid);

        if (CommonConfig.isQtalk) {

//
//            phone_layout.setVisibility(View.VISIBLE);
        } else {

//            phone_layout.setVisibility(View.GONE);
//
        }
        if (isHideBtn) {
//            send_message.setVisibility(View.GONE);
            add_buddy.setVisibility(View.GONE);
        } else {
            if (!TextUtils.isEmpty(jid)) {
                if (jid.equals(CurrentPreference.getInstance().getPreferenceUserId())) {
                    send_message.setVisibility(View.GONE);
                    add_buddy.setVisibility(View.GONE);
                    comment_txt.setVisibility(View.GONE);
                    remark_layout.setVisibility(View.GONE);
////                CurrentPreference.getInstance().getFullName();
////                CurrentPreference.getInstance().getPreferenceUserId();
//                setSignView();
//                sign.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.atom_ui_edit, 0);
//                isHideBtn = true;
//                comment_txt.setVisibility(View.GONE);
//                cloud_record_of_chat.setVisibility(View.GONE);
//                bottom_container.setVisibility(View.VISIBLE);
//                ll_markup_container.setVisibility(View.GONE);
//                initProgressDialog();
                }

            } else {
//                setActionBarRightIcon(R.string.atom_ui_new_more);
//                final PopupMenu menu = new PopupMenu(PersonalInfoActivity.this, actionBar.getRightIcon());
//                menu.inflate(R.menu.atom_ui_menu_personal);
//                menu.setOnMenuItemClickListener(PersonalInfoActivity.this);
//                setActionBarRightIconClick(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (isFriend) {
//                            menu.getMenu().findItem(R.id.menu_friend_ops).setVisible(false);
//                            menu.getMenu().findItem(R.id.menu_del_friend).setVisible(true);
//                        } else {
//                            menu.getMenu().findItem(R.id.menu_friend_ops).setVisible(true);
//                            menu.getMenu().findItem(R.id.menu_del_friend).setVisible(false);
//                        }
//                        menu.show();
//                    }
//                });
            }


        }
//
//        if (isHideBtn) {
//
//            ll_bottom.setVisibility(View.GONE);
//        } else {
//            final ImageView imageView = new ImageView(this);
//            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//            imageView.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.atom_ui_icon_size)
//                    , getResources().getDimensionPixelSize(R.dimen.atom_ui_icon_size)));
//            imageView.setImageResource(R.drawable.atom_ui_more);
//            actionBar.getRightContainer().addView(imageView);
//
//            imageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (isFriend) {
//                        menu.getMenu().findItem(R.id.menu_friend_ops).setVisible(false);
//                        menu.getMenu().findItem(R.id.menu_del_friend).setVisible(true);
//                    } else {
//                        menu.getMenu().findItem(R.id.menu_friend_ops).setVisible(true);
//                        menu.getMenu().findItem(R.id.menu_del_friend).setVisible(false);
//                    }
//                    menu.show();
//                }
//            });

        user_gravatar.setOnClickListener(this);

        if (!CommonConfig.isQtalk) {
            //隐藏组织架构 隐藏评论 隐藏打电话
//            ll_dept.setVisibility(View.GONE);
//            comment_txt.setVisibility(View.GONE);
//            findViewById(R.id.v_vertical_line).setVisibility(View.GONE);
//            dial.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostPause(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isHideBtn) {//判断是不是好友 或者是在不在组织架构里判断是不是可以发消息逻辑 目前不需要这个判断 都可以发
            ((ICheckFriendPresenter) personalInfoPresenter).checkFriend();
        }
        personalInfoPresenter.loadPersonalInfo();
        editMyProfilePresenter.loadMood();
        remark_text.setText(editMyProfilePresenter.getMarkNames());
        if(QtalkNavicationService.getInstance().getNavConfigResult().ops != null
                && !TextUtils.isEmpty(QtalkNavicationService.getInstance().getNavConfigResult().ops.host)
                && !TextUtils.isEmpty(QtalkNavicationService.getInstance().getNavConfigResult().ops.checkversion)
                && !TextUtils.isEmpty(QtalkNavicationService.getInstance().getNavConfigResult().ops.conf)){
            //TODO 暂时注释掉
//            Bundle bundle = getDefaultBundle();
//            mReactInstanceManager = QtalkCardRNViewInstanceManager.getInstanceManager(getApplication());
//            if(mrn!=null){
//                mrn.unmountReactApplication();
//            }
//            mrn = new ReactRootView(this);
//            mrn.startReactApplication(mReactInstanceManager,QtalkCardRNViewInstanceManager.MODULE,bundle);
//            ll_3.removeAllViews();
//            ll_3.addView(mrn);
//            if (mReactInstanceManager != null) {
//                mReactInstanceManager.onHostResume(this, this);
//            }
        }else{
            ll_3.setVisibility(View.GONE);
        }
    }

    void restartChat() {
        Intent intent = new Intent(this, PbChatActivity.class);
        intent.putExtra("jid", jid);
        intent.putExtra("isFromChatRoom", false);
        intent.putExtra("realJid", TextUtils.isEmpty(realUser)?jid:realUser);
        intent.putExtra("chatType", hotLine);
        startActivity(intent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    //更改群名称
    void markupName() {
        if (TextUtils.isEmpty(jid)) return;
        View contentView = LayoutInflater.from(this).inflate(R.layout.atom_ui_dialog_change_group_name, null);
        final EditText et = (EditText) contentView.findViewById(R.id.et_group_name);
        et.setText(markup);
        new AlertDialog.Builder(this)
                .setTitle(R.string.atom_ui_common_markup)
                .setView(contentView)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (et.getText().length() > 10) {
                            Toast.makeText(PersonalInfoActivity.this, R.string.atom_ui_tip_remark_toolong, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (TextUtils.isEmpty(et.getText())) {
                            Toast.makeText(PersonalInfoActivity.this, R.string.atom_ui_tip_remark_null, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String markup = et.getText().toString();
                        ProfileUtils.markupName(markup, jid);
//                        tv_markup.setText(markup);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    //更改备注
    void markupPersonalName() {
        if (TextUtils.isEmpty(jid)) {return;}
        final EditText et = new EditText(this);
        et.setText(markupName);
        new AlertDialog.Builder(this)
                .setTitle(R.string.atom_ui_common_markup)
                .setView(et)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (et.getText().length() > 10) {
                            Toast.makeText(PersonalInfoActivity.this, R.string.atom_ui_tip_remark_toolong, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (TextUtils.isEmpty(et.getText())) {
                            Toast.makeText(PersonalInfoActivity.this, R.string.atom_ui_tip_remark_null, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        markupName = et.getText().toString();
                        editMyProfilePresenter.updateMarkupName();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }


    void my_qrcode() {
        if (!TextUtils.isEmpty(jid)) {
            Intent intent = new Intent(this, QRActivity.class);
            intent.putExtra("qrString", Constants.Config.QR_SCHEMA + "://user?id=" + jid);
            startActivity(intent);
        }
    }

    void cloud_record_of_chat() {
        if (!TextUtils.isEmpty(fullName)) {
            Intent intent = new Intent(this, CloudChatRecordActivity.class);
            intent.putExtra("fullName", fullName);
            intent.putExtra("isFromGroup", false);
            intent.putExtra("toId", jid);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(handlePersonalEvent);
        super.onDestroy();
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostDestroy();
        }
        if(mrn != null){
            mrn.unmountReactApplication();
        }
    }

    @Override
    public void onBackPressed() {
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void setDeptName(final String deptName) {
        if (TextUtils.isEmpty(deptName)) return;
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                tv_organizational_structure.setText(deptName);
//                dept_name.setText(deptName);
            }
        });
    }

    @Override
    public void setJid(final String jid) {
        if (TextUtils.isEmpty(jid)) return;
        this.jid = jid;
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                user_id.setText(QtalkStringUtils.parseLocalpart(jid));
            }
        });
    }

    @Override
    public SimpleDraweeView getImagetView() {
        return user_gravatar;
    }

    @Override
    public void setNickName(final String nick) {
        if (TextUtils.isEmpty(nick)) return;
        fullName = nick;
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                nickname.setText(nick);
//                myActionBar.getTitleTextview().setText(nick);
                setActionBarTitle(nick);
            }
        });
    }

    @Override
    public String getJid() {
        return jid;
    }

    @Override
    public String getMood() {
        return mood;
    }

    @Override
    public void setMood(final String m) {

        getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(m)) {
                    signature.setText(R.string.atom_ui_tip_no_sign);

                }else {
                    signature.setText(Html.fromHtml(m));
                }
            }
        });
    }

    @Override
    public String getMarkup() {
        return markupName;
    }


    @Override
    public void setMarkup(final boolean isScuess) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(PersonalInfoActivity.this,isScuess ? R.string.atom_ui_tip_save_success :
                        R.string.atom_ui_tip_operation_failed ,Toast.LENGTH_SHORT).show();
                if(isScuess)
                    remark_text.setText(markupName);
            }
        });
    }

    @Override
    public void setUpdateResult(final boolean result) {
        if (CommonConfig.isQtalk) {
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    selGravatarPath = "";
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    if (result) {
                        personalInfoPresenter.loadGravatar(true);
                        EventBus.getDefault().post(new EventBusEvent.GravantarChanged());
                    }
                    Toast.makeText(PersonalInfoActivity.this, result ? getString(R.string.atom_ui_tip_gravantar_update_success) : getString(R.string.atom_ui_tip_gravantar_update_failure), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void setLargeGravatarInfo(String url, String thumbPath) {
        Intent intent = new Intent(this, ImageBrowersingActivity.class);
        intent.putExtra(Constants.BundleKey.IMAGE_URL, url);
        intent.putExtra(Constants.BundleKey.IMAGE_ON_LOADING, thumbPath);
        int location[] = new int[2];
        user_gravatar.getLocationOnScreen(location);
        intent.putExtra("left", location[0]);
        intent.putExtra("top", location[1]);
        intent.putExtra("height", user_gravatar.getHeight());
        intent.putExtra("width", user_gravatar.getWidth());
        startActivity(intent);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public String getGravatarPath() {
        return selGravatarPath;
    }

    @Override
    public void setCommentUrl(final String url, final String uid) {
        if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(uid)) {
            comment_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String commentUrl = url + "?u=" + CurrentPreference.getInstance().getUserid() + "&k=" + CommonConfig.verifyKey + "&t=" + uid;
                    Intent intent = new Intent(PersonalInfoActivity.this, QunarWebActvity.class);
                    intent.setData(Uri.parse(commentUrl));
                    PersonalInfoActivity.this.startActivity(intent);
                }
            });
        }
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.atom_ui_title_update_avater);
        progressDialog.setMessage(getString(R.string.atom_ui_tip_updating));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private void changeGravantarFromLocal() {
        Intent intentPic = new Intent(this, PictureSelectorActivity.class);
        intentPic.putExtra("isGravantarSel", true);
        intentPic.putExtra("isMultiSel", false);
        startActivity(intentPic);
    }

    private void changeGravantarFromCamera() {
        Intent intentCamera = new Intent(this, ImageClipActivity.class);
        startActivity(intentCamera);
    }

    public void showAlertDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(PersonalInfoActivity.this);
//        View view = LayoutInflater.from(PersonalInfoActivity.this).inflate(R.layout.atom_ui_dialog_change_gravatar, rl_header, false);
//        TextView tv_check_large_gravtar = (TextView) view.findViewById(R.id.tv_check_large_gravtar);
//        TextView tv_change_gravtar_photos = (TextView) view.findViewById(R.id.tv_change_gravtar_photos);
//        TextView tv_change_gravtar_camera = (TextView) view.findViewById(R.id.tv_change_gravtar_camera);
//        TextView tv_change_gravatar_prompt = (TextView) view.findViewById(R.id.tv_change_gravtar_prompt);
//        tv_check_large_gravtar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                personalInfoPresenter.showLargeGravatar();
//                if (mDialog != null && mDialog.isShowing()) {
//                    mDialog.dismiss();
//                }
//            }
//        });
//        if(CommonConfig.isQtalk) {
//            tv_change_gravtar_photos.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    PermissionDispatcher.requestPermissionWithCheck(PersonalInfoActivity.this, new int[]{PermissionDispatcher.REQUEST_WRITE_EXTERNAL_STORAGE,
//                                    PermissionDispatcher.REQUEST_READ_EXTERNAL_STORAGE},
//                            PersonalInfoActivity.this, REQUEST_GRANT_LOCAL);
//                    if (mDialog != null && mDialog.isShowing()) {
//                        mDialog.dismiss();
//                    }
//                }
//            });
//            tv_change_gravtar_camera.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    PermissionDispatcher.requestPermissionWithCheck(PersonalInfoActivity.this, new int[]{PermissionDispatcher.REQUEST_CAMERA},
//                            PersonalInfoActivity.this, REQUEST_GRANT_CAMERA);
//                    if (mDialog != null && mDialog.isShowing()) {
//                        mDialog.dismiss();
//                    }
//                }
//            });
//        }
//        else {
//            tv_change_gravtar_photos.setVisibility(View.GONE);
//            tv_change_gravtar_camera.setVisibility(View.GONE);
//            tv_change_gravatar_prompt.setVisibility(View.VISIBLE);
//        }
//
//        builder.setView(view);
//        mDialog = builder.show();
//        mDialog.setCanceledOnTouchOutside(true);
    }

    @Override
    public String getCheckedUserId() {

        return jid;
    }


    @Override
    public void setCheckResult(boolean isFriend) {

        if (!TextUtils.isEmpty(jid)) {
            if (jid.equals(CurrentPreference.getInstance().getPreferenceUserId())) {
                send_message.setVisibility(View.GONE);
                add_buddy.setVisibility(View.GONE);
            }
        } else {
            send_message.setVisibility(View.VISIBLE);
            add_buddy.setVisibility(View.VISIBLE);

        }
        if (isFriend) {

            add_buddy.setBackgroundResource(R.drawable.atom_ui_common_button_red_select);
            add_buddy.setText(R.string.atom_ui_delete_friend);
            add_buddy.setTextColor(ContextCompat.getColor(this,R.color.atom_ui_white));
            add_buddy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(jid)) {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(PersonalInfoActivity.this);
                        commonDialog.setMessage(R.string.atom_ui_prompt_del_friend);
                        commonDialog.setPositiveButton(R.string.atom_ui_common_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                IBuddyPresenter buddyPresenter = new BuddyPresenter();
                                buddyPresenter.setBuddyView(new DeleteBuddyView() {
                                    @Override
                                    public String getTargetId() {
                                        return jid;
                                    }
                                });
                                buddyPresenter.deleteBuddy();
                                Toast.makeText(PersonalInfoActivity.this, R.string.atom_ui_tip_friend_deleted, Toast.LENGTH_SHORT).show();
                                PersonalInfoActivity.this.finish();
                            }
                        });
                        commonDialog.setNegativeButton(R.string.atom_ui_common_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        commonDialog.show();
                    }
                }
            });
        } else {
            add_buddy.setBackgroundResource(R.drawable.atom_ui_common_button_white_select);
            add_buddy.setText(R.string.atom_ui_title_add_buddy);
            add_buddy.setTextColor(ContextCompat.getColor(this,R.color.atom_ui_light_gray_33));
            add_buddy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PersonalInfoActivity.this, AddAuthMessageActivity.class);
                    intent.putExtra("jid", jid);
                    startActivity(intent);
                    finish();
                }
            });
        }
        this.isFriend = isFriend;
    }

//    if (item.getItemId() == R.id.menu_friend_ops) {
//        Intent intent = new Intent(PersonalInfoActivity.this, AddAuthMessageActivity.class);
//        intent.putExtra("jid", jid);
//        startActivity(intent);
//        finish();
//    } else if (item.getItemId() == R.id.menu_del_friend) {
//        if (!TextUtils.isEmpty(jid)) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setMessage(R.string.atom_ui_prompt_del_friend);
//            builder.setPositiveButton(R.string.atom_ui_common_confirm, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                    IBuddyPresenter buddyPresenter = new BuddyPresenter();
//                    buddyPresenter.setBuddyView(new DeleteBuddyView() {
//                        @Override
//                        public String getTargetId() {
//                            return jid;
//                        }
//                    });
//                    buddyPresenter.deleteBuddy();
//                    Toast.makeText(PersonalInfoActivity.this, R.string.tip_friend_deleted, Toast.LENGTH_SHORT).show();
//                }
//            });
//            builder.setNegativeButton(R.string.atom_ui_common_cancel, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });
//            builder.show();
//        }
//    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.my_qrcode) {
            my_qrcode();

        } else if (i == R.id.cloud_record_of_chat) {
            cloud_record_of_chat();
        } else if (i == R.id.tv_markup) {
            markupName();
        } else if (i == R.id.operation_btn) {
            if (!TextUtils.isEmpty(jid)) {
                restartChat();
            }
        } else if (i == R.id.send_message) {
            if (!TextUtils.isEmpty(jid)) {
                restartChat();
            }
        } else if (i == R.id.user_gravatar) {
            if (!TextUtils.isEmpty(jid)) {
                if (jid.equals(CurrentPreference.getInstance().getPreferenceUserId())) {
                    showAlertDialog();
                } else {
                    personalInfoPresenter.showLargeGravatar();
                }
            }
        }else if(i == R.id.remark_layout){
            markupPersonalName();
        }
//        else if (i == R.id.phone_number_check) {
//            PermissionDispatcher.requestPermissionWithCheck(PersonalInfoActivity.this, new int[]{PermissionDispatcher.REQUEST_CALL},
//                    PersonalInfoActivity.this, REQUEST_GRANT_CALL);
//        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.menu_friend_ops) {
            Intent intent = new Intent(PersonalInfoActivity.this, AddAuthMessageActivity.class);
            intent.putExtra("jid", jid);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.menu_del_friend) {
            if (!TextUtils.isEmpty(jid)) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                commonDialog.setMessage(R.string.atom_ui_prompt_del_friend);
                commonDialog.setPositiveButton(R.string.atom_ui_common_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        IBuddyPresenter buddyPresenter = new BuddyPresenter();
                        buddyPresenter.setBuddyView(new DeleteBuddyView() {
                            @Override
                            public String getTargetId() {
                                return jid;
                            }
                        });
                        buddyPresenter.deleteBuddy();
                        Toast.makeText(PersonalInfoActivity.this, R.string.atom_ui_tip_friend_deleted, Toast.LENGTH_SHORT).show();
                        PersonalInfoActivity.this.finish();
                    }
                });
                commonDialog.setNegativeButton(R.string.atom_ui_common_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                commonDialog.show();
            }
        }
        return true;
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        super.onBackPressed();
    }

    class HandlePersonalEvent {
        public void onEventMainThread(final EventBusEvent.GravtarGot gravtarGot) {
//            if(gravtarGot.jid!=null&&gravtarGot.jid.equals(jid)) {
//                BackgroundExecutor.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        try{
//                            File imageFile = Glide.with(PersonalInfoActivity.this)
//                                    .load(gravtarGot.murl)
//                                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                                    .get();
//                            final Bitmap gravantarBg = BitmapHelper.decodeFile(imageFile.getAbsolutePath());
//                            if(gravantarBg!=null&&!gravantarBg.isRecycled()){
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        ImageUtils.blur(gravantarBg, rl_header);
//                                        gravantarBg.recycle();
//                                    }
//                                });
//                            }
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//                    }
//                });
//
//
//            }
        }

        public void onEventMainThread(EventBusEvent.GravanterSelected selectedFile) {
            File tempFile = selectedFile.selectedFile;
            if (tempFile != null && tempFile.exists()) {
                progressDialog.show();
                selGravatarPath = tempFile.getPath();
                personalInfoPresenter.updateMyPersonalInfo();
            }
        }

    }

    public Bundle getDefaultBundle() {
        Bundle bundle = new Bundle();
        //TODO server prefix
        Map<String,String> map = new HashMap<>();
        map.put("user_id",CurrentPreference.getInstance().getUserid());
        map.put("qtalk_id",QtalkStringUtils.parseId(jid));
        map.put("domain","@"+QtalkNavicationService.getInstance().getXmppdomain());
        bundle.putString("user_id", CurrentPreference.getInstance().getUserid());
        bundle.putString("qtalk_id", QtalkStringUtils.parseId(jid));
        bundle.putString("domain", "@"+QtalkNavicationService.getInstance().getXmppdomain());
//        bundle.putString("server", QtalkNavicationService.getInstance().getSimpleapiurl());

        return bundle;
    }
}
