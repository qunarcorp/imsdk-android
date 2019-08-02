package com.qunar.im.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.base.jsonbean.GeneralJson;
import com.qunar.im.permission.PermissionCallback;
import com.qunar.im.permission.PermissionDispatcher;
import com.qunar.im.ui.presenter.IBuddyPresenter;
import com.qunar.im.ui.presenter.ICheckFriendPresenter;
import com.qunar.im.ui.presenter.IEditMyProfilePresenter;
import com.qunar.im.ui.presenter.IPersonalInfoPresenter;
import com.qunar.im.ui.presenter.factory.PersonalInfoFactory;
import com.qunar.im.ui.presenter.impl.BuddyPresenter;
import com.qunar.im.ui.presenter.impl.EditMyProfilePresenter;
import com.qunar.im.ui.presenter.views.DeleteBuddyView;
import com.qunar.im.ui.presenter.views.ICheckFriendsView;
import com.qunar.im.base.common.ICommentView;
import com.qunar.im.ui.presenter.views.IGravatarView;
import com.qunar.im.ui.presenter.views.IMyProfileView;
import com.qunar.im.ui.presenter.views.IPersonalInfoView;
import com.qunar.im.base.protocol.OpsAPI;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.Utils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;
import com.qunar.im.utils.QtalkStringUtils;

import java.io.File;

import de.greenrobot.event.EventBus;

/**
 * Created by hubin on 2017/12/26.
 */

public class PersonalInfoMyActivity extends SwipeBackActivity implements IMyProfileView,
        IPersonalInfoView, ICheckFriendsView, IGravatarView, ICommentView, PermissionCallback, View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    public static final int REQUEST_GRANT_CAMERA = PermissionDispatcher.getRequestCode();
    public static final int REQUEST_GRANT_LOCAL = PermissionDispatcher.getRequestCode();
    public static final int REQUEST_GRANT_CALL = PermissionDispatcher.getRequestCode();

    public static final String JID = "jid";
    public static final String IS_HIDE_BTN = "isHideBtn";
    public static final String REAL_USER = "realUser";
    public static final String HOT_LINE = "hotLine";

    public static final String TAG = "PersonalInfoMyActivity";

    SimpleDraweeView user_gravatar;
    RelativeLayout rl_sign;
    RelativeLayout rl_qr;
    TextView user_name;
    TextView user_id;
    TextView tv_organizational_structure;
    TextView sign;
    RelativeLayout rl_header;

    ProgressDialog progressDialog;
    PersonalInfoMyActivity.HandlePersonalEvent handlePersonalEvent = new PersonalInfoMyActivity.HandlePersonalEvent();

    boolean isHideBtn;
    String jid;
    //热线的真实用户
    String realUser;
    String mood;
    String hotLine;
    private String fullName;
    boolean isFriend;

    IPersonalInfoPresenter personalInfoPresenter;
    IEditMyProfilePresenter editMyProfilePresenter;

    private String selGravatarPath;
    AlertDialog mDialog;
    String markup;

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
                                Toast.makeText(PersonalInfoMyActivity.this, generalJson.msg, Toast.LENGTH_SHORT).show();
                            } else if (generalJson.data == null) {
                                Toast.makeText(PersonalInfoMyActivity.this, R.string.atom_ui_get_phone_deny, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(PersonalInfoMyActivity.this, R.string.atom_ui_tip_check_network, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }


    @Override
    public void onCreate(Bundle saveInstancedState) {
        super.onCreate(saveInstancedState);
        setContentView(R.layout.atom_ui_activity_personal_my_info);
        bindViews();
        injectExtras();
        personalInfoPresenter = PersonalInfoFactory.getPersonalPresenter();
        editMyProfilePresenter = new EditMyProfilePresenter();
        initViews();
        EventBus.getDefault().register(handlePersonalEvent);
    }

    private void injectExtras() {

        Bundle extras_ = getIntent().getExtras();
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
            }
        }
    }


    private void bindViews() {
        user_gravatar = (com.facebook.drawee.view.SimpleDraweeView) findViewById(R.id.user_gravatar);
        user_id = (TextView) findViewById(R.id.user_id);
        tv_organizational_structure = (TextView) findViewById(R.id.tv_organizational_structure);
        rl_sign = (RelativeLayout) findViewById(R.id.rl_sign);
        rl_qr = (RelativeLayout) findViewById(R.id.rl_qr);
        user_name = (TextView) findViewById(R.id.user_name);
        sign = (TextView) findViewById(R.id.sign);

        rl_header = (RelativeLayout) findViewById(R.id.rl_header);
//
        rl_sign.setOnClickListener(this);
        rl_qr.setOnClickListener(this);
        rl_header.setOnClickListener(this);
        setSignView();

    }

    void initViews() {

        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
//        actionBar.setBackgroundResource(R.drawable.atom_ui_gradient_linear_actionbar_selector);
        setNewActionBar(actionBar);
        setActionBarTitle(R.string.atom_ui_title_profile);
        setActionBarLeftText(R.string.atom_ui_tab_mine);
//        actionBar.getLeftButton().setBackground(null);
        personalInfoPresenter.setGravatarView(this);
        personalInfoPresenter.setPersonalInfoView(this);
        personalInfoPresenter.setCommentView(this);
        editMyProfilePresenter.setPersonalInfoView(this);

        initProgressDialog();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isHideBtn) {//判断是不是好友 或者是在不在组织架构里判断是不是可以发消息逻辑 目前不需要这个判断 都可以发
            ((ICheckFriendPresenter) personalInfoPresenter).checkFriend();
        }
    }

    //初始化编辑签名的Dialog
    private void setSignView() {
        rl_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(PersonalInfoMyActivity.this);
                editText.setContentDescription("sign_dialog_edit");//为了自动化测试
                editText.setGravity(Gravity.CENTER_VERTICAL);
                editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dipToPixels(PersonalInfoMyActivity.this,48)));
                Dialog moodDialog = new AlertDialog.Builder(PersonalInfoMyActivity.this)
                        .setTitle(R.string.atom_ui_tip_input_signature)
                        .setView(editText)
                        .setPositiveButton(R.string.atom_ui_common_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String tmpMood  = editText.getText().toString();
                                if(!TextUtils.isEmpty(tmpMood)&&!tmpMood.equals(mood)) {
                                    mood = tmpMood;
                                    editMyProfilePresenter.updateMood();
                                }
                                dialog.dismiss();
                            }

                        })
                        .setNegativeButton(R.string.atom_ui_common_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mood = null;
                                dialog.dismiss();
                            }
                        })
                        .create();

                editText.setText(sign.getText());
                editText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText.selectAll();
                        editText.requestFocusFromTouch();
                        editText.requestFocus();
                    }
                });
                moodDialog.show();
            }
        });
    }

    void restartChat() {
//        EventBus.getDefault().post(new EventBusEvent.restartChat());
        Intent intent = new Intent(this, PbChatActivity.class);
        intent.putExtra("jid", jid);
        intent.putExtra("isFromChatRoom", false);
        intent.putExtra("realJid", realUser);
        intent.putExtra("chatType", hotLine);
//        intent.putExtra("newChat",true);
        startActivity(intent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        personalInfoPresenter.loadPersonalInfo();
        editMyProfilePresenter.loadMood();
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
                            Toast.makeText(PersonalInfoMyActivity.this, R.string.atom_ui_tip_remark_toolong, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (TextUtils.isEmpty(et.getText())) {
                            Toast.makeText(PersonalInfoMyActivity.this, R.string.atom_ui_tip_remark_null, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String markup = et.getText().toString();
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
                user_name.setText(nick);
//                myActionBar.getTitleTextview().setText(nick);

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
        if (TextUtils.isEmpty(m)) {
            return;
        }
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                sign.setText(m);
//                signature.setText(m);
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
                    Toast.makeText(PersonalInfoMyActivity.this, result ? getString(R.string.atom_ui_tip_gravantar_update_success) : getString(R.string.atom_ui_tip_gravantar_update_failure), Toast.LENGTH_LONG).show();
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
//        if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(uid)) {
//            comment_txt.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String commentUrl = url + "?u=" + CurrentPreference.getInstance().getUserid() + "&k=" + CommonConfig.verifyKey + "&t=" + uid;
//                    Intent intent = new Intent(PersonalInfoMyActivity.this, QunarWebActvity.class);
//                    intent.setData(Uri.parse(commentUrl));
//                    PersonalInfoMyActivity.this.startActivity(intent);
//                }
//            });
//        }
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
        AlertDialog.Builder builder = new AlertDialog.Builder(PersonalInfoMyActivity.this);
        View view = LayoutInflater.from(PersonalInfoMyActivity.this).inflate(R.layout.atom_ui_dialog_change_gravatar, null);
        TextView tv_check_large_gravtar = (TextView) view.findViewById(R.id.tv_check_large_gravtar);
        TextView tv_change_gravtar_photos = (TextView) view.findViewById(R.id.tv_change_gravtar_photos);
        TextView tv_change_gravtar_camera = (TextView) view.findViewById(R.id.tv_change_gravtar_camera);
        TextView tv_change_gravatar_prompt = (TextView) view.findViewById(R.id.tv_change_gravtar_prompt);
        tv_check_large_gravtar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                personalInfoPresenter.showLargeGravatar();
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });
        if(CommonConfig.isQtalk) {
            tv_change_gravtar_photos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PermissionDispatcher.requestPermissionWithCheck(PersonalInfoMyActivity.this, new int[]{PermissionDispatcher.REQUEST_WRITE_EXTERNAL_STORAGE,
                                    PermissionDispatcher.REQUEST_READ_EXTERNAL_STORAGE},
                            PersonalInfoMyActivity.this, REQUEST_GRANT_LOCAL);
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                }
            });
            tv_change_gravtar_camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PermissionDispatcher.requestPermissionWithCheck(PersonalInfoMyActivity.this, new int[]{PermissionDispatcher.REQUEST_CAMERA},
                            PersonalInfoMyActivity.this, REQUEST_GRANT_CAMERA);
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                }
            });
        }
        else {
            tv_change_gravtar_photos.setVisibility(View.GONE);
            tv_change_gravtar_camera.setVisibility(View.GONE);
            tv_change_gravatar_prompt.setVisibility(View.VISIBLE);

        }

        builder.setView(view);
        mDialog = builder.show();
        mDialog.setCanceledOnTouchOutside(true);
    }

    @Override
    public String getCheckedUserId() {

        return jid;
    }

    @Override
    public String getMarkup() {
        return markup;
    }

    @Override
    public void setMarkup(boolean isScuess) {

    }

    @Override
    public void setCheckResult(boolean isFriend) {

//        if (!TextUtils.isEmpty(jid)) {
//            if (jid.equals(CurrentPreference.getInstance().getPreferenceUserId())) {
//                send_message.setVisibility(View.GONE);
//                add_buddy.setVisibility(View.GONE);
//////                CurrentPreference.getInstance().getFullName();
//////                CurrentPreference.getInstance().getPreferenceUserId();
////                setSignView();
////                sign.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.atom_ui_edit, 0);
////                isHideBtn = true;
////                comment_txt.setVisibility(View.GONE);
////                cloud_record_of_chat.setVisibility(View.GONE);
////                bottom_container.setVisibility(View.VISIBLE);
////                ll_markup_container.setVisibility(View.GONE);
////                initProgressDialog();
//            }
//        } else {
//            send_message.setVisibility(View.VISIBLE);
//            add_buddy.setVisibility(View.VISIBLE);
//
//        }
//        if (isFriend) {
//            add_buddy.setBackgroundResource(R.drawable.atom_ui_common_button_red_select);
//            add_buddy.setText(R.string.atom_ui_delete_friend);
//            add_buddy.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (!TextUtils.isEmpty(jid)) {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(PersonalInfoMyActivity.this);
//                        builder.setMessage(R.string.atom_ui_prompt_del_friend);
//                        builder.setPositiveButton(R.string.atom_ui_common_confirm, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                                IBuddyPresenter buddyPresenter = new BuddyPresenter();
//                                buddyPresenter.setBuddyView(new DeleteBuddyView() {
//                                    @Override
//                                    public String getTargetId() {
//                                        return jid;
//                                    }
//                                });
//                                buddyPresenter.deleteBuddy();
//                                Toast.makeText(PersonalInfoMyActivity.this, R.string.tip_friend_deleted, Toast.LENGTH_SHORT).show();
//                                PersonalInfoMyActivity.this.finish();
//                            }
//                        });
//                        builder.setNegativeButton(R.string.atom_ui_common_cancel, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
//                        builder.show();
//                    }
//                }
//            });
//        } else {
//            add_buddy.setBackgroundResource(R.drawable.atom_ui_common_button_white_select);
//            add_buddy.setText(R.string.atom_ui_add_buddy);
//            add_buddy.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(PersonalInfoMyActivity.this, AddAuthMessageActivity.class);
//                    intent.putExtra("jid", jid);
//                    startActivity(intent);
//                    finish();
//                }
//            });
//        }
//        this.isFriend = isFriend;
    }

//    if (item.getItemId() == R.id.menu_friend_ops) {
//        Intent intent = new Intent(PersonalInfoMyActivity.this, AddAuthMessageActivity.class);
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
//                    Toast.makeText(PersonalInfoMyActivity.this, R.string.tip_friend_deleted, Toast.LENGTH_SHORT).show();
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
        if (i == R.id.rl_qr) {
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
        } else if (i == R.id.user_gravatar || i==R.id.rl_header) {
            if (!TextUtils.isEmpty(jid)) {
                if (jid.equals(CurrentPreference.getInstance().getPreferenceUserId())) {
                    showAlertDialog();
                } else {
                    personalInfoPresenter.showLargeGravatar();
                }
            }
        }
//        else if (i == R.id.phone_number_check) {
//            PermissionDispatcher.requestPermissionWithCheck(PersonalInfoMyActivity.this, new int[]{PermissionDispatcher.REQUEST_CALL},
//                    PersonalInfoMyActivity.this, REQUEST_GRANT_CALL);
//        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.menu_friend_ops) {
            Intent intent = new Intent(PersonalInfoMyActivity.this, AddAuthMessageActivity.class);
            intent.putExtra("jid", jid);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.menu_del_friend) {
            if (!TextUtils.isEmpty(jid)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.atom_ui_prompt_del_friend);
                builder.setPositiveButton(R.string.atom_ui_common_confirm, new DialogInterface.OnClickListener() {
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
                        Toast.makeText(PersonalInfoMyActivity.this, R.string.atom_ui_tip_friend_deleted, Toast.LENGTH_SHORT).show();
                        PersonalInfoMyActivity.this.finish();
                    }
                });
                builder.setNegativeButton(R.string.atom_ui_common_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        }
        return true;
    }

    class HandlePersonalEvent {
        public void onEventMainThread(final EventBusEvent.GravtarGot gravtarGot) {
//            if(gravtarGot.jid!=null&&gravtarGot.jid.equals(jid)) {
//                BackgroundExecutor.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        try{
//                            File imageFile = Glide.with(PersonalInfoMyActivity.this)
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
}
