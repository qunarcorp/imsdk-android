package com.qunar.im.ui.activity;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.common.CurrentPreference;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.jsonbean.ExtendMessageEntity;
import com.qunar.im.base.jsonbean.HongbaoContent;
import com.qunar.im.base.jsonbean.NoticeBean;
import com.qunar.im.base.jsonbean.QunarLocation;
import com.qunar.im.base.module.FavouriteMessage;
import com.qunar.im.base.module.GroupMember;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.presenter.IAddEmojiconPresenter;
import com.qunar.im.base.presenter.IChatingPresenter;
import com.qunar.im.base.presenter.ICloudRecordPresenter;
import com.qunar.im.base.presenter.IFavourityMessagePresenter;
import com.qunar.im.base.presenter.IP2pRTC;
import com.qunar.im.base.presenter.IDailyMindPresenter;
import com.qunar.im.base.presenter.ISendLocationPresenter;
import com.qunar.im.base.presenter.IShakeMessagePresenter;
import com.qunar.im.base.presenter.IShowNickPresenter;
import com.qunar.im.base.presenter.impl.ChatroomInfoPresenter;
import com.qunar.im.base.presenter.impl.FavourityMessagePresenter;
import com.qunar.im.base.presenter.impl.MultipleSessionPresenter;
import com.qunar.im.base.presenter.impl.DailyMindPresenter;
import com.qunar.im.base.presenter.impl.SendLocationPresenter;
import com.qunar.im.base.presenter.impl.SingleSessionPresenter;
import com.qunar.im.base.presenter.messageHandler.ConversitionType;
import com.qunar.im.base.presenter.views.IChatRoomInfoView;
import com.qunar.im.base.presenter.views.IChatView;
import com.qunar.im.base.presenter.views.IFavourityMsgView;
import com.qunar.im.base.presenter.views.IShowNickView;
import com.qunar.im.base.structs.FuncButtonDesc;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.base.structs.MessageType;
import com.qunar.im.base.util.BinaryUtil;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.EmotionUtils;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.FileUtils;
import com.qunar.im.base.util.InternDatas;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.ListUtil;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.ProfileUtils;
import com.qunar.im.utils.QtalkStringUtils;
import com.qunar.im.base.util.Utils;
import com.qunar.im.base.util.graphics.MyDiskCache;
import com.qunar.im.base.view.faceGridView.EmoticonEntity;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.permission.PermissionCallback;
import com.qunar.im.permission.PermissionDispatcher;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.ChatViewAdapter;
import com.qunar.im.ui.adapter.ExtendChatViewAdapter;
import com.qunar.im.ui.broadcastreceivers.ShareReceiver;
import com.qunar.im.ui.util.AndroidBug5497Workaround;
import com.qunar.im.ui.view.CustomAnimation;
import com.qunar.im.ui.view.baseView.ViewPool;
import com.qunar.im.ui.view.chatExtFunc.FuncHanlder;
import com.qunar.im.ui.view.chatExtFunc.FuncItem;
import com.qunar.im.ui.view.chatExtFunc.FuncMap;
import com.qunar.im.ui.view.chatExtFunc.OperationView;
import com.qunar.im.ui.view.emojiconEditView.EmojiconEditText;
import com.qunar.im.ui.view.faceGridView.EmotionLayout;
import com.qunar.im.ui.view.faceGridView.FaceGridView;
import com.qunar.im.ui.view.medias.play.MediaPlayerImpl;
import com.qunar.im.ui.view.medias.record.RecordView;

import java.io.File;
import java.io.Serializable;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import de.greenrobot.event.EventBus;


/**
 * Created by jiang.cheng on 2014/10/30.
 * Rewrite by xinbo.wang
 */
public class ChatActivity extends IMBaseActivity
        implements IChatView, PermissionCallback, SensorEventListener, View.OnClickListener, IMNotificaitonCenter.NotificationCenterDelegate,
        View.OnFocusChangeListener, IShowNickView {

    //连接核心类
    private ConnectionUtil connectionUtil;

    //数据消息adapter

    private List<ProtoMessageOuterClass.ProtoMessage> protoMessageList = new ArrayList<>();


    public static final String TAG = ChatActivity.class.getSimpleName();
    public static final String KEY_JID = "jid";
    public static final String KEY_IS_CHATROOM = "isFromChatRoom";
    public static final String REAL_USER = "realUser";
    public static final String KEY_ENCRYPT_BODY = "encryptBody";

    public static final int ACTIVITY_GET_CAMERA_IMAGE = 1;
    public static final int ACTIVITY_SELECT_PHOTO = 2;
    public static final int ACTIVITY_SELECT_LOCATION = 3;
    public static final int FILE_SELECT_CODE = 0x12;
    public static final int AT_MEMBER = 0x13;
    public static final int RECORD_VIDEO = 0x14;
    public static final int TRANSFER_CONVERSATION_REQUEST_CODE = 0x15;
    public static final int HONGBAO = 0x16;
    public static final int ADD_EMOTICON = 0x32;
    public static final int ACTIVITY_SELECT_VIDEO = 0x64;
    protected static final int MENU1 = 0x01;
    protected static final int MENU2 = 0x02;
    protected static final int MENU4 = 0x04;
    protected static final int MENU5 = 0x05;
    protected static final int MENU6 = 0x06;
    protected static final int MENU7 = 0x07;
    protected static final int MENU8 = 0x8;
    protected static final int MENU9 = 0x9;
    //引用功能
    protected static final int MENU10 = 0x10;

    protected final int SHOW_CAMERA = PermissionDispatcher.getRequestCode();
    protected final int SELECT_PIC = PermissionDispatcher.getRequestCode();
    protected final int RECORD = PermissionDispatcher.getRequestCode();
    protected final int SEND_VIDEO = PermissionDispatcher.getRequestCode();
    protected final int READ_FILE = PermissionDispatcher.getRequestCode();
    protected final int READ_LOCATION = PermissionDispatcher.getRequestCode();
    protected final int SELECT_VIDEO = PermissionDispatcher.getRequestCode();
    protected final int REAL_VIDEO = PermissionDispatcher.getRequestCode();
    protected final int REAL_AUDIO = PermissionDispatcher.getRequestCode();

    LinearLayout edit_region, linearlayout_tab2, outter_msg_prompt, atom_bottom_more;
    OperationView linearlayout_tab;
    PullToRefreshListView chat_region;
    TextView send_btn, new_msg_prompt, voice_prompt, outter_msg, no_prompt, close_prompt;
    ImageView left_btn, voice_switch_btn,
            tv_options_btn,emotion_btn,shareMessgeBtn, deleteMessageBtn, collectMsgBtn, emailMsgBtn;
    EmojiconEditText edit_msg;
    RelativeLayout input_container, relativeLayout, atom_bottom_frame;
    RecordView record;
    RelativeLayout chating_view;
    EmotionLayout faceView;
    View line;
    String jid;
    //热线的真实用户
    String realUser;
    Vibrator vibrator;
    AtomicInteger unreadMsgCount = new AtomicInteger(0);
    boolean isShowOutterMsg;
    String curOutterJid;
    boolean isFromChatRoom;
    boolean isFirstInit = true;
    protected IChatingPresenter chatingPresenter;
    private ISendLocationPresenter sendLocationPresenter;
    private IDailyMindPresenter passwordPresenter;//加密会话 密码箱
    protected ExtendChatViewAdapter adapter = null;
    boolean canShowAtActivity = true;
    boolean isSnapMsg;
    private int newMsgCount = 0;
    private String imageUrl = null;
    private String typingPrompt = "对方正在输入...";
    private String titleTempVar;
    private String transferId;
    private List<IMMessage> selectedMessages = new ArrayList<>();
    private String mTransferConversationContext;
    private AlertDialog mDialog;
    private Runnable typingShow = new Runnable() {
        @Override
        public void run() {
            myActionBar.getTitleTextview().setText(titleTempVar);
        }
    };

    private Runnable showOutter = new Runnable() {
        @Override
        public void run() {
            outter_msg_prompt.setVisibility(View.GONE);
        }
    };

    HandleChatEvent handleChatEvent = new HandleChatEvent();

    private long lastShakeTime;

    @Override
    public void responsePermission(int requestCode, boolean granted) {
        if (!granted) return;
        if (requestCode == SHOW_CAMERA) {
            showCamera();
        } else if (requestCode == SELECT_PIC) {
            selectPic();
        } else if (requestCode == READ_LOCATION) {
            chooseLocationType();
        } else if (requestCode == SEND_VIDEO) {
            sendVideo();
        } else if (requestCode == RECORD) {
            showRecordView();
        } else if (requestCode == READ_FILE) {
            sendFile();
        } else if (requestCode == SELECT_VIDEO) {
            selectVideo();
        } else if (requestCode == REAL_VIDEO) {
            Intent i = new Intent("android.intent.action.VIEW",
                    Uri.parse(CommonConfig.schema + "://qcrtc/webrtc?fromid=" +
                            QtalkStringUtils.userId2Jid(CurrentPreference.getInstance().getUserId())
                            + "&toid=" + QtalkStringUtils.parseBareJid(getFromId())
                            + "&offer=false&video=true"));
            startActivity(i);
            ((IP2pRTC) chatingPresenter).startVideoRtc();
        } else if (requestCode == REAL_AUDIO) {
            Intent i = new Intent("android.intent.action.VIEW",
                    Uri.parse(CommonConfig.schema + "://qcrtc/webrtc?fromid=" +
                            QtalkStringUtils.userId2Jid(CurrentPreference.getInstance().getUserId())
                            + "&toid=" + QtalkStringUtils.parseBareJid(getFromId())
                            + "&offer=false&video=false"));
            startActivity(i);
            ((IP2pRTC) chatingPresenter).startAudioRtc();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.atom_ui_activity_chat);
//        connectionUtil = ConnectionUtil.getInstance();
//        bindViews();
//        injectExtras(getIntent());
//        handleExtraData(savedInstanceState);
//        if (!CurrentPreference.getInstance().isLandscape()) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
//        initViews();
//        isShowOutterMsg = true;

        //挂事件
//        connectionUtil.addEvent(ChatActivity.this, QtalkEvent.Chat_Message_Text);
    }

    public void handlerReceivedData() {
        Intent shareIntent = getIntent();
        if (shareIntent == null) return;
        boolean fromShare = shareIntent.getBooleanExtra(Constants.BundleKey.IS_FROM_SHARE, false);
        if (fromShare) {
            if (shareIntent.getExtras() != null && shareIntent.getBooleanExtra(ShareReceiver.SHARE_TAG, false)) {
                String content = shareIntent.getStringExtra(ShareReceiver.SHARE_TEXT);
                ArrayList<String> icons = shareIntent.getStringArrayListExtra(ShareReceiver.SHARE_IMG);
                ArrayList<String> videos = shareIntent.getStringArrayListExtra(ShareReceiver.SHARE_VIDEO);
                if (!TextUtils.isEmpty(content)) {
                    edit_msg.setText(content);
                    chatingPresenter.sendMsg();
                    edit_msg.setText("");
                }
                if (!ListUtil.isEmpty(icons)) {
                    for (String img : icons) {
                        imageUrl = img;
                        chatingPresenter.sendImage();
                        imageUrl = "";
                    }
                }
                if (!ListUtil.isEmpty(videos)) {
                    for (String video : videos) {
                        chatingPresenter.sendVideo(video);
                    }
                }
            } else if (shareIntent.getExtras() != null && shareIntent.getExtras().containsKey(ShareReceiver.SHARE_EXTRA_KEY)) {
                try {
                    String jsonStr = shareIntent.getExtras().getString(ShareReceiver.SHARE_EXTRA_KEY);
                    ExtendMessageEntity entity = JsonUtils.getGson().fromJson(jsonStr, ExtendMessageEntity.class);
                    chatingPresenter.sendExtendMessage(entity);
                } catch (Exception ex) {
                    LogUtil.e(TAG, "ERROR", ex);
                }
            }
        }
    }


    protected void injectExtras(Intent intent) {
        Bundle extras_ = intent.getExtras();
        if (extras_ != null) {
            if (extras_.containsKey(KEY_JID)) {
                jid = extras_.getString(KEY_JID);
            }
            if (extras_.containsKey(KEY_IS_CHATROOM)) {
                isFromChatRoom = extras_.getBoolean(KEY_IS_CHATROOM);
            }
            //获取真实用户 consult消息
            if (extras_.containsKey(REAL_USER)) {
                realUser = extras_.getString(REAL_USER);
                if (TextUtils.isEmpty(realUser)) {
                    CurrentPreference.getInstance().setItConnection(null);
                    CurrentPreference.getInstance().setIsHot(false);
                    CurrentPreference.getInstance().savePreference();
                } else {
                    CurrentPreference.getInstance().setItConnection(realUser);
                    CurrentPreference.getInstance().setIsHot(true);
                    CurrentPreference.getInstance().savePreference();
                }
            }
        }
    }

//    protected void bindViews() {
//        edit_region = (LinearLayout) findViewById(R.id.edit_region);
//        atom_ui_left_btn = (ImageView) findViewById(R.id.atom_ui_left_btn);
//        voice_switch_btn = (ImageView) findViewById(R.id.voice_switch_btn);
//        voice_prompt = (TextView) findViewById(R.id.voice_prompt);
//        input_container = (RelativeLayout) findViewById(R.id.input_container);
//        atom_bottom_frame = (RelativeLayout) findViewById(R.id.atom_bottom_frame);
//        edit_msg = (EmojiconEditText) findViewById(R.id.edit_msg);
//        tv_options_btn = (ImageView) findViewById(R.id.tv_options_btn);
//        send_btn = (TextView) findViewById(R.id.send_btn);
//        linearlayout_tab = (OperationView) findViewById(R.id.linearlayout_tab);
//        record = (RecordView) findViewById(R.id.record);
//        linearlayout_tab2 = (LinearLayout) findViewById(R.id.linearlayout_tab2);
//        atom_bottom_more = (LinearLayout) findViewById(R.id.atom_bottom_more);
//        faceView = (EmotionLayout) findViewById(R.id.faceView);
//        chating_view = (RelativeLayout) findViewById(R.id.chating_view);
//        chat_region = (com.handmark.pulltorefresh.library.PullToRefreshListView) findViewById(R.id.chat_region);
//        new_msg_prompt = (TextView) findViewById(R.id.new_msg_prompt);
//        emotion_btn = (ImageView) findViewById(R.id.tv_emojicon);
//        outter_msg_prompt = (LinearLayout) findViewById(R.id.outter_msg_prompt);
//        outter_msg = (TextView) findViewById(R.id.outter_msg);
//        shareMessgeBtn = (ImageView) findViewById(R.id.txt_share_message);
//        deleteMessageBtn = (ImageView) findViewById(R.id.txt_del_msgs);
//        collectMsgBtn = (ImageView) findViewById(R.id.txt_collect_msg);
//        emailMsgBtn = (ImageView) findViewById(R.id.txt_email_msg);
//        no_prompt = (TextView) findViewById(R.id.no_prompt);
//        close_prompt = (TextView) findViewById(R.id.close_prompt);
//        relativeLayout = (RelativeLayout) findViewById(R.id.resizelayout);
//        line = findViewById(R.id.line);
//        new_msg_prompt.setOnClickListener(this);
//        shareMessgeBtn.setOnClickListener(this);
//        deleteMessageBtn.setOnClickListener(this);
//        emailMsgBtn.setOnClickListener(this);
//        collectMsgBtn.setOnClickListener(this);
//        tv_options_btn.setOnClickListener(this);
//        atom_ui_left_btn.setOnClickListener(this);
//        voice_switch_btn.setOnClickListener(this);
//        send_btn.setOnClickListener(this);
//        emotion_btn.setOnClickListener(this);
//        edit_msg.setOnFocusChangeListener(this);
//        no_prompt.setOnClickListener(this);
//        close_prompt.setOnClickListener(this);
//        outter_msg.setOnClickListener(this);
//        QtActionBar actionBar = (QtActionBar) this.findViewById(R.id.my_action_bar);
//        setActionBar(actionBar);
//    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        injectExtras(intent);
//        isFirstInit = true;
//        adapter = null;
//        handleExtraData(null);
//        initViews();
//        adapter.setMessages(null);
//        resetNewMsgCount();
//        isShowOutterMsg = true;
    }

    protected void handleExtraData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            jid = savedInstanceState.getString("jid");
        }
        if (jid != null) {
            jid = QtalkStringUtils.parseBareJid(jid);
            if (isFromChatRoom) {
                jid = QtalkStringUtils.roomId2Jid(jid);
            } else {
                jid = QtalkStringUtils.userId2Jid(jid);
            }
        }

        if (isFromChatRoom) {
            chatingPresenter = new MultipleSessionPresenter();
            ((IShowNickPresenter) chatingPresenter).setShowNickView(this);
        } else {
            chatingPresenter = new SingleSessionPresenter();
            sendLocationPresenter = new SendLocationPresenter();
            sendLocationPresenter.setView(this);
            passwordPresenter = new DailyMindPresenter();
        }
        chatingPresenter.setView(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
//        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    public void initActionBar() {
        myActionBar.getRightImageBtn().setVisibility(View.VISIBLE);
        if (isFromChatRoom) {
            myActionBar.getRightImageBtn().setImageResource(R.drawable.atom_ui_ic_chatroom_info);
        } else {
            myActionBar.getRightImageBtn().setImageResource(R.drawable.atom_ui_ic_personal_info);
        }
        myActionBar.getRightImageBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this,
                        ChatroomMembersActivity.class);
                intent.putExtra("jid", jid);
                intent.putExtra("isFromGroup", isFromChatRoom);
                startActivity(intent);
            }
        });
    }

    protected void initInputRegion() {
        edit_msg.clearFocus();
        edit_msg.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEND) {
                    sendMessage();
                    return true;
                }
                return false;
            }
        });

        edit_msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (count > 0 && after == 0) {
                    canShowAtActivity = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null || s.length() == 0) {
                    send_btn.setVisibility(View.GONE);
                    tv_options_btn.setVisibility(View.VISIBLE);
                    return;
                }

                tv_options_btn.setVisibility(View.GONE);
                send_btn.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isFromChatRoom) {
                    if (!canShowAtActivity) {
                        canShowAtActivity = true;
                        return;
                    }
                    int atIndex = edit_msg.getSelectionStart();
                    if (atIndex > 0) {
                        String at = s.subSequence(atIndex - 1, atIndex).toString();
                        if (at.equals("@")) {
                            Intent intent = new Intent(ChatActivity.this, AtListActivity.class);
                            intent.putExtra("jid", jid);
                            startActivityForResult(intent, AT_MEMBER);
                        }
                    }
                } else {
                    //consult消息发送到的是一个无效的虚拟帐号 所以暂时不做typing
                    if (TextUtils.isEmpty(realUser)) {
                        chatingPresenter.sendTypingStatus();
                    }
                }
            }
        });

        record.setVisibility(View.GONE);
        input_container.setVisibility(View.VISIBLE);
        left_btn.setVisibility(View.GONE);
        voice_switch_btn.setVisibility(View.VISIBLE);
        FuncMap map = initGridData();
        initGridView(map);
        final File file = MyDiskCache.getVoiceFile(MyDiskCache.TEMP_VOICE_FILE_NAME);
        /**
         * 控件限制，赋值顺序不能改变
         */
        record.initView(file.getAbsolutePath());
        record.setCallBack(new RecordView.IRecordCallBack() {

            @Override
            public void recordStart() {
                originalVal = CurrentPreference.getInstance().isTurnOnMsgSound();
                CurrentPreference.getInstance().setTurnOnMsgSound(false);
            }

            @Override
            public void recordFinish(long duration) {
                CurrentPreference.getInstance().setTurnOnMsgSound(originalVal);
                chatingPresenter.sendVoiceMessage(file.getAbsolutePath(), (int) (duration / 1000));

            }

            @Override
            public void recordCancel() {

            }

            boolean originalVal = true;
        });
        record.setStatusView(voice_prompt, getHandler());
        String draft = InternDatas.getDraft(QtalkStringUtils.parseBareJid(jid));
        if (!TextUtils.isEmpty(draft))
            edit_msg.setText(draft);
        if (!faceView.isInitialize()) {
            faceView.setDefaultOnEmoticionsClickListener(new DefaultOnEmoticionsClickListener());
            faceView.setOthersOnEmoricionsClickListener(new ExtentionEmoticionsClickListener());
            faceView.setAddFavoriteEmoticonClickListener(new OnAddFavoriteEmoticonClickListener());
            faceView.setFavoriteEmojiconOnClickListener(new FavoriteEmoticonOnClickListener());
            faceView.setDeleteImageViewOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int keyCode = KeyEvent.KEYCODE_DEL;  //这里是退格键
                    KeyEvent keyEventDown = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
                    KeyEvent keyEventUp = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
                    edit_msg.onKeyDown(keyCode, keyEventDown);
                    edit_msg.onKeyUp(keyCode, keyEventUp);
                }
            });
            faceView.initFaceGridView(EmotionUtils.getExtEmotionsMap(this, false), EmotionUtils.getDefaultEmotion(this),EmotionUtils.getDefaultEmotion1(this), EmotionUtils.getFavoriteMap(this));
        }
    }

    private FuncMap initGridData() {
        FuncMap funcMap = new FuncMap();

        FuncItem item = new FuncItem();
        item.id = FuncMap.PHOTO;
        item.icon = "res:///" + R.drawable.atom_ui_sharemore_picture;
        item.textId = getString(R.string.atom_ui_function_photo);
        item.hanlder = new FuncHanlder() {
            @Override
            public void handelClick() {
                choosePictrueSource();
            }
        };
        funcMap.regisger(item);
        item = new FuncItem();
        item.id = FuncMap.FILE;
        item.icon = "res:///" + R.drawable.atom_ui_sharemore_file;
        item.textId = getString(R.string.atom_ui_function_file);
        item.hanlder = new FuncHanlder() {
            @Override
            public void handelClick() {
                PermissionDispatcher.
                        requestPermissionWithCheck(ChatActivity.this, new int[]{
                                        PermissionDispatcher.REQUEST_WRITE_EXTERNAL_STORAGE,
                                        PermissionDispatcher.REQUEST_READ_EXTERNAL_STORAGE}, ChatActivity.this,
                                READ_FILE);
            }
        };
        funcMap.regisger(item);
        item = new FuncItem();
        item.id = FuncMap.LOCATION;
        item.icon = "res:///" + R.drawable.atom_ui_ic_sharelocation;
        item.textId = getString(R.string.atom_ui_function_location);
        item.hanlder = new FuncHanlder() {
            @Override
            public void handelClick() {
                PermissionDispatcher.
                        requestPermissionWithCheck(ChatActivity.this, new int[]{
                                        PermissionDispatcher.REQUEST_ACCESS_COARSE_LOCATION,
                                        PermissionDispatcher.REQUEST_ACCESS_FINE_LOCATION}, ChatActivity.this,
                                READ_LOCATION);
            }
        };
        funcMap.regisger(item);

        item = new FuncItem();
        item.id = FuncMap.VIDEO;
        item.icon = "res:///" + R.drawable.atom_ui_icon_send_video;
        item.textId = getString(R.string.atom_ui_function_video);
        item.hanlder = new FuncHanlder() {
            @Override
            public void handelClick() {
                chooseVideoSource();
            }
        };
        funcMap.regisger(item);

//        if(CommonConfig.showFireAfierread){
//            item = new FuncItem();
//            item.id = FuncMap.FIREAFTERREAD;
//            item.icon = "res:///" + R.drawable.atom_ui_ic_fire_msg;
//            item.textId = getString(R.string.atom_ui_message_has_destroy);
//            item.hanlder = new FuncHanlder() {
//                @Override
//                public void handelClick() {
//                    isSnapMsg = !isSnapMsg;
//                    if (!isSnapMsg) {
//                        edit_msg.setCompoundDrawables(null, null, null, null);
//                    } else {
//                        Drawable drawable = getResources().getDrawable(R.drawable.atom_ui_ic_fire);
//                        drawable.setBounds(0, 0, 48, 48);
//                        edit_msg.setCompoundDrawables(drawable, null, null, null);
//                    }
//                    ((ISnapPresenter) chatingPresenter).changeSnapStatus(isSnapMsg);
//                }
//            };
//            funcMap.regisger(item);
//        }

        if (!CommonConfig.isQtalk &&
                com.qunar.im.protobuf.common.CurrentPreference.getInstance().isMerchants() &&
                !isFromChatRoom) {
            item = new FuncItem();
            item.id = funcMap.genNewId();
            item.icon = "res:///" + R.drawable.atom_ui_transfer;
            item.textId = getString(R.string.atom_ui_transfer);
            item.hanlder = new FuncHanlder() {
                @Override
                public void handelClick() {
                    transferConversation();
                }
            };
            funcMap.regisger(item);
        }
        if (CommonConfig.showHongbao) {
            item = new FuncItem();
            item.id = FuncMap.HONGBAO;
            item.icon = "res:///" + R.drawable.atom_ui_ic_lucky_money;
            item.textId = getString(R.string.atom_ui_textbar_button_red_package);
            item.hanlder = new FuncHanlder() {
                @Override
                public void handelClick() {
                    giveLuckyMoney(false);
                }
            };
            funcMap.regisger(item);
        }
        if (!TextUtils.isEmpty(Constants.AA_PAY_URL)) {
            item = new FuncItem();
            item.id = FuncMap.AA;
            item.icon = "res:///" + R.drawable.atom_ui_ic_aa_pay_black;
            item.textId = getString(R.string.atom_ui_textbar_button_aa);
            item.hanlder = new FuncHanlder() {
                @Override
                public void handelClick() {
                    giveLuckyMoney(true);
                }
            };
            funcMap.regisger(item);
        }
        if (!isFromChatRoom) {
            item = new FuncItem();
            item.id = funcMap.genNewId();
            item.icon = "res:///" + R.drawable.atom_ui_shake_acitivity;
            item.textId = getString(R.string.atom_ui_textbar_button_shake);
            item.hanlder = new FuncHanlder() {
                @Override
                public void handelClick() {
                    shake();
                }
            };
            funcMap.regisger(item);
        }

        if (!isFromChatRoom) {
            item = new FuncItem();
            item.id = funcMap.genNewId();
            item.icon = "res:///" + R.drawable.atom_ui_video;
            item.textId = getString(R.string.atom_ui_function_video_call);
            item.hanlder = new FuncHanlder() {
                @Override
                public void handelClick() {
                    chooseRtcType();
                }
            };
            funcMap.regisger(item);
        }

        return funcMap;
    }

    protected void shake() {
        long time = Calendar.getInstance().getTimeInMillis();
        if (time - lastShakeTime >= 300000) {
            lastShakeTime = time;
            ((IShakeMessagePresenter) chatingPresenter).setShakeMessage();
            CustomAnimation customAnimation = new CustomAnimation();
            customAnimation.setDuration(2000);
            relativeLayout.startAnimation(customAnimation);
         /*
         * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
         * */
            long[] pattern = {100, 400, 100, 400, 100, 400};   // 停止 开启 停止 开启
            if (vibrator == null)
                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(pattern, -1);//重复两次上面的pattern 如果只想震动一次，index设为-1
        } else if (time - lastShakeTime < 10000) {
            Toast.makeText(this, "歇一会再发窗口抖动吧!", Toast.LENGTH_LONG).show();
        }
    }

    protected void initGridView(FuncMap map) {
        int CLIENT = CommonConfig.isQtalk ? 2 : 1;
        int SUPPORT = isFromChatRoom ? 2 : 1;
        int SCOPE = com.qunar.im.protobuf.common.CurrentPreference.getInstance().isMerchants() ? 2 : 1;
        for (final FuncButtonDesc funcButtonDesc : InternDatas.funcButtonDescs) {
            if ((funcButtonDesc.client & CLIENT) == CLIENT
                    && (funcButtonDesc.support & SUPPORT) == SUPPORT
                    && (funcButtonDesc.scope & SCOPE) == SCOPE) {

                FuncItem item = new FuncItem();
                item.id = map.genNewId();
                item.icon = funcButtonDesc.icon;
                item.textId = funcButtonDesc.title;
                item.hanlder = new FuncHanlder() {
                    @Override
                    public void handelClick() {
                        if (TextUtils.isEmpty(CommonConfig.verifyKey)) return;
                        StringBuilder builder = new StringBuilder(funcButtonDesc.linkurl);
                        if (builder.indexOf("?") > -1) {
                            builder.append("&");
                        } else {
                            builder.append("?");
                        }
                        builder.append("username=");
                        builder.append(URLEncoder.encode(CurrentPreference.getInstance().getUserId()));
                        builder.append("&rk=");
                        builder.append(URLEncoder.encode(CommonConfig.verifyKey));
                        if (isFromChatRoom) {
                            builder.append("&group_id=");
                        } else {
                            builder.append("&user_id=");
                        }
                        builder.append(URLEncoder.encode(getToId()));
                        builder.append("&company=");
                        builder.append(URLEncoder.encode(Constants.COMPANY));
                        builder.append("&domain=");
                        builder.append(URLEncoder.encode(Constants.Config.PUB_NET_XMPP_Domain));
                        Intent intent = new Intent(ChatActivity.this, QunarWebActvity.class);
                        intent.setData(Uri.parse(builder.toString()));
                        intent.putExtra(WebMsgActivity.IS_HIDE_BAR, true);
                        startActivity(intent);
                    }
                };
                map.regisger(item);
            }
        }
        linearlayout_tab.init(this, map);
    }

    private void initChatRegion() {
        if (adapter == null) {
            adapter = new ExtendChatViewAdapter(this, jid, getHandler(), isFromChatRoom);
            adapter.setLeftImageClickHandler(new ChatViewAdapter.LeftImageClickHandler() {
                @Override
                public void onLeftImageClickEvent(String jid) {
                    Intent intent = new Intent(ChatActivity.this,PersonalInfoActivity.class);
                    intent.putExtra("jid",ProfileUtils.getJid(jid));
                    intent.putExtra("isHideBtn", !isFromChatRoom);
                    ChatActivity.this.startActivity(intent);
                }
            });

            adapter.setContextMenuRegister(new ChatViewAdapter.ContextMenuRegister() {
                @Override
                public void registerContextMenu(View v) {
                    registerForContextMenu(v);
                }
            });
            adapter.setGravatarHandler(new ChatViewAdapter.GravatarHandler() {

                @Override
                public void requestGravatarEvent(String jid, String imageSrc, SimpleDraweeView view) {
                    ProfileUtils.displayGravatarByImageSrc(jid,imageSrc,view);
                }

//                @Override
//                public void requestGravatarEvent(final String nickOrJid, final SimpleDraweeView view) {
//                    ProfileUtils.displayGravatarByFullname(nickOrJid,view);
//                }
            });
            if (isFromChatRoom) {
                adapter.setLeftImageLongClickHandler(new ChatViewAdapter.LeftImageLongClickHandler() {
                    @Override
                    public void onLeftImageLongClickEvent(String from) {
                        canShowAtActivity = false;
                        chatRegionClick(from);
                    }
                });
            }
        }

        voice_prompt.setVisibility(View.GONE);
        linearlayout_tab.setVisibility(View.GONE);
        linearlayout_tab2.setVisibility(View.GONE);
        chat_region.setAdapter(adapter);
        chat_region.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (unreadMsgCount.intValue() > 0 &&
                            chat_region.getRefreshableView().getFirstVisiblePosition() <= adapter.getCount() - unreadMsgCount.intValue() - 1) {
                        clearUnread();
                    }
                    if (chat_region.getRefreshableView().getLastVisiblePosition() == chat_region.getRefreshableView().getCount() - 1) {
                        new_msg_prompt.setVisibility(View.GONE);
                        newMsgCount = 0;
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        chat_region.getRefreshableView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                edit_msg.clearFocus();
                if (linearlayout_tab2.getVisibility() == View.VISIBLE)
                    linearlayout_tab2.setVisibility(View.GONE);
                if (linearlayout_tab.getVisibility() == View.VISIBLE)
                    linearlayout_tab.setVisibility(View.GONE);
                //bottomInput.requestLayout();
                return false;
            }
        });


        chat_region.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> listViewPullToRefreshBase) {
                loadMoreHistory();
            }
        });

        new_msg_prompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetNewMsgCount();
            }
        });
    }

    protected void loadMoreHistory() {
        BackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                ((ICloudRecordPresenter) chatingPresenter).showMoreOldMsg(isFromChatRoom);
                LogUtil.d("debug", "history");
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v instanceof LinearLayout) {
            v.setTag(R.string.atom_ui_voice_hold_to_talk, "longclick");
            Object obj = v.getTag();
            if (obj == null) return;
            IMMessage message = (IMMessage) obj;
            if (message.getMsgType() == MessageType.MSG_HONGBAO_MESSAGE) return;
            Intent intent = new Intent();
            intent.putExtra(Constants.BundleKey.MESSAGE, message);
            if (message.getDirection() == IMMessage.DIRECTION_SEND) {
                menu.add(0, MENU8, 0, "撤销").setIntent(intent);
            }
            if (message.getMsgType() != MessageType.READ_TO_DESTROY_MESSAGE) {
                if (message.getDirection() == IMMessage.DIRECTION_RECV
                        && v.getTag(R.id.imageview) != null) {
                    intent.putExtra(Constants.BundleKey.IMAGE_URL,
                            v.getTag(R.id.imageview).toString());
                    menu.add(0, MENU7, 0, "添加到表情").setIntent(intent);
                }
                if (message.getDirection() == IMMessage.DIRECTION_SEND && message.getReadState() == MessageStatus.STATUS_FAILED
                        && message.getMsgType() == MessageType.TEXT_MESSAGE) {
                    menu.add(0, MENU2, 0, "重发").setIntent(intent);
                } else if (message.getReadState() == MessageStatus.STATUS_SUCCESS) {
                    menu.add(0, MENU1, 0, "转发").setIntent(intent);
                }
                if (message.getMsgType() == MessageType.TEXT_MESSAGE) {
                    menu.add(0, MENU5, 0, "复制").setIntent(intent);
                }
                menu.add(0, MENU6, 0, "收藏").setIntent(intent);
            }
            if (v.getTag(R.id.imageview) == null) {
                menu.add(0, MENU10, 0, "引用").setIntent(intent);
            }
            menu.add(0, MENU4, 0, getString(R.string.atom_ui_common_delete)).setIntent(intent);
            menu.add(0, MENU9, 0, "更多").setIntent(intent);
        }
    }

    private void initPbChatRegion() {
//        List<ProtoMessageOuterClass.ProtoMessage> list = new ArrayList<>();
//        pbChatViewAdapter = new PbChatViewAdapter2(this, list, getJid());
//        voice_prompt.setVisibility(View.GONE);
//        linearlayout_tab.setVisibility(View.GONE);
//        linearlayout_tab2.setVisibility(View.GONE);
//        chat_region.setAdapter(pbChatViewAdapter);
    }

    void initViews() {
        initActionBar();
        initInputRegion();
        //使用pb初始化聊天区域
        initPbChatRegion();


//        initChatRegion();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //只有全屏模式需要此设置
            //需要等到布局文件完成可调用
            AndroidBug5497Workaround.assistActivity(this);
        }
        chating_view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (edit_msg.isFocused() ||
                        linearlayout_tab.getVisibility() == View.VISIBLE ||
                        linearlayout_tab2.getVisibility() == View.VISIBLE) {
                    resetNewMsgCount();
                    //   mEditText.requestFocus();
                }
            }
        });
        //加载表情
        EmotionUtils.getDefaultEmotion(ChatActivity.this.getApplicationContext());
        EmotionUtils.getExtEmotionsMap(ChatActivity.this.getApplicationContext(),false);
        if (isFromChatRoom && InternDatas.cache.get(jid) != null) {
            InternDatas.cache.put(jid, "flag");
            BackgroundExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    ChatroomInfoPresenter chatroomInfoPresenter = new ChatroomInfoPresenter();
                    chatroomInfoPresenter.setView(new IChatRoomInfoView() {
                        @Override
                        public void setMemberList(List<GroupMember> list, int i,boolean enforce) {

                        }

                        @Override
                        public String getRoomId() {
                            return jid;
                        }

                        @Override
                        public void closeActivity() {


                        }

                        @Override
                        public void setChatroomInfo(Nick chatRoom) {

                        }

                        @Override
                        public void setMemberCount(int i) {

                        }

                        @Override
                        public void setExitResult(boolean b) {

                        }

                        @Override
                        public void setJoinResult(boolean b, String s) {

                        }

                        @Override
                        public void setUpdateResult(boolean b, String s) {

                        }

                        @Override
                        public Nick getChatroomInfo() {
                            return null;
                        }

                        @Override
                        public Context getContext() {
                            return ChatActivity.this;
                        }

                        @Override
                        public String getRealJid() {
                            return null;
                        }

                        @Override
                        public String getChatType() {
                            return null;
                        }
                    });
                    chatroomInfoPresenter.showMembers(false);
                }
            });
        }
    }

    protected void cancelMore() {
        adapter.changeShareStatus(false);
        adapter.notifyDataSetChanged();
        atom_bottom_frame.setVisibility(View.VISIBLE);
        atom_bottom_more.setVisibility(View.GONE);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) line.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ABOVE, R.id.atom_bottom_frame);
        line.setLayoutParams(layoutParams);
    }

    @Override
    public void onBackPressed() {
        if (atom_bottom_more.getVisibility() == View.VISIBLE) {
            cancelMore();
            return;
        }
        if (!isSoftinputShow()) {
            hideSoftInput();
            return;
        }
        if (linearlayout_tab.getVisibility() == View.VISIBLE) {
            linearlayout_tab.setVisibility(View.GONE);
            return;
        }
        if (linearlayout_tab2.getVisibility() == View.VISIBLE) {
            linearlayout_tab2.setVisibility(View.GONE);
            return;
        }
        this.finish();
    }


    void messgeInputFocusChangeListener(View v, boolean hasFocus) {
        if (hasFocus) {
            linearlayout_tab2.setVisibility(View.GONE);
            linearlayout_tab.setVisibility(View.GONE);
            // mlgbd,setBackgroundResource 会使padding 实效
            int left = input_container.getPaddingLeft();
            int right = input_container.getPaddingRight();
            int top = input_container.getPaddingTop();
            int bottom = input_container.getPaddingBottom();
            input_container.setBackgroundResource(R.drawable.atom_ui_bottom_border_primary_color);
            input_container.setPadding(left, top, right, bottom);

        } else {
            int left = input_container.getPaddingLeft();
            int right = input_container.getPaddingRight();
            int top = input_container.getPaddingTop();
            int bottom = input_container.getPaddingBottom();
            input_container.setBackgroundResource(R.drawable.atom_ui_bottom_border_gray_color);
            input_container.setPadding(left, top, right, bottom);
            hideSoftInput();
        }
    }

    void chatRegionClick(String fromName) {
        edit_msg.getEditableText().append("@" +
                ProfileUtils.getNickByKey(
                        QtalkStringUtils.userId2Jid(fromName)));
    }

    void scrollToBottom() {
        new_msg_prompt.setVisibility(View.GONE);
        newMsgCount = 0;
    }

    void mOptionButtonClickListener() {
        if (linearlayout_tab.getVisibility() == View.VISIBLE) {
            linearlayout_tab.setVisibility(View.GONE);
        } else {
            linearlayout_tab2.setVisibility(View.GONE);
            linearlayout_tab.setVisibility(View.VISIBLE);
            edit_msg.clearFocus();
        }
    }

    void switchButtonClickListener() {
        linearlayout_tab2.setVisibility(View.GONE);
        linearlayout_tab.setVisibility(View.GONE);
        record.setVisibility(View.GONE);
        voice_prompt.setVisibility(View.GONE);
        left_btn.setVisibility(View.GONE);
        voice_switch_btn.setVisibility(View.VISIBLE);
        input_container.setVisibility(View.VISIBLE);
        tv_options_btn.setVisibility(View.VISIBLE);
    }


    void voice_switch_btnClickListener() {
        PermissionDispatcher.requestPermissionWithCheck(this, new int[]{PermissionDispatcher.REQUEST_READ_EXTERNAL_STORAGE,
                PermissionDispatcher.REQUEST_WRITE_EXTERNAL_STORAGE, PermissionDispatcher.REQUEST_RECORD_AUDIO}, this, RECORD);
    }

    void showRecordView() {
        linearlayout_tab2.setVisibility(View.GONE);
        linearlayout_tab.setVisibility(View.GONE);
        input_container.setVisibility(View.GONE);
        voice_switch_btn.setVisibility(View.GONE);
        left_btn.setVisibility(View.VISIBLE);
        record.setVisibility(View.VISIBLE);
        voice_prompt.setVisibility(View.VISIBLE);
        tv_options_btn.setVisibility(View.INVISIBLE);
        edit_msg.clearFocus();
    }

    void mEmiticonButtonClickListener() {
        if (linearlayout_tab2.getVisibility() == View.VISIBLE) {
            linearlayout_tab2.setVisibility(View.GONE);
        } else {
            linearlayout_tab2.setVisibility(View.VISIBLE);
            linearlayout_tab.setVisibility(View.GONE);
            edit_msg.clearFocus();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFromChatRoom) {
            ((IShowNickPresenter) chatingPresenter).checkShowNick();
        }
        if (isFirstInit) {
            initHistoryMsg();
            isFirstInit = false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this, mSensor);
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("jid", jid);
    }

    @Override
    protected void onPause() {
        if (isFinishing()) {
            if (vibrator != null) vibrator.cancel();
            record.destroy();
            ViewPool.clear();
            getHandler().removeCallbacks(typingShow);
            chatingPresenter.close();
            CommonConfig.isPlayVoice = false;
            MediaPlayerImpl.getInstance().release();
            String draft = edit_msg.getText().toString();
            if (!TextUtils.isEmpty(draft)) {
                InternDatas.putDraft(QtalkStringUtils.parseBareJid(jid), draft);
            } else {
                InternDatas.removeDraft(QtalkStringUtils.parseBareJid(jid));
            }
        }
        super.onPause();
    }

    protected void initHistoryMsg() {
        BackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                chatingPresenter.propose();
            }
        });
    }

    void sendMessage() {
        if (edit_msg.getText() == null || edit_msg.getText().toString().trim().length() < 1) {
            return;
        }
        chatingPresenter.sendMsg();
        edit_msg.setText("");
    }

    /**
     * @return see whether softinput was shown or not
     */
    void hideSoftInput() {
        final InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(edit_msg.getWindowToken(), 0);
        }
    }

    boolean isSoftinputShow() {
        final InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        return imm.isActive();
    }

    @Override
    public String getInputMsg() {
        return edit_msg.getText().toString();
    }

    @Override
    public void setNewMsg2DialogueRegion(final IMMessage newMsg) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (unreadMsgCount.intValue() > 0) unreadMsgCount.incrementAndGet();
                adapter.addNewMsg(newMsg);
                if (newMsg.getDirection() == IMMessage.DIRECTION_RECV || newMsg.getDirection() == IMMessage.DIRECTION_SEND) {
                    newMsgCount++;
                }
                if (chat_region.getRefreshableView().getCount() > 0) {
                    if (newMsg.getDirection() == IMMessage.DIRECTION_SEND ||
                            edit_msg.isFocused() || linearlayout_tab.getVisibility() == View.VISIBLE ||
                            chat_region.getRefreshableView().getLastVisiblePosition() >= chat_region.getRefreshableView().getCount() - 5) {
                        chat_region.getRefreshableView().setSelection(chat_region.getRefreshableView().getCount() - 1);
                        new_msg_prompt.setVisibility(View.GONE);
                        newMsgCount = 0;
                    } else {
                        if (newMsg.getDirection() == IMMessage.DIRECTION_RECV || newMsg.getDirection() == IMMessage.DIRECTION_SEND) {
                            String msg = MessageFormat.format(getString(R.string.atom_ui_tip_new_msg_prompt), newMsgCount);
                            new_msg_prompt.setText(msg);
                            new_msg_prompt.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void showUnReadCount(int count) {

    }

    @Override
    public String getAutoReply() {
        return null;
    }

    @Override
    public String getOf() {
        return null;
    }

    @Override
    public String getOt() {
        return null;
    }

    @Override
    public String getRealJid() {
        return null;
    }

    private void resetNewMsgCount() {
        if (chat_region.getRefreshableView().getCount() > 0) {
            //mListView.getRefreshableView().setSelection(mListView.getRefreshableView().getCount() - 1); //在vivo手机上会导致粘贴功能不正常
            chat_region.getRefreshableView().smoothScrollToPosition(chat_region.getRefreshableView().getCount() - 1);
        }

        new_msg_prompt.setVisibility(View.GONE);
        newMsgCount = 0;
    }

    @Override
    public String getFromId() {
        return QtalkStringUtils.userId2Jid(CurrentPreference.getInstance().getUserId());
    }

    @Override
    public String getFullName() {
        return null;
    }

    @Override
    public String getToId() {
        return jid;
    }

    @Override
    public String getChatType() {
        return null;
    }

//    @Override
//    public String getSission() {
//        return sission;
//    }

    @Override
    public String getUserId() {
        return CurrentPreference.getInstance().getUserId();
    }

    @Override
    public void setHistoryMessage(final List<IMMessage> historyMessage, final int unread) {

        getHandler().post(new Runnable() {
            @Override
            public void run() {
                unreadMsgCount.set(unread);
                chat_region.onRefreshComplete();
                if (historyMessage != null && historyMessage.size() > 0) {

                    adapter.setMessages(historyMessage);
                    if (chat_region.getRefreshableView().getCount() > 0)
                        chat_region.getRefreshableView().setSelection(chat_region.getRefreshableView().getCount() - 1);
                }
                handlerReceivedData();
                if (unread > 0) {
                    final TextView textView = new TextView(ChatActivity.this);
                    int padding = Utils.dipToPixels(ChatActivity.this, 4);
                    int size = Utils.dipToPixels(ChatActivity.this, 32);
                    final LinearLayout linearLayout = new LinearLayout(ChatActivity.this);
                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    linearLayout.setGravity(Gravity.CENTER_VERTICAL);
                    linearLayout.setBackgroundResource(R.drawable.atom_ui_float_tab);
                    ImageView imageView = new ImageView(ChatActivity.this);
                    imageView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
                    imageView.setImageResource(R.drawable.atom_ui_close);
                    imageView.setPadding(padding, padding, padding, padding);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            size);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    layoutParams.setMargins(0, 0, 0, size);
                    linearLayout.setLayoutParams(layoutParams);
                    textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    textView.setGravity(Gravity.CENTER);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                    textView.setTextColor(getResources().getColor(R.color.atom_ui_primary_color));

                    textView.setText(unread + "条未读消息");
                    textView.setPadding(padding, padding, padding, padding);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            chat_region.getRefreshableView().setSelection(adapter.getCount() - unreadMsgCount.intValue() - 1);
                            clearUnread();
                        }
                    });
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clearUnread();
                        }
                    });
                    linearLayout.addView(textView);
                    linearLayout.addView(imageView);
                    LayoutTransition layoutTransition = new LayoutTransition();
                    layoutTransition.setAnimator(LayoutTransition.APPEARING, ObjectAnimator.ofFloat(this, "scaleX", 0, 1));
                    chating_view.setLayoutTransition(layoutTransition);
                    chating_view.addView(linearLayout);
                }
            }
        });
    }

    protected void clearUnread() {
        unreadMsgCount.set(0);
        chating_view.setLayoutTransition(null);
        chating_view.removeViewAt(chating_view.getChildCount() - 1);
    }

    @Override
    public void addHistoryMessage(final List<IMMessage> historyMessage) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                chat_region.onRefreshComplete();
                if (historyMessage == null || historyMessage.size() == 0) {
                    return;
                }
                if (historyMessage.size() == 1) {
                    if (TextUtils.isEmpty(historyMessage.get(0).getBody())) {
                        historyMessage.get(0).setBody(getString(R.string.atom_ui_cloud_record_prompt));
                        adapter.addOldMsg(historyMessage);
                        return;
                    }
                }
                adapter.addOldMsg(historyMessage);
                chat_region.getRefreshableView().setSelection(historyMessage.size());
            }
        });
    }

    @Override
    public void showNoticePopupWindow(NoticeBean noticeBean) {

    }

    @Override
    public Map<String, String> getAtList() {
        return null;
    }

    @Override
    public void clearAndAddMsgs(List<IMMessage> historyMessage, int unread) {

    }

    @Override
    public void setTitle(final String title) {
        if (title != null) {
            QunarIMApp.mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    myActionBar.getTitleTextview().setText(title);
                }
            });
        }
    }

    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public int getListSize() {
        return 0;
    }

    @Override
    public void setTitleState(String stats) {

    }

    @Override
    public void revokeItem(IMMessage imMessage) {

    }

    @Override
    public void deleteItem(IMMessage imMessage) {

    }


    @Override
    public void replaceItem(IMMessage imMessage) {

    }

    @Override
    public void sendEditPic(String path) {

    }


    @Override
    public void closeActivity() {

    }


    @Override
    public String getUploadImg() {
        return imageUrl;
    }

    @Override
    public String getTransferId() {
        return transferId;
    }

    @Override
    public List<IMMessage> getSelMessages() {
        return selectedMessages;
    }

    @Override
    public void refreshDataset() {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void setCurrentStatus(final String status) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                myActionBar.getTitleTextview().append(status);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case ADD_EMOTICON:
                handleEmojiconResult(data);
                break;
            case ACTIVITY_GET_CAMERA_IMAGE:
                getCameraImageResult(data);
                break;
            case ACTIVITY_SELECT_PHOTO:
                selectPhotoResult(data);
                break;
            case FILE_SELECT_CODE:
                selectFileResult(data);
                break;
            case AT_MEMBER:
                String fm = data.getStringExtra("atName");
                if (!TextUtils.isEmpty(fm)) {
                    int index = edit_msg.getSelectionStart();
                    Editable edit = edit_msg.getEditableText();
                    if (index <= 0 || index >= edit.length()) {
                        edit.append(fm);
                        edit.append(" ");
                    } else {
                        edit.insert(index, fm + " ");
                    }
                }
                break;
            case ACTIVITY_SELECT_LOCATION:
                selectLocationResult(data);
                break;
            case RECORD_VIDEO:
                String filePath = null;
                Uri _uri = data.getData();
                if (_uri != null && "content".equals(_uri.getScheme())) {
                    Cursor cursor = null;
                    try {
                        cursor = this
                                .getContentResolver()
                                .query(_uri,
                                        new String[]{android.provider.MediaStore.Video.VideoColumns.DATA},
                                        null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            filePath = cursor.getString(0);
                        }
                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }

                } else if (_uri != null) {
                    filePath = _uri.getPath();
                }
                if (!TextUtils.isEmpty(filePath)) {
                    File file = new File(filePath);
                    if (file.exists()) {
                        chatingPresenter.sendVideo(filePath);
                    } else {
                        Toast.makeText(this, R.string.atom_ui_file_not_exist, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case ACTIVITY_SELECT_VIDEO:
                String path = data.getStringExtra("filepath");
                if (!TextUtils.isEmpty(path)) {
                    File file = new File(path);
                    if (file.exists()) {
                        chatingPresenter.sendVideo(path);
                    } else {
                        Toast.makeText(this, R.string.atom_ui_file_not_exist, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case TRANSFER_CONVERSATION_REQUEST_CODE:
                transferId = data.getStringExtra("userid");
                String origin = edit_msg.getText().toString();
                edit_msg.setText(mTransferConversationContext);
                chatingPresenter.transferConversation();
                edit_msg.setText(origin);
                mTransferConversationContext = null;
                transferId = null;
                break;
            case HONGBAO:
                String content = data.getStringExtra(Constants.BundleValue.HONGBAO);
                if (!TextUtils.isEmpty(content)) {
                    try {
                        String jsonStr = new String(Base64.decode(content, Base64.DEFAULT));
                        HongbaoContent hongbao = JsonUtils.getGson().fromJson(jsonStr, HongbaoContent.class);
                        chatingPresenter.hongBaoMessage(hongbao);
                    } catch (Exception ex) {
                        LogUtil.e(TAG, "ERROR", ex);
                    }
                }
                break;
            default:
                break;

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getCameraImageResult(Intent data) {
        if (data != null) {
            imageUrl = data.getStringExtra(ImageClipActivity.KEY_CAMERA_PATH);
            //旋转照片，但是耗时太久，暂时弃用
            //Bitmap bitmap = ImageUtils.transformRotation(imageUrl);
            //ImageUtils.saveBitmap(bitmap, new File(imageUrl));
            //bitmap.recycle();
            chatingPresenter.sendImage();
        }
    }

    public void selectPhotoResult(Intent data) {
        try {
            if (data != null) {
                List<String> images = data.getStringArrayListExtra(PictureSelectorActivity.KEY_SELECTED_PIC);
                if (images.size() > 0) {
                    for (String image : images) {
                        imageUrl = image;
                        chatingPresenter.sendImage();
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "ERROR", e);
        }
    }

    public void selectFileResult(Intent data) {
        Uri uri = data.getData();
        // String path = FileUtils.getRealPath(uri);
        String path = FileUtils.getPath(this, uri);
        if (!TextUtils.isEmpty(path)) {
            chatingPresenter.sendFile(path);
        }
    }

    public void selectLocationResult(Intent data) {
        QunarLocation location = new QunarLocation();
        location.latitude = data.getDoubleExtra(Constants.BundleKey.LATITUDE, 0);
        location.longitude = data.getDoubleExtra(Constants.BundleKey.LONGITUDE, 0);
        location.addressDesc = data.getStringExtra(Constants.BundleKey.ADDRESS);
        location.fileUrl = data.getStringExtra(Constants.BundleKey.FILE_NAME);
        location.name = data.getStringExtra(Constants.BundleKey.LOCATION_NAME);
        chatingPresenter.sendLocation(location);
    }

    public void choosePictrueSource() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.atom_ui_dialog_choose_picture, (ViewGroup) this.getWindow().getDecorView(), false);
        TextView tv_change_gravtar_photos = (TextView) view.findViewById(R.id.tv_change_gravtar_photos);
        TextView tv_change_gravtar_camera = (TextView) view.findViewById(R.id.tv_change_gravtar_camera);
        tv_change_gravtar_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionDispatcher.
                        requestPermissionWithCheck(ChatActivity.this, new int[]{PermissionDispatcher.REQUEST_READ_EXTERNAL_STORAGE,
                                PermissionDispatcher.REQUEST_WRITE_EXTERNAL_STORAGE}, ChatActivity.this, SELECT_PIC);
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });
        tv_change_gravtar_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionDispatcher.
                        requestPermissionWithCheck(ChatActivity.this, new int[]{PermissionDispatcher.REQUEST_CAMERA,
                                        PermissionDispatcher.REQUEST_WRITE_EXTERNAL_STORAGE, PermissionDispatcher.REQUEST_READ_EXTERNAL_STORAGE}, ChatActivity.this,
                                SHOW_CAMERA);
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });

        builder.setView(view);
        mDialog = builder.show();
        mDialog.setCanceledOnTouchOutside(true);
    }

    public void chooseVideoSource() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.atom_ui_dialog_choose_picture, (ViewGroup) this.getWindow().getDecorView(), false);
        TextView tv_change_gravtar_photos = (TextView) view.findViewById(R.id.tv_change_gravtar_photos);
        TextView tv_change_gravtar_camera = (TextView) view.findViewById(R.id.tv_change_gravtar_camera);
        tv_change_gravtar_photos.setText(getString(R.string.atom_ui_function_choose_file));
        tv_change_gravtar_camera.setText(getString(R.string.atom_ui_function_use_camera));
        tv_change_gravtar_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionDispatcher.
                        requestPermissionWithCheck(ChatActivity.this, new int[]{PermissionDispatcher.REQUEST_READ_EXTERNAL_STORAGE,
                                PermissionDispatcher.REQUEST_WRITE_EXTERNAL_STORAGE}, ChatActivity.this, SELECT_VIDEO);
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });
        tv_change_gravtar_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionDispatcher.
                        requestPermissionWithCheck(ChatActivity.this, new int[]{PermissionDispatcher.REQUEST_CAMERA, PermissionDispatcher.REQUEST_RECORD_AUDIO,
                                        PermissionDispatcher.REQUEST_WRITE_EXTERNAL_STORAGE, PermissionDispatcher.REQUEST_READ_EXTERNAL_STORAGE}, ChatActivity.this,
                                SEND_VIDEO);
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });

        builder.setView(view);
        mDialog = builder.show();
        mDialog.setCanceledOnTouchOutside(true);
    }

    public void chooseRtcType() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.atom_ui_dialog_choose_picture, (ViewGroup) this.getWindow().getDecorView(), false);
        TextView tv_change_gravtar_photos = (TextView) view.findViewById(R.id.tv_change_gravtar_photos);
        TextView tv_change_gravtar_camera = (TextView) view.findViewById(R.id.tv_change_gravtar_camera);
        tv_change_gravtar_photos.setText(R.string.atom_ui_rtc_call);
        tv_change_gravtar_camera.setText(R.string.atom_ui_rtc_video_call);
        tv_change_gravtar_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionDispatcher.
                        requestPermissionWithCheck(ChatActivity.this, new int[]{PermissionDispatcher.REQUEST_RECORD_AUDIO},
                                ChatActivity.this, REAL_AUDIO);
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });
        tv_change_gravtar_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionDispatcher.
                        requestPermissionWithCheck(ChatActivity.this, new int[]{PermissionDispatcher.REQUEST_CAMERA,
                                        PermissionDispatcher.REQUEST_RECORD_AUDIO}, ChatActivity.this,
                                REAL_VIDEO);
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });

        builder.setView(view);
        mDialog = builder.show();
        mDialog.setCanceledOnTouchOutside(true);
    }

    private void chooseLocationType() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.atom_ui_dialog_choose_location, (ViewGroup) this.getWindow().getDecorView(), false);
        TextView sendLocation = (TextView) view.findViewById(R.id.send_location);
        final TextView shareLocation = (TextView) view.findViewById(R.id.share_current_location);
        sendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLocation();
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });
        shareLocation.setOnClickListener(new View.OnClickListener() {

            //共享实时位置
            @Override
            public void onClick(View v) {
                shareMyLocation();
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });

        builder.setView(view);
        mDialog = builder.show();
        mDialog.setCanceledOnTouchOutside(true);
    }

    private void shareMyLocation() {
        if (isFromChatRoom) {
            //群组共享实时位置暂时不支持,等待后续版本加上
            Toast.makeText(this, "暂时不支持群组共享实时位置", Toast.LENGTH_SHORT).show();
            return;
        }
        //发送共享位置消息并且启动共享位置的Activity
        String shareId = UUID.randomUUID().toString();
        sendLocationPresenter.sendShareLocationMessage(shareId);
        Intent intent = new Intent();
        intent.setClass(this, ShareLocationActivity.class);
        intent.putExtra(ShareLocationActivity.FROM_ID, QtalkStringUtils.userId2Jid(CurrentPreference.getInstance().getUserId()));
        intent.putExtra(ShareLocationActivity.SHARE_ID, shareId);
        startActivity(intent);
    }

    void sendLocation() {
        Intent intentLocation = new Intent(this, LocationActivity.class);
        intentLocation.putExtra(Constants.BundleKey.LOCATION_TYPE, LocationActivity.TYPE_SEND);
        startActivityForResult(intentLocation, ACTIVITY_SELECT_LOCATION);
    }

    void sendFile() {
        /** 调用文件选择软件来选择文件 **/
        Intent intentFile = new Intent(Intent.ACTION_GET_CONTENT);
        intentFile.setType("*/*");
        intentFile.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intentFile, "请选择一个要上传的文件"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(ChatActivity.this, "请安装文件管理器", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    void sendVideo() {
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0); // 先设置为0省点流量
        startActivityForResult(videoIntent, RECORD_VIDEO);
    }

    void selectPic() {
        Intent intent1 = new Intent(this, PictureSelectorActivity.class);
        intent1.putExtra("isMultiSel", true);
        intent1.putExtra(PictureSelectorActivity.SHOW_EDITOR, true);
        startActivityForResult(intent1, ACTIVITY_SELECT_PHOTO);
    }

    void selectVideo() {
        Intent intent = new Intent(this, VideoSelectorActivity.class);
        intent.putExtra("isMultiSel", false);
        startActivityForResult(intent, ACTIVITY_SELECT_VIDEO);
    }

    void showCamera() {
        Intent intent = new Intent(this, ImageClipActivity.class);
        intent.putExtra(ImageClipActivity.KEY_CLIP_ENABLE, false);
        File file = new File(MyDiskCache.getDirectory(),
                UUID.randomUUID().toString() + ".jpg");
        intent.putExtra(ImageClipActivity.KEY_CAMERA_PATH, file.getPath());
        startActivityForResult(intent, ACTIVITY_GET_CAMERA_IMAGE);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        IMMessage message = null;
        message = (IMMessage) item.getIntent().getSerializableExtra(Constants.BundleKey.MESSAGE);

        switch (item.getItemId()) {
            case MENU1:
                Intent selUser = new Intent(this, SearchUserActivity.class);
                selUser.putExtra(Constants.BundleKey.IS_TRANS, true);
                selUser.putExtra(Constants.BundleKey.TRANS_MSG, message);
                startActivity(selUser);
                break;
            case MENU2:
                selectedMessages.clear();
                selectedMessages.add(message);
                chatingPresenter.resendMessage();
                selectedMessages.clear();
                break;
            case MENU4:
                selectedMessages.clear();
                selectedMessages.add(message);
                chatingPresenter.deleteMessge();
                selectedMessages.clear();
                break;
            case MENU5:
                if (message != null && !TextUtils.isEmpty(message.getBody())) {
                    String content = ChatTextHelper.showContentType(message.getBody(), message.getMsgType());
                    Utils.dropIntoClipboard(content, this);
                    Toast.makeText(this, "已复制到剪切板", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "没有可复制内容", Toast.LENGTH_SHORT).show();
                }
                break;
            case MENU6:
                if (message != null) {
                    IFavourityMessagePresenter fPresenter = new FavourityMessagePresenter();
                    final IMMessage finalMessage = message;
                    fPresenter.setFavourity(new IFavourityMsgView() {
                        @Override
                        public List<FavouriteMessage> getSelectedMsgs() {
                            List<FavouriteMessage> messages = new ArrayList<FavouriteMessage>();
                            FavouriteMessage fMsg = new FavouriteMessage();
                            fMsg.setId(UUID.randomUUID().toString());
                            fMsg.setTextContent(JsonUtils.getGson().toJson(finalMessage));
                            if (isFromChatRoom) {
                                fMsg.setFromType("1");
                            }
                            fMsg.setFromUserId(finalMessage.getFromID());
                            fMsg.setTime(Calendar.getInstance().getTime().getTime() + "");
                            messages.add(fMsg);
                            return messages;
                        }

                        @Override
                        public void setFavourityMessages(List<FavouriteMessage> list) {
                        }
                    });
                    fPresenter.addFavourity();
                } else {
                    Toast.makeText(this, "没有可收藏内容", Toast.LENGTH_SHORT).show();
                }
                break;
            case MENU7:
                imageUrl = item.getIntent().getStringExtra(Constants.BundleKey.IMAGE_URL);
                ((IAddEmojiconPresenter) chatingPresenter).addEmojicon();
                imageUrl = null;
                faceView.resetFavoriteEmotion(EmotionUtils.getFavoriteMap(this));
                //重新加载自定义表情
                faceView.resetFavoriteTab();
                break;
            case MENU8:
                selectedMessages.clear();
                selectedMessages.add(message);
                chatingPresenter.revoke();
                selectedMessages.clear();
                break;
            case MENU9:
                atom_bottom_frame.setVisibility(View.GONE);
                atom_bottom_more.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) line.getLayoutParams();
//                layoutParams.addRule(RelativeLayout.ABOVE, R.id.atom_bottom_more);
                line.setLayoutParams(layoutParams);
                adapter.changeShareStatus(true);
                adapter.notifyDataSetChanged();
                break;
            //引用功能
            case MENU10:
                String str = "";
                //群组聊天
                if (message.getType() == 1) {
                    str = ProfileUtils.getNickByKey(QtalkStringUtils.parseResource(message.getFromID()));
                } else
                    //单人聊天
                    if (message.getType() == 0) {
                        str = QtalkStringUtils.parseResource(ProfileUtils.getNickByKey(QtalkStringUtils.userId2Jid(message.getFromID())));
                    }
                edit_msg.setText("「" + str + "：" + message.getBody() + "」" + "\n" + "- - - - - - - - - - - - - - -" + "\n");
                edit_msg.setFocusable(true);
                edit_msg.setFocusableInTouchMode(true);
                edit_msg.setSelection(edit_msg.getText().toString().length());
                edit_msg.requestFocus();

                break;

        }
        return true;
    }

    @Override
    public void onClick(View v) {
//        int i = v.getId();
//        if (i == R.id.new_msg_prompt) {
//            scrollToBottom();
//        } else if (i == R.id.tv_options_btn) {
//            mOptionButtonClickListener();
//
//        } else if (i == R.id.atom_ui_left_btn) {
//            switchButtonClickListener();
//
//        } else if (i == R.id.voice_switch_btn) {
//            voice_switch_btnClickListener();
//
//        } else if (i == R.id.send_btn) {
//            sendMessage();
//
//        } else if (i == R.id.tv_emojicon) {
//            mEmiticonButtonClickListener();
//
//        } else if (i == R.id.no_prompt) {
//            isShowOutterMsg = false;
//            outter_msg_prompt.setVisibility(View.GONE);
//
//        } else if (i == R.id.close_prompt) {
//            outter_msg_prompt.setVisibility(View.GONE);
//        } else if (i == R.id.outter_msg) {
//            if (!TextUtils.isEmpty(curOutterJid)) {
//                getHandler().removeCallbacks(showOutter);
//                outter_msg_prompt.setVisibility(View.GONE);
//                String draft = edit_msg.getText().toString();
//                if (!TextUtils.isEmpty(draft)) {
//                    InternDatas.putDraft(QtalkStringUtils.parseBareJid(jid), draft);
//                } else {
//                    InternDatas.removeDraft(QtalkStringUtils.parseBareJid(jid));
//                }
//                edit_msg.getText().clear();
//                Intent jumpConv = new Intent(this, ChatActivity.class);
//                jumpConv.putExtra("jid", curOutterJid);
//                jumpConv.putExtra("isFromChatRoom", curOutterJid.contains("@conference"));
//                startActivity(jumpConv);
//            }
//        } else if (i == R.id.txt_share_message) {
//            if (adapter.getSharingMsg().size() > 0) {
//                Intent selUser = new Intent(this, SearchUserActivity.class);
//                selUser.putExtra(Constants.BundleKey.IS_TRANS, true);
//                selUser.putExtra(Constants.BundleKey.TRANS_MSG, "share");
//                startActivity(selUser);
//            }
//        } else if (i == R.id.txt_collect_msg) {
//            if (adapter.getSharingMsg().size() == 0) return;
//            IFavourityMessagePresenter fPresenter = new FavourityMessagePresenter();
//            fPresenter.setFavourity(new IFavourityMsgView() {
//                @Override
//                public List<FavouriteMessage> getSelectedMsgs() {
//                    List<FavouriteMessage> messages = new ArrayList<FavouriteMessage>();
//                    for (IMMessage message : adapter.getSharingMsg()) {
//                        FavouriteMessage fMsg = new FavouriteMessage();
//                        fMsg.setId(UUID.randomUUID().toString());
//                        fMsg.setTextContent(JsonUtils.getGson().toJson(message));
//                        if (isFromChatRoom) {
//                            fMsg.setFromType("1");
//                        }
//                        fMsg.setFromUserId(message.getFromID());
//                        fMsg.setTime(Calendar.getInstance().getTime().getTime() + "");
//                        messages.add(fMsg);
//                    }
//                    return messages;
//                }
//
//                @Override
//                public void setFavourityMessages(List<FavouriteMessage> list) {
//                }
//            });
//            fPresenter.addFavourity();
//            cancelMore();
//        } else if (i == R.id.txt_del_msgs) {
//            if (adapter.getSharingMsg().size() == 0) return;
//            selectedMessages.clear();
//            selectedMessages.addAll(adapter.getSharingMsg());
//            chatingPresenter.deleteMessge();
//            selectedMessages.clear();
//            cancelMore();
//        } else if (i == R.id.txt_email_msg) {
//            if (adapter.getSharingMsg().size() == 0) return;
//            StringBuilder emailContent = new StringBuilder();
//            for (IMMessage message : adapter.getSharingMsg()) {
//                String nick = ProfileUtils.getNickByKey(QtalkStringUtils.parseResource(message.getFromID()));
//                String content = ChatTextHelper.showContentType(message.getBody(), message.getMsgType());
//                emailContent.append(nick);
//                emailContent.append(" ");
//                emailContent.append(message.getTime().toString());
//                emailContent.append("\n");
//                emailContent.append(content);
//                emailContent.append("\n");
//            }
//            Intent intent = new Intent(Intent.ACTION_SENDTO);
//            intent.putExtra(Intent.EXTRA_SUBJECT, CommonConfig.isQtalk ? "qtalk" : "qchat" + "聊天记录");
//            intent.putExtra(Intent.EXTRA_TEXT, emailContent.toString());
//            startActivity(Intent.createChooser(intent, getString(R.string.atom_ui_email)));
//        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int i = v.getId();
        if (i == R.id.edit_msg) {
            messgeInputFocusChangeListener(v, hasFocus);

        }
    }

    @Override
    public boolean getShowNick() {
        return adapter.getShowNick();
    }

    @Override
    public void setShowNick(boolean showNick) {
        if (isFromChatRoom) {
            if (showNick != adapter.getShowNick()) {
                adapter.setShowNick(showNick);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public String getJid() {
        return jid;
    }


    private final class OnAddFavoriteEmoticonClickListener implements FaceGridView.AddFavoriteEmojiconClickListener {

        @Override
        public void onAddFavoriteEmojiconClick() {
            Intent intent = new Intent(ChatActivity.this, ManageEmojiconActivity.class);
            intent.putExtra(PictureSelectorActivity.TYPE, PictureSelectorActivity.TYPE_EMOJICON);
            startActivityForResult(intent, ADD_EMOTICON);
        }
    }

    private final class FavoriteEmoticonOnClickListener implements FaceGridView.OnEmoticionsClickListener {

        @Override
        public void onEmoticonClick(EmoticonEntity entity, String pkgId) {
            imageUrl = entity.fileFiexd;
            if (imageUrl == null) {
                imageUrl = entity.fileOrg;
            }
            if (imageUrl != null) {
                chatingPresenter.sendImage();
            }
        }
    }

    private final class DefaultOnEmoticionsClickListener implements FaceGridView.OnEmoticionsClickListener {
        @Override
        public void onEmoticonClick(EmoticonEntity entity, String pkgId) {
            StringBuilder text = new StringBuilder();
            text.append((char) 0).
                    append(pkgId).
                    append((char) 1).
                    append(entity.shortCut).append((char) 255);
            int index = edit_msg.getSelectionStart();
            Editable edit = edit_msg.getEditableText();
            if (index < 0 || index > edit.length()) {
                edit.append(text);
            } else {
                edit.insert(index, text);
            }
        }
    }

    private final class ExtentionEmoticionsClickListener implements FaceGridView.OnEmoticionsClickListener {
        @Override
        public void onEmoticonClick(EmoticonEntity entity, String pkgId) {
            String originText = edit_msg.getText().toString();
            edit_msg.setText(((char) 0) + pkgId + ((char) 1) + entity.shortCut + ((char) 255));
            chatingPresenter.sendMsg();
            edit_msg.setText(originText);
        }
    }

    public void giveLuckyMoney(boolean isAA) {
        StringBuilder sb = new StringBuilder();
        String username = CurrentPreference.getInstance().getUserId();
        sb.append(isAA ? Constants.AA_PAY_URL : Constants.HONGBAO_URL)
                .append("?username=").append(username).append("&sign=")
                .append(BinaryUtil.MD5(username + Constants.SIGN_SALT))
                .append("&company=qunar&")
                .append(isFromChatRoom ? "group_id=" : "user_id=")
                .append(jid);
        Uri uri = Uri.parse(sb.toString());
        Intent intent = new Intent(this, QunarWebActvity.class);
        intent.putExtra(Constants.BundleKey.WEB_FROM, Constants.BundleValue.HONGBAO);
        intent.putExtra(QunarWebActvity.IS_HIDE_BAR, true);
        intent.setData(uri);
        startActivityForResult(intent, HONGBAO);
    }

    public void transferConversation() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.atom_ui_transfer_conversation_ad, null);
        final EditText etContext = (EditText) view.findViewById(R.id.et_content);
        final TextView cancel = (TextView) view.findViewById(R.id.txt_cancel);
        final TextView confirm = (TextView) view.findViewById(R.id.txt_confirm);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTransferConversationContext = etContext.getText().toString().trim();
                Intent selUser = new Intent(ChatActivity.this, SearchUserActivity.class);
                selUser.putExtra("requestcode", TRANSFER_CONVERSATION_REQUEST_CODE);
                startActivityForResult(selUser, TRANSFER_CONVERSATION_REQUEST_CODE);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    protected void handleRushOrderMsg(IMMessage message) {

    }

    protected class HandleChatEvent {

        public void onEventMainThread(EventBusEvent.UpdateVoiceMessage updateVoiceMessage) {
            if (updateVoiceMessage != null && updateVoiceMessage.message != null) {
                adapter.replaceItem(updateVoiceMessage.message);
                adapter.notifyDataSetChanged();
            }
        }

        public void onEvent(final EventBusEvent.HasNewMessageEvent event) {
            final IMMessage message = event.mMessage;
            if (message != null && message.getConversationID().equals(jid)) {
                if (message.getMsgType() == MessageType.MSG_TYPE_RUNSHING_ORDER_RESPONSE) {
                    getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            handleRushOrderMsg(message);
                        }
                    });
                } else if (message.getMsgType() == MessageType.REVOKE_MESSAGE) {
                    adapter.replaceItem(message);
                    chatingPresenter.receiveMsg(message);
                } else {
                    chatingPresenter.receiveMsg(message);
                }
            } else if (message != null) {
                if (!isShowOutterMsg) return;
                getHandler().removeCallbacks(showOutter);
                curOutterJid = QtalkStringUtils.parseBareJid(message.getFromID());
                boolean isShow = false;
                if (message.getType() == ConversitionType.MSG_TYPE_GROUP) {
                    String name = InternDatas.getName(curOutterJid);
                    if (TextUtils.isEmpty(name)) {
                        name = "讨论组";
                    }
                    final String finalName = name;
                    getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            outter_msg.setText(finalName + ":" + ChatTextHelper.showContentType(message.getBody(),
                                    message.getMsgType()));
                        }
                    });

                    isShow = true;
                } else if (message.getType() == ConversitionType.MSG_TYPE_CHAT
                        || message.getType() == ConversitionType.MSG_TYPE_TRANSFER) {
                    ProfileUtils.loadNickName(curOutterJid,
                            false, new ProfileUtils.LoadNickNameCallback() {
                                @Override
                                public void finish(String name) {
                                    outter_msg.setText(name + ":" + ChatTextHelper.showContentType(message.getBody(),
                                            message.getMsgType()));
                                }
                            });
                    isShow = true;
                }
                if (isShow) {
                    getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            outter_msg_prompt.setVisibility(View.VISIBLE);
                        }
                    });
                    getHandler().postDelayed(showOutter, 5000);
                }
            }
        }

        public void onEventMainThread(EventBusEvent.TypingEvent typingEvent) {
            if (typingEvent.jid != null && typingEvent.jid.equals(jid)) {
                String title = myActionBar.getTitleTextview().getText().toString();
                if (title.equals(typingPrompt))
                    return;
                titleTempVar = title;
                myActionBar.getTitleTextview().setText(typingPrompt);
                getHandler().postDelayed(typingShow, 4000);
            }
        }

        public void onEventMainThread(EventBusEvent.KinckoffChatroom kinckoffChatroom) {
            if (jid.equals(kinckoffChatroom.roomId)) {
                chatingPresenter.close();
                finish();
            }
        }

        public void onEventMainThread(EventBusEvent.DownEmojComplete downEmojComplete) {
            faceView.resetTabLayout();
        }

        public void onEventMainThread(EventBusEvent.RefreshMessageStatusEvent refreshMessageStatusEvent) {
            if (refreshMessageStatusEvent.jid.equals(jid)) {
                adapter.notifyDataSetChanged();
            }
        }

        public void onEventMainThread(EventBusEvent.RefreshChatroom refreshChatroom) {
            if (isFromChatRoom) {
                if (refreshChatroom.roomId != null && refreshChatroom.roomId.equals(jid)) {
                    if (refreshChatroom.roomName != null && myActionBar != null)
                        myActionBar.getTitleTextview().setText(refreshChatroom.roomName);
                }

            }
        }

        public void onEventMainThread(EventBusEvent.NewPictureEdit edit) {
            if (!TextUtils.isEmpty(edit.mPicturePath)) {
                imageUrl = edit.mPicturePath;
                chatingPresenter.sendImage();
            }
        }

        public void onEvent(final EventBusEvent.ReceivedHistory receivedHistory) {
            if (!TextUtils.isEmpty(receivedHistory.id)
                    && receivedHistory.id.equals(jid)) {
                BackgroundExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        chatingPresenter.reloadMessages();
                    }
                });
            }
        }

        public void onEventMainThread(EventBusEvent.SendTransMsg sendTransMsg) {
            final Serializable imMessage = sendTransMsg.msg;
            final String transJid = sendTransMsg.transId;
            if (imMessage == null) return;
            if (IMMessage.class.isInstance(imMessage)) {
                BackgroundExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        ChatActivity.this.transferId = transJid;
                        IMMessage selectedMessage = (IMMessage) imMessage;
                        selectedMessages.clear();
                        selectedMessages.add(selectedMessage);
                        chatingPresenter.transferMessage();
                        ChatActivity.this.transferId = null;
                        if (jid.equals(transJid)) {
                            chatingPresenter.receiveMsg(selectedMessage);
                        }
                        selectedMessages.clear();
                    }
                });
                Toast.makeText(ChatActivity.this, "转发成功!", Toast.LENGTH_LONG).show();
            } else if (String.class.isInstance(imMessage)) {
                if (imMessage.equals("share")) {
                    ChatActivity.this.transferId = transJid;
                    chatingPresenter.shareMessage(adapter.getSharingMsg());
                    ChatActivity.this.transferId = null;
                    cancelMore();
                }
            }
        }

        public void onEventMainThread(EventBusEvent.CleanHistory cleanHistory) {
            if (jid.equals(cleanHistory.jid)) {
                adapter.setMessages(new ArrayList<IMMessage>());
                chatingPresenter.reset();
            }
        }

    }

    private SensorManager mSensorManager;
    private Sensor mSensor;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (CommonConfig.isPlayVoice) {
            float range = event.values[0];
            if (range >= mSensor.getMaximumRange()) {
                MediaPlayerImpl.getInstance().changeNormalCall(this);
                LogUtil.d("onSensorChanged", "Normal");
            } else {
                MediaPlayerImpl.getInstance().changeVoiceCall(this);
                LogUtil.d("onSensorChanged", "Voice");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    void handleEmojiconResult(Intent data) {
        //添加自定义表情,刷新显示
        faceView.resetFavoriteEmotion(EmotionUtils.getFavoriteMap(this));
        faceView.resetFavoriteTab();
    }

    @Override
    public void didReceivedNotification(String key, Object... args) {
        Logger.i("收到单聊的消息推送:" + args[0]);
        ProtoMessageOuterClass.ProtoMessage protoMessage = (ProtoMessageOuterClass.ProtoMessage) args[0];
        Logger.i("单聊来自于谁:" + protoMessage.getFrom());
        Logger.i("本单聊是谁的:" + getJid());
        String from = protoMessage.getFrom();
        Logger.i("from:" + from);
        from = from.substring(0, from.indexOf("/"));
        Logger.i("from:" + from);
        Logger.i("自身from:" + getFromId());
        Logger.i("jid:" + getJid());
        Logger.i("这次的key:" + key);
        if (!from.equals(getJid())) {
            return;
        }
        switch (key) {
            case QtalkEvent.Chat_Message_Text:
//                protoMessageList.add(protoMessage);
////                pbChatViewAdapter.setDateList(protoMessageList);
//                chat_region.getRefreshableView().setSelection(chat_region.getRefreshableView().getCount() - 1);
                break;
        }
    }

    @Override
    public boolean isFromChatRoom() {
        return false;
    }

    @Override
    public void parseEncryptSignal(IMMessage message) {

    }

    @Override
    public void updateUploadProgress(IMMessage message, int progress, boolean isDone) {

    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void isEmotionAdd(boolean flag) {

    }

    @Override
    public void popNotice(NoticeBean noticeBean) {

    }

    @Override
    public void clearMessage() {

    }

    @Override
    public void sendRobotMsg(String msg) {

    }

    @Override
    public String getBackupInfo() {
        return null;
    }

    @Override
    public boolean isMessageExit(String msgId) {
        return false;
    }

    @Override
    public int getUnreadMsgCount() {
        return 0;
    }
}
