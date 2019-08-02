package com.qunar.im.ui.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qunar.im.utils.HttpUtil;
import com.qunar.im.base.jsonbean.NoticeBean;
import com.qunar.im.base.jsonbean.NoticeRequestBean;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.Utils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.PbChatActivity;
import com.qunar.im.ui.activity.QunarWebActvity;

import java.util.List;

/**
 * 提醒通知
 * Created by lihaibin.li on 2018/2/8.
 */

public class NoticeView extends LinearLayout {
    private TextView textView;//展示的文本
    private ImageView closeBtn;//关闭按钮

    private NoticeBean noticeBean;
    private RequestCallBack requestCallBack;
    private OnClickListener closeListener;

    public NoticeView(Context context) {
        super(context);
        init(context);
    }

    public NoticeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NoticeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.HORIZONTAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setGravity(Gravity.CENTER_VERTICAL);

        int padding = Utils.dipToPixels(context, 8);
        setPadding(padding, padding, padding, padding);

        textView = new TextView(context);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        textView.setLayoutParams(layoutParams);
//        textView.setText("test");

        addView(textView);

        closeBtn = new ImageView(context);
        closeBtn.setImageResource(R.drawable.atom_ui_close);
        addView(closeBtn);

        setBackgroundColor(Color.parseColor("#f8fbdf"));
    }

    public void setData(NoticeBean noticeBean) {
        if (noticeBean == null) return;
        this.noticeBean = noticeBean;
        List<NoticeBean.NoticeStrBean> strBeans = noticeBean.getNoticeStr();
        if (strBeans != null && !strBeans.isEmpty()) {
            textView.setText("");
            for (NoticeBean.NoticeStrBean bean : strBeans) {
                if (bean == null) continue;
                String str = bean.getStr();
                if (TextUtils.isEmpty(str)) continue;
                SpannableString spanString = new SpannableString(str);
                ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor(bean.getStrColor()));
                int start = 0;
                int end = str.length();
                spanString.setSpan(span, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                if (!"text".equals(bean.getType()))
                    spanString.setSpan(new MyClickableSpan(bean), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                textView.append(spanString);
            }
            textView.setMovementMethod(LinkMovementMethod.getInstance());//不设置 没有点击事件
        }
    }

    /**
     * 设置发送request监听
     *
     * @param requestCallBack
     */
    public void setRequestCallBack(RequestCallBack requestCallBack) {
        this.requestCallBack = requestCallBack;
    }

    interface RequestCallBack {
        void callBack(String response);
    }

    public void setCloseListener(OnClickListener closeListener) {
        this.closeListener = closeListener;
        closeBtn.setOnClickListener(this.closeListener);
    }


    /**
     * 内部类，用于截获点击富文本后的事件
     */
    class MyClickableSpan extends ClickableSpan {
        private NoticeBean.NoticeStrBean bean;
        private String type;

        public MyClickableSpan(NoticeBean.NoticeStrBean bean) {
            this.bean = bean;
            type = bean.getType();
        }


        @Override
        public void onClick(View widget) {
            try {
//                if(bean.isClose && closeListener != null){
//                    closeListener.onClick(widget);
//                }
                switch (type) {
                    case "link":
                        Intent intent = new Intent(getContext(), QunarWebActvity.class);
                        intent.putExtra(QunarWebActvity.IS_HIDE_BAR, false);
                        intent.setData(Uri.parse(bean.getUrl()));
                        getContext().startActivity(intent);
                        closeBtn.performClick();
                        break;
                    case "request":
                        if (!Utils.IsUrl(bean.getUrl())) {
                            return;
                        }
                        HttpUtil.requestGet(bean.getUrl(), new ProtocolCallback.UnitCallback<String>() {
                            @Override
                            public void onCompleted(String s) {
                                NoticeRequestBean nrb = JsonUtils.getGson().fromJson(s, NoticeRequestBean.class);
                                if (!(nrb != null && nrb.isRet())) {
                                    return;
                                }
                                NoticeRequestBean.DataBean dataBean = nrb.getData();
                                if(dataBean == null){
                                    return;
                                }
                                switch (dataBean.getType()) {
                                    case "link":
                                        Intent intent = new Intent(getContext(), QunarWebActvity.class);
                                        intent.putExtra(QunarWebActvity.IS_HIDE_BAR, false);
                                        intent.setData(Uri.parse(dataBean.getUrl()));
                                        getContext().startActivity(intent);
                                        closeBtn.performClick();
                                        break;
                                    case "newChat":
                                        intent = new Intent(getContext(), PbChatActivity.class);
                                        //设置jid 就是当前会话对象
                                        intent.putExtra(PbChatActivity.KEY_JID, bean.getTo());
                                        if (dataBean.isIsCouslt()) {


                                            if (dataBean.getCouslt().equals("4")) {
                                                if (CommonConfig.isQtalk) {

                                                    intent.putExtra(PbChatActivity.KEY_REAL_JID, dataBean.getRealTo());
                                                } else {
//                        //todo  不知道怎么走
                                                    intent.putExtra(PbChatActivity.KEY_REAL_JID, bean.getRealTo());
                                                }

                                            } else if (dataBean.getCouslt().equals("5")) {
                                                intent.putExtra(PbChatActivity.KEY_REAL_JID, dataBean.getRealTo());
                                            } else {
                                                intent.putExtra(PbChatActivity.KEY_REAL_JID, TextUtils.isEmpty(dataBean.getRealTo()) ? dataBean.getTo() : dataBean.getRealTo());
                                            }
                                        } else {
                                            intent.putExtra(PbChatActivity.KEY_REAL_JID, TextUtils.isEmpty(dataBean.getRealTo()) ? dataBean.getTo() : dataBean.getRealTo());
                                        }

                                        intent.putExtra(PbChatActivity.KEY_CHAT_TYPE, TextUtils.isEmpty(dataBean.getCouslt()) ? "0" : dataBean.getCouslt());
                                        //设置是否是群聊
//                    boolean isChatRoom = item.getConversationType() == ConversitionType.MSG_TYPE_GROUP;
                                        intent.putExtra(PbChatActivity.KEY_IS_CHATROOM, false);


                                        getContext().startActivity(intent);

                                        closeBtn.performClick();
                                        break;
                                }


//                                if (requestCallBack != null) {
//                                    Logger.i("通知请求的返回值:" + s);
//                                    requestCallBack.callBack(s);
//                                }
                            }

                            @Override
                            public void onFailure(String errMsg) {

                            }
                        });
                        break;
                    case "newChat":

                        intent = new Intent(getContext(), PbChatActivity.class);
                        //设置jid 就是当前会话对象

                        intent.putExtra(PbChatActivity.KEY_JID, bean.getTo());
                        if (bean.isIsCouslt()) {


                            if (bean.getCouslt().equals("4")) {
                                if (CommonConfig.isQtalk) {

                                    intent.putExtra(PbChatActivity.KEY_REAL_JID, bean.getRealTo());
                                } else {
//                        //todo  不知道怎么走
                                    intent.putExtra(PbChatActivity.KEY_REAL_JID, bean.getRealTo());
                                }

                            } else if (bean.getCouslt().equals("5")) {
                                intent.putExtra(PbChatActivity.KEY_REAL_JID, bean.getRealTo());
                            } else {
                                intent.putExtra(PbChatActivity.KEY_REAL_JID, TextUtils.isEmpty(bean.getRealTo()) ? bean.getTo() : bean.getRealTo());
                            }
                        } else {
                            intent.putExtra(PbChatActivity.KEY_REAL_JID, TextUtils.isEmpty(bean.getRealTo()) ? bean.getTo() : bean.getRealTo());
                        }

                        intent.putExtra(PbChatActivity.KEY_CHAT_TYPE, TextUtils.isEmpty(bean.getCouslt()) ? "0" : bean.getCouslt());
                        //设置是否是群聊
//                    boolean isChatRoom = item.getConversationType() == ConversitionType.MSG_TYPE_GROUP;
                        intent.putExtra(PbChatActivity.KEY_IS_CHATROOM, false);


                        getContext().startActivity(intent);

                        closeBtn.performClick();
                        break;
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), R.string.atom_ui_tips_error_unforeseen, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
//            ds.setColor(ds.linkColor);
            ds.setUnderlineText("text".equals(type) ? false : true);//超链接的下划线
        }
    }
}
