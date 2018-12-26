package com.qunar.im.ui.view.baseView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.jsonbean.EncryptMsg;
import com.qunar.im.base.module.BaseIMMessage;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.util.AESTools;
import com.qunar.im.base.util.DataCenter;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.Utils;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.ChatViewAdapter;
import com.qunar.im.ui.util.ColorUtils;
import com.qunar.im.ui.util.ResourceUtils;
import com.qunar.im.ui.view.baseView.processor.MessageProcessor;
import com.qunar.im.ui.view.baseView.processor.ProcessorFactory;
import com.qunar.im.ui.view.bubbleLayout.BubbleLayout;


/**
 * Created by zhaokai on 15-8-10.
 */
public class IMChatBaseView extends RelativeLayout implements IMessageItem {
    protected Context context;
    protected TextView mTimeTextView;
    protected IMMessage message;
    protected int position;
    protected Handler handler;
    protected BubbleLayout bubble_layout_left, bubble_layout_right;
    protected TextView mLeftNickName, statusView, send_states_text;
    protected SimpleDraweeView mLeftRoundedImageView;
    protected SimpleDraweeView mRightRoundedImageView;
    protected LinearLayout mLeftChatView, mRightChatView;
    protected RelativeLayout mLeftWrapper;
    protected LinearLayout mRightWrapper;
    protected ProgressBar mLeftProgressBar, mRightProgressBar;
    protected ImageView mLeftSendFailureImageView, mRightSendFailureImageView;
    protected ChatViewAdapter.LeftImageLongClickHandler leftImageLongClickHandler;
    protected ChatViewAdapter.LeftImageClickHandler leftImageClickHandler;
    protected ChatViewAdapter.GravatarHandler gravatarHandler;
    protected ChatViewAdapter.ContextMenuRegister contextMenuRegister;
    protected ChatViewAdapter.RightSendFailureClickHandler rightSendFailureClickHandler;
    //核心连接管理类
    protected ConnectionUtil connectionUtil;
    //默认头像路径
    private static String defaultHeadUrl = QtalkNavicationService.getInstance().getSimpleapiurl() + "/file/v2/download/perm/3ca05f2d92f6c0034ac9aee14d341fc7.png";

    protected int m8dp;
    protected int m48dp;

    protected boolean showReadState;

    public IMChatBaseView(Context context) {
        this(context, null);
    }

    public IMChatBaseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IMChatBaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context);
        m8dp = Utils.dipToPixels(context, 8);
        m48dp = Utils.dipToPixels(context, 48);
    }

    private void init(Context context) {
        this.context = context;
        connectionUtil = ConnectionUtil.getInstance();

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findViewById();
    }

    private void setLeftVisible() {
        mLeftNickName.setVisibility(VISIBLE);
        mLeftChatView.setVisibility(VISIBLE);
        mLeftWrapper.setVisibility(VISIBLE);
        mLeftRoundedImageView.setVisibility(VISIBLE);
    }

    private void setRightVisible() {
        mRightChatView.setVisibility(VISIBLE);
        mRightWrapper.setVisibility(VISIBLE);
        mRightRoundedImageView.setVisibility(VISIBLE);
    }

    protected void setInVisible() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).setVisibility(GONE);
        }
    }

    protected void findViewById() {
        mLeftWrapper = findViewById(R.id.chatview_left_wrapper);
        mRightWrapper = findViewById(R.id.chatview_right_wrapper);
        mLeftChatView = findViewById(R.id.chatview_left);
        mLeftNickName = findViewById(R.id.nickname_left);
        mLeftProgressBar = findViewById(R.id.message_progress_left);
        mLeftSendFailureImageView = findViewById(R.id.send_failure_icon_left);
        mLeftRoundedImageView = findViewById(R.id.imageview_left);
        mRightChatView = findViewById(R.id.chatview_right);
        mRightProgressBar = findViewById(R.id.message_progress_right);
        mRightSendFailureImageView = findViewById(R.id.send_failure_icon_right);
        mRightRoundedImageView = findViewById(R.id.imageview_right);
        mTimeTextView = findViewById(R.id.textview_time);
        statusView = findViewById(R.id.txt_status);
        send_states_text = findViewById(R.id.send_states_text);
        bubble_layout_left = findViewById(R.id.bubble_layout_left);
        bubble_layout_right = findViewById(R.id.bubble_layout_right);
//        mLeftChatView.setBackgroundResource(R.drawable.atom_ui_balloon_left);
//        mRightChatView.setBackgroundResource(R.drawable.atom_ui_balloon_right);
    }
    public void setReadStateShow(boolean show){
        if(show){
            send_states_text.setVisibility(VISIBLE);
        }else{
            send_states_text.setVisibility(GONE);
        }
    }


    public void setNickStatus(boolean showNick) {
//        setReadStateShow(showNick);
        if (showNick) {
            mLeftNickName.setVisibility(VISIBLE);
            LayoutParams params = (LayoutParams) mLeftWrapper.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_TOP, 0);
            params.setMargins(0, m8dp, m48dp, m8dp);
            mLeftWrapper.setLayoutParams(params);


        } else {
            mLeftNickName.setVisibility(GONE);
            LayoutParams params = (LayoutParams) mLeftWrapper.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_TOP, R.id.imageview_left);
            params.setMargins(0, 0, m48dp, m8dp);
            mLeftWrapper.setLayoutParams(params);

        }
    }

    public void initFontSize() {
        switch (com.qunar.im.protobuf.common.CurrentPreference.getInstance().getFontSizeMode()) {
            case 1:
                mLeftNickName.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        context.getResources().
                                getDimensionPixelSize(R.dimen.atom_ui_text_size_smaller)
                                - ResourceUtils.getFontSizeIntervalPX(context));
                mTimeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        context.getResources().getDimensionPixelSize(R.dimen.atom_ui_text_size_small)
                                - ResourceUtils.getFontSizeIntervalPX(context));
                break;
            case 2:
                mLeftNickName.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        context.getResources().getDimensionPixelSize(R.dimen.atom_ui_text_size_smaller));
                mTimeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        context.getResources().getDimensionPixelSize(R.dimen.atom_ui_text_size_small));
                break;
            case 3:
                mLeftNickName.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        context.getResources().getDimensionPixelSize(R.dimen.atom_ui_text_size_smaller)
                                + ResourceUtils.getFontSizeIntervalPX(context));
                mTimeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        context.getResources().getDimensionPixelSize(R.dimen.atom_ui_text_size_small)
                                + ResourceUtils.getFontSizeIntervalPX(context));
                break;
        }
    }

    private int getEncryptMessageType(IMMessage message) {
        int msgType = ProtoMessageOuterClass.MessageType.MessageTypeText_VALUE;
        String encryptUid = message.getDirection() == IMMessage.DIRECTION_SEND ? message.getToID() : message.getConversationID();
        String password;
        if (DataCenter.decryptUsers.containsKey(encryptUid)) {
            password = DataCenter.decryptUsers.get(encryptUid);
        } else password = DataCenter.encryptUsers.get(encryptUid);
        if (!TextUtils.isEmpty(password)) {//存在密码解密
            try {
                EncryptMsg encryptMsg = JsonUtils.getGson().fromJson(AESTools.decodeFromBase64(password, message.getExt()), EncryptMsg.class);
                if (encryptMsg != null)
                    msgType = encryptMsg.MsgType;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return msgType;
    }

    public void setMessage(ChatViewAdapter adapter, Handler handler, final IMMessage message, int position) {
        this.message = message;
        this.position = position;
        this.handler = handler;

        MessageProcessor p;
        if (message.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeEncrypt_VALUE) {//加密会话消息
            p = ProcessorFactory.getProcessorMap().get(getEncryptMessageType(message));
        } else {
            p = ProcessorFactory.getProcessorMap().get(message.getMsgType());
        }
        if (p == null) {
            p = ProcessorFactory.getProcessorMap().get(ProcessorFactory.DEFAULT_PROCESSOR);
        }
//        final String nickOrUid = message.getType() == ConversitionType.MSG_TYPE_GROUP ?
//                message.getFromID() : QtalkStringUtils.parseBareJid(message.getFromID());
        if (message.getDirection() == IMMessage.DIRECTION_SEND) {
            setRightVisible();
            message.position = BaseIMMessage.RIGHT;
            if (mRightChatView.getChildCount() > 0) {
                for (int index = 0; index < mRightChatView.getChildCount(); index++) {
                    ViewPool.recycleView(mRightChatView.getChildAt(index));
                }
                mRightChatView.removeAllViews();
            }
            p.processErrorSendingView(mRightProgressBar,mRightSendFailureImageView,this);
//            p.processProgressbar(mRightProgressBar, this);
            p.processTimeText(mTimeTextView, this, adapter);
//            p.processErrorImageView(mRightSendFailureImageView, this);
            p.processChatView(mRightChatView, this);
            p.processBubbleView(bubble_layout_right, this);
            p.processSendStatesView(send_states_text, this);

            for (int i = 0; i < mRightChatView.getChildCount(); i++) {
                mRightChatView.getChildAt(i).setOnLongClickListener(null);
            }
            if (gravatarHandler != null) {
                //如果是null 证明是单人消息
//                Nick selectnick = connectionUtil.getUserCardByName(message.getNickName());

//                if(TextUtils.isEmpty(message.getNickName())){
                connectionUtil.getUserCard(message.getFromID(), new IMLogicManager.NickCallBack() {
                    @Override
                    public void onNickCallBack(final Nick nick) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (nick != null) {
                                    message.setNick(nick);
                                    gravatarHandler.requestGravatarEvent(nick.getXmppId(), nick.getHeaderSrc(), mRightRoundedImageView);

                                } else {
                                    gravatarHandler.requestGravatarEvent(message.getFromID(), defaultHeadUrl, mRightRoundedImageView);
                                }
                            }
                        });


                    }
                }, false, false);
//                }
//                Nick selectnick = connectionUtil.getUserCardByName(message.getNickName());
//                gravatarHandler.requestGravatarEvent(nickOrUid, mRightRoundedImageView);
            }
            mRightChatView.setTag(message);
            if (contextMenuRegister != null) {
                contextMenuRegister.registerContextMenu(mRightChatView);
            }
            mRightSendFailureImageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (rightSendFailureClickHandler != null) {
                        rightSendFailureClickHandler.resendMessage(message);
                    }
                }
            });
        } else if (message.getDirection() == IMMessage.DIRECTION_RECV) {
            setLeftVisible();
            message.position = BaseIMMessage.LEFT;
            //现在可以直接设置名字

//            ProfileUtils.loadNickName(nickOrUid,mLeftNickName,
//                     true);
            if (mLeftChatView.getChildCount() > 0) {
                for (int index = 0; index < mLeftChatView.getChildCount(); index++) {
                    ViewPool.recycleView(mLeftChatView.getChildAt(index));
                }
                mLeftChatView.removeAllViews();
            }
//            p.processProgressbar(mLeftProgressBar, this);
            p.processTimeText(mTimeTextView, this, adapter);
//            p.processErrorImageView(mLeftSendFailureImageView, this);
//            p.processErrorSendingView(mLeftProgressBar,mLeftSendFailureImageView,this);
            p.processChatView(mLeftChatView, this);
            p.processBubbleView(bubble_layout_left, this);
            for (int i = 0; i < mLeftChatView.getChildCount(); i++) {
                mLeftChatView.getChildAt(i).setOnLongClickListener(null);
            }
            mLeftRoundedImageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (leftImageClickHandler != null) {
                        if (message.getNick() == null) return;
                        leftImageClickHandler.onLeftImageClickEvent(message.getNick().getXmppId());
                    }
                }
            });
            mLeftRoundedImageView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (leftImageLongClickHandler != null)
                        leftImageLongClickHandler.
                                onLeftImageLongClickEvent(message.getFromID());
                    return true;
                }
            });
            mLeftNickName.setTextColor(Color.parseColor(ColorUtils.getColorFromHash(message.getFromID())));
            if (gravatarHandler != null) {
//                Nick selectnick = connectionUtil.getUserCardByName(message.getNickName());
                if (message.isCollection()) {
                    mLeftRoundedImageView.setOnClickListener(null);
                    mLeftRoundedImageView.setOnLongClickListener(null);
                    connectionUtil.getCollectionUserCard(message.getRealfrom(), new IMLogicManager.NickCallBack() {
                        @Override
                        public void onNickCallBack(final Nick nick) {
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (nick != null) {
                                        message.setNick(nick);
                                        gravatarHandler.requestGravatarEvent(nick.getXmppId(), TextUtils.isEmpty(nick.getHeaderSrc()) ? defaultHeadUrl : nick.getHeaderSrc(), mLeftRoundedImageView);
                                        mLeftNickName.setText(TextUtils.isEmpty(nick.getName()) ? nick.getXmppId() : nick.getName());
                                    } else {
                                        gravatarHandler.requestGravatarEvent(message.getFromID(), defaultHeadUrl, mLeftRoundedImageView);
                                    }
                                }
                            });
                        }
                    }, false, false);
                } else {
                    connectionUtil.getUserCard(message.getRealfrom(), new IMLogicManager.NickCallBack() {
                        @Override
                        public void onNickCallBack(final Nick nick) {
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (nick != null) {
                                        message.setNick(nick);
                                        gravatarHandler.requestGravatarEvent(nick.getXmppId(), TextUtils.isEmpty(nick.getHeaderSrc()) ? defaultHeadUrl : nick.getHeaderSrc(), mLeftRoundedImageView);
                                        String markupName = CurrentPreference.getInstance().getMarkupNames().get(nick.getXmppId());
                                        if(TextUtils.isEmpty(markupName))
                                            mLeftNickName.setText(TextUtils.isEmpty(nick.getName()) ? nick.getXmppId() : nick.getName());
                                        else mLeftNickName.setText(markupName);
                                    } else {
                                        gravatarHandler.requestGravatarEvent(message.getFromID(), defaultHeadUrl, mLeftRoundedImageView);
                                    }
                                }
                            });

                        }
                    }, false, false);
                }
//                gravatarHandler.requestGravatarEvent(nickOrUid, mLeftRoundedImageView);
            }
            mLeftChatView.setTag(message);
            if (contextMenuRegister != null) {
                contextMenuRegister.registerContextMenu(mLeftChatView);
            }
        } else if (message.getDirection() == IMMessage.DIRECTION_MIDDLE) {
            mTimeTextView.setVisibility(View.VISIBLE);
            mTimeTextView.setText(Html.fromHtml(message.getBody()));
            //拦截url链接
            handleUrlSpan();
        }
    }

    private void handleUrlSpan(){
        mTimeTextView.setMovementMethod(LinkMovementMethod.getInstance());
        CharSequence text = mTimeTextView.getText();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        if(text instanceof Spannable) {
            int end = text.length();
            Spannable spannable = (Spannable) mTimeTextView.getText();
            URLSpan[] urlSpans = spannable.getSpans(0, end, URLSpan.class);
            if (urlSpans.length == 0) {
                return;
            }
            // 循环遍历并拦截
            for (URLSpan uri : urlSpans) {
                String url = uri.getURL();
                if (url.indexOf("http://") == 0 || url.indexOf("https://") == 0) {
                    CustomUrlSpan customUrlSpan = new CustomUrlSpan(context, url);
                    spannableStringBuilder.setSpan(customUrlSpan, spannable.getSpanStart(uri),
                            spannable.getSpanEnd(uri), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                spannableStringBuilder.removeSpan(uri);
            }
            mTimeTextView.setText(spannableStringBuilder);
        }
    }

    public void setLeftImageLongClickHandler(ChatViewAdapter.LeftImageLongClickHandler leftImageLongClickHandler) {
        this.leftImageLongClickHandler = leftImageLongClickHandler;
    }

    public void setLeftImageClickHandler(ChatViewAdapter.LeftImageClickHandler leftImageClickHandler) {
        this.leftImageClickHandler = leftImageClickHandler;
    }

    public void setGravatarHandler(ChatViewAdapter.GravatarHandler gravatarHandler) {
        this.gravatarHandler = gravatarHandler;
    }

    public void setContextMenuRegister(ChatViewAdapter.ContextMenuRegister register) {
        this.contextMenuRegister = register;
    }

    public void setRightSendFailureClickHandler(ChatViewAdapter.RightSendFailureClickHandler handler) {
        this.rightSendFailureClickHandler = handler;
    }

    @Override
    public IMMessage getMessage() {
        return this.message;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public Handler getHandler() {
        return com.qunar.im.common.CommonConfig.mainhandler;
    }

    @Override
    public ProgressBar getProgressBar() {
        if (message.getDirection() == IMMessage.DIRECTION_SEND) {
            return mRightProgressBar;
        } else {
            return mLeftProgressBar;
        }
    }

    @Override
    public ImageView getErrImageView() {
        if (message.getDirection() == IMMessage.DIRECTION_SEND) {
            return mRightSendFailureImageView;
        } else {
            return mLeftSendFailureImageView;
        }
    }

    @Override
    public TextView getStatusView() {
        return statusView;
    }

}
