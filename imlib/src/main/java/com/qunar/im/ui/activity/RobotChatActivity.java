package com.qunar.im.ui.activity;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.base.jsonbean.RobotInfoResult.Action;
import com.qunar.im.base.jsonbean.RobotInfoResult.RobotBody;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.module.PublishPlatform;
import com.qunar.im.base.presenter.IRobotSessionPresenter;
import com.qunar.im.base.presenter.IRushOrderPresenter;
import com.qunar.im.base.presenter.impl.RobotSessionPresenter;
import com.qunar.im.base.presenter.views.IChatView;
import com.qunar.im.base.presenter.views.IRobotChatView;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.ChatViewAdapter;
import com.qunar.im.ui.adapter.ExtendChatViewAdapter;
import com.qunar.im.ui.adapter.RobotActionAdapter;
import com.qunar.im.ui.adapter.RobotItemAdapter;
import com.qunar.im.ui.events.RushOrderEvent;
import com.qunar.im.ui.view.HorizontalListView;
import com.qunar.im.ui.view.MyDialog;
import com.qunar.im.ui.view.chatExtFunc.FuncMap;
import com.qunar.im.ui.view.zxing.activity.CaptureActivity;
import com.qunar.im.utils.QtalkStringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by zhaokai on 15-9-1.
 */

public class RobotChatActivity extends PbChatActivity implements IRobotChatView {
    public static final String OPEN_URL = "openurl";
    public static final String SEND_NSG = "sendmsg";
    public static final String QRCODE = "qrcode";
    public static final String BARCODE = "barcode";

    public static final int SCAN_REQUEST_BAR_CODE = 10020;
    public static final int SCAN_REQUEST_QR_CODE = 10021;
    public final static String ROBOT_ID_EXTRA = "robotId";
    public final static String MSG_TYPE_EXTRA = "msgType";
    public final static String CONTENT_EXTRA = "content";

    TextView robot_menu;
    LinearLayout chat_view;
    HorizontalListView robot_view;
    String robotId, content, msgType;
    PopupWindow pw;
    private boolean robotStatus = true;

    ValueAnimator animator = null;

    private RobotItemAdapter robotItemAdapter;

    private String robotGravatarUrl;
    private HandleCancelled cancelFR = new HandleCancelled();

    MyDialog dialog;

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        chatingPresenter = new RobotSessionPresenter();
        chatingPresenter.setView((IChatView) this);
        ((IRobotSessionPresenter) chatingPresenter).setIRobotChatView(this);
        pbChatViewAdapter.setMessages(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pw = new PopupWindow();
        chatingPresenter = new RobotSessionPresenter();
        chatingPresenter.setView((IChatView) this);
        ((IRobotSessionPresenter) chatingPresenter).setIRobotChatView(this);
        EventBus.getDefault().register(cancelFR);
    }

    @Override
    protected void injectExtras(Intent intent) {
        super.injectExtras(intent);
        Bundle extras_ = intent.getExtras();
        if (extras_ != null) {
            if (extras_.containsKey(ROBOT_ID_EXTRA)) {
                robotId = QtalkStringUtils.parseLocalpart(extras_.getString(ROBOT_ID_EXTRA));
            }
            if (extras_.containsKey(MSG_TYPE_EXTRA)) {
                msgType = extras_.getString(MSG_TYPE_EXTRA);
            }
            if (extras_.containsKey(CONTENT_EXTRA)) {
                content = extras_.getString(CONTENT_EXTRA);
            }
        }
    }

    @Override
    protected void bindViews() {
        super.bindViews();
        robot_menu = (TextView) findViewById(R.id.robot_menu);
        chat_view = (LinearLayout) findViewById(R.id.chat_view);
        robot_view = (HorizontalListView) findViewById(R.id.robot_view);
    }

    @Override
   protected void initViews() {
        jid = QtalkStringUtils.userId2Jid(robotId);
        if (pbChatViewAdapter == null) {
            pbChatViewAdapter = new ExtendChatViewAdapter(this, jid, getHandler(), false);
            pbChatViewAdapter.setLeftImageClickHandler(new ChatViewAdapter.LeftImageClickHandler() {
                @Override
                public void onLeftImageClickEvent(String jid) {
                    Intent intent = new Intent(RobotChatActivity.this, RobotInfoActivity.class);
                    intent.putExtra("robotId", robotId);
                    RobotChatActivity.this.startActivity(intent);
                }
            });

            pbChatViewAdapter.setContextMenuRegister(new ChatViewAdapter.ContextMenuRegister() {
                @Override
                public void registerContextMenu(View v) {
                    registerForContextMenu(v);
                }
            });
            pbChatViewAdapter.setGravatarHandler(new ChatViewAdapter.GravatarHandler() {
                @Override
                public void requestGravatarEvent(String jid, String imageSrc, SimpleDraweeView view) {

                }

//                @Override
//                public void requestGravatarEvent(final String nickOrUid, final SimpleDraweeView view) {
//                    if (!TextUtils.isEmpty(nickOrUid)) {
//                        if (nickOrUid.equals(jid) && !TextUtils.isEmpty(robotGravatarUrl)) {
//                            FacebookImageUtil.loadWithCache(robotGravatarUrl, view, false,
//                                    new FacebookImageUtil.ImageLoadCallback.EmptyCallback());
//                        } else {
//                            ProfileUtils.displayGravatarByFullname(nickOrUid, view);
//                        }
//                    }
//                }
            });
        }
        super.initViews();
        if (!TextUtils.isEmpty(content) &&
                !TextUtils.isEmpty(msgType)) {
            if (msgType.equals("action")) {
                ((IRobotSessionPresenter) chatingPresenter).sendActionMsg(content);
            } else {
//                edit_msg.setText(content);
//                chatingPresenter.sendMsg();
//                edit_msg.setText("");
            }
        }
    }

    @Override
    protected void onResume() {
        //会议室签到流程简化
        if (!TextUtils.isEmpty(robotId) && !TextUtils.isEmpty(msgType)) {
            if (robotId.equals(RobotChatActivity.this.robotId)
                    && msgType.equals("action") || msgType.equals("method")) {
                ((IRobotSessionPresenter) chatingPresenter).sendActionMsg(content);
            }
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void initActionBar() {
//        super.initActionBar();
    }

    private void setSubActionOnClickListener(final ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RobotActionAdapter actionAdapter = (RobotActionAdapter) listView.getAdapter();
                Action.SubAction action = (Action.SubAction) actionAdapter.getItem(position);
                Object value = action.actioncontent.value;
                String actionKey = action.actioncontent.action;
                if (value != null && actionKey != null)
                    handleAction(actionKey, value);
            }
        });
    }

    private void handleAction(String action, Object value) {
        switch (action) {
            case OPEN_URL:
                Intent intent = new Intent(this, QunarWebActvity.class);
                String url = (String) value;
                intent.setData(Uri.parse(url));
                startActivity(intent);
                break;
            case SEND_NSG:
                ((IRobotSessionPresenter) chatingPresenter).sendActionMsg((String) value);
                break;
            case BARCODE:
                Intent scanBARCodeIntent = new Intent(this, CaptureActivity.class);
                try {
                    Map<String, String> barcodeMap = (Map<String, String>) value;
                    scanBARCodeIntent.putExtra("robot_bar_code", barcodeMap.get("method"));
                } catch (Exception e) {
                    LogUtil.e(TAG, "ERROR", e);
                }
                startActivityForResult(scanBARCodeIntent, SCAN_REQUEST_BAR_CODE);
                break;
            case QRCODE:
                Intent scanQRCodeIntent = new Intent(this, CaptureActivity.class);
                startActivityForResult(scanQRCodeIntent, SCAN_REQUEST_QR_CODE);
                break;
        }
    }

    private void setRobotViewOnclickListener() {
        robot_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Action item = (Action) robotItemAdapter.getItem(position);
                if (item.actioncontent != null) {
                    String action = item.actioncontent.action;
                    if (action != null) {
                        handleAction(action, item.actioncontent.value);
                    }
                    return;
                }
                if (item.subactions != null && item.subactions.size() > 0) {
                    List<Action.SubAction> subActions = item.subactions;
                    RobotActionAdapter actionAdapter = new RobotActionAdapter(RobotChatActivity.this, subActions);
                    ListView listView = new ListView(RobotChatActivity.this);
                    listView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    listView.setAdapter(actionAdapter);
                    listView.setVerticalScrollBarEnabled(false);
                    actionAdapter.setTextViewWidth(view.getWidth());
                    actionAdapter.setTextViewHeight(view.getHeight());
                    setSubActionOnClickListener(listView);
                    int height = view.getHeight() * subActions.size() + 40;
                    int width = view.getWidth() + 20;
                    if (pw != null) {
                        pw.dismiss();
                        pw.setContentView(listView);
                        pw.setWidth(width);
                        pw.setHeight(height);
                        pw.setFocusable(true);
                        pw.setTouchable(true);
                        pw.setBackgroundDrawable(getResources().getDrawable(R.drawable.atom_ui_robot_balloon_1));
                        int[] location = new int[2];
                        view.getLocationOnScreen(location);
                        pw.showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1] - pw.getHeight() - 10);
                    }
                }
            }
        });
    }

    private void robotClickAnimation() {
        robot_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!animator.isRunning()){
                    robotStatus = !robotStatus;
                    animator.start();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == SCAN_REQUEST_BAR_CODE) {
            if (data != null) {
                if (!TextUtils.isEmpty(data.getStringExtra("content"))) {
                    Map<String, String> actionResult = new LinkedHashMap<>();
                    if (!TextUtils.isEmpty(data.getStringExtra("robot_bar_code"))) {
                        actionResult.put("method", data.getStringExtra("robot_bar_code"));
                    }
                    actionResult.put("content", data.getStringExtra("content"));
                    String action = JsonUtils.getGson().toJson(actionResult);
                    ((IRobotSessionPresenter) chatingPresenter).sendActionMsg(action);
                }
            }
        } else if (requestCode == SCAN_REQUEST_QR_CODE) {
            if (data != null) {
                if (!TextUtils.isEmpty(data.getStringExtra("content"))) {
                    Uri uri = Uri.parse(data.getStringExtra("content"));
                    String protocol = uri.getScheme();
                    if (protocol != null) {
                        if (protocol.equals(Constants.Config.QR_SCHEMA)) {
                            if (uri.getHost().equals("robot")) {
                                String robotId = QtalkStringUtils.parseLocalpart(uri.getQueryParameter("id"));
                                String cnt = uri.getQueryParameter("content");
                                String msgType = uri.getQueryParameter("msgType");
                                if (robotId.equals(RobotChatActivity.this.robotId)
                                        && msgType.equals("action") || msgType.equals("method")) {
                                    ((IRobotSessionPresenter) chatingPresenter).
                                            sendActionMsg(cnt);
                                }
                            }
                        }
                    } else {
                        ((IRobotSessionPresenter) chatingPresenter).
                                sendActionMsg(JsonUtils.getGson().toJson(data.getStringExtra("content")));
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v instanceof LinearLayout) {
            v.setTag(R.string.atom_ui_voice_hold_to_talk, "longclick");
            IMMessage message = (IMMessage) v.getTag();
            Intent intent = new Intent();
            intent.putExtra(Constants.BundleKey.MESSAGE, message);
            if (message.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeText_VALUE) {
                menu.add(0, MENU5, 0, getString(R.string.atom_ui_menu_copy)).setIntent(intent);
            }
            menu.add(0, MENU4, 0, getString(R.string.atom_ui_common_delete)).setIntent(intent);
        }
    }


    @Override
    public void onStop() {
        onWindowFocusChanged(false);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(cancelFR);
        super.onDestroy();
    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public String getRobotId() {
        return robotId;
    }

    @Override
    public void setRobotInfo(final RobotBody body, final int pubtype) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                //设置对话页面标题
                setActionBarTitle(body == null ? getString(R.string.atom_ui_contact_tab_public_number) : body.robotCnName);
                robotGravatarUrl = (body.headerurl == null ? "" : body.headerurl);
                if (pubtype == PublishPlatform.NOTICE_MSG) {
                    edit_region.setVisibility(View.GONE);
                    setActionBarRightIcon(0);
                    dialog = new MyDialog(RobotChatActivity.this, R.style.atom_ui_my_dialog);
                    return;
                } else {
                    setActionBarRightIcon(R.string.atom_ui_new_person);
                    if (body != null) {
                        setActionBarRightIconClick(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(RobotChatActivity.this, RobotInfoActivity.class);
                                intent.putExtra("robotId", body.robotEnName);
                                intent.putExtra("isHiden", true);
                                startActivity(intent);
                            }
                        });
                    } else {
                        setActionBarRightIcon(0);
                    }
                }
                List<Action> actions = body.actionlist;
                if (actions != null && actions.size() > 0) {
                    robotItemAdapter = new RobotItemAdapter(RobotChatActivity.this, actions);
                    setRobotViewOnclickListener();
                    robotClickAnimation();
                    robot_view.setAdapter(robotItemAdapter);
                    chat_view.setVisibility(View.GONE);
                    robot_view.setVisibility(View.VISIBLE);
                    robot_menu.setVisibility(View.VISIBLE);
                    final int height = edit_region.getHeight();
                    animator = ValueAnimator.ofInt(0, height);
                    animator.setDuration(400);
                    animator.setRepeatCount(0);
                    animator.setInterpolator(new TimeInterpolator() {
                        @Override
                        public float getInterpolation(float input) {
                            return 4.0f * input * input - 4.0f * input + 1;
                        }
                    });
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
//                            edit_region.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
//                                    Integer.valueOf(animation.getAnimatedValue().toString())));
                        }
                    });
                    animator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            chat_view.setEnabled(false);
                            robot_view.setEnabled(false);
                            linearlayout_tab.setVisibility(View.GONE);
                            linearlayout_tab2.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (robotStatus) {
                                chat_view.setVisibility(View.GONE);
                                robot_view.setVisibility(View.VISIBLE);
                            } else {
                                chat_view.setVisibility(View.VISIBLE);
                                robot_view.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    });
                } else {
                    chat_view.setVisibility(View.VISIBLE);
                    robot_view.setVisibility(View.GONE);
                    robot_menu.setVisibility(View.GONE);
                }
            }
        });
    }

//    @Override
//    protected void handleRushOrderMsg(IMMessage message) {
//        if (!TextUtils.isEmpty(message.getExt())) {
//            ThirdResponseMsgJson responseMsgJson = null;
//            try {
//                responseMsgJson = JsonUtils.getGson().
//                        fromJson(message.getExt(), ThirdResponseMsgJson.class);
//            } catch (Exception ex) {
//                LogUtil.e(TAG, "ERROR", ex);
//            }
//            if (responseMsgJson != null && !TextUtils.isEmpty(responseMsgJson.result)
//                    && responseMsgJson.result.equals("1")) {
//                if (dialog.isShowing()) {
//                    dialog.dismiss();
//                }
//                String chanelId = QtalkStringUtils.userId2Jid(responseMsgJson.sessionid);
//                ((IRushOrderPresenter) chatingPresenter).updateRushResult(responseMsgJson);
//                ((IRushOrderPresenter) chatingPresenter).initChanelId(chanelId, responseMsgJson.dealid);
//                Intent intent = new Intent(this, PbChatActivity.class);
//                intent.putExtra("jid", chanelId);
//                intent.putExtra("isFromChatRoom", false);
//                startActivity(intent);
//            } else {
//                if (dialog.isShowing()) {
//                    dialog.dismiss();
//                }
//                ((IRushOrderPresenter) chatingPresenter).clearRushQueue();
//                Toast.makeText(RobotChatActivity.this, "抢单失败", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    @Override
    public String getToId() {
        return robotId;
    }

    @Override
    public String getFromId() {
        return QtalkStringUtils.userId2Jid(CurrentPreference.getInstance().getUserid());
    }

    public class HandleCancelled {
        public void onEvent(EventBusEvent.CancelFollowRobot robot) {
            if (robotId.equals(robot.robotId)) {
                finish();
            }
        }

        public void onEventMainThread(final RushOrderEvent event) {
            if (dialog == null) {
                dialog = new MyDialog(RobotChatActivity.this, R.style.atom_ui_my_dialog);
            }
            if (dialog.isShowing()) dialog.dismiss();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            ((IRushOrderPresenter) chatingPresenter).rushOrder(event.dealId, event.message);
            event.message = null;
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    if (dialog.isShowing()) {
                        if (event.timeout == -1) {
                            dialog.dismiss();
                            Toast.makeText(RobotChatActivity.this, "抢单失败", Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.setContent("多人同时抢单\n正在筛选(" + event.timeout + ")");
                            event.timeout -= 1;
                            getHandler().postDelayed(this, 1000);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

//    @Override
//    protected void initGridView() {
//        super.initGridView();
//        funcMap.unregisger(FuncMap.AA);
//        funcMap.unregisger(FuncMap.HONGBAO);
//        linearlayout_tab.init(this, funcMap);
//    }
}
