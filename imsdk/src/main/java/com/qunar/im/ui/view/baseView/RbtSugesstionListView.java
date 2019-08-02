package com.qunar.im.ui.view.baseView;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.jsonbean.RbtSuggestionListJson;
import com.qunar.im.ui.presenter.views.IChatView;
import com.qunar.im.base.protocol.HttpRequestCallback;
import com.qunar.im.base.protocol.HttpUrlConnectionHandler;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.util.Utils;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.IconView;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by xinbo.wang on 2017-04-07.
 */
public class RbtSugesstionListView extends LinearLayout {

    private String ExpendedKey;
    private static int sTagKey = 100;
    private LinearLayout subll = null;
    private int maxshow = 0;
    private RbtSuggestionListJson data;
    private ViewGroup parent = null;
    private Set<String> expendedItemKey = null;

    final int lineSpace = Utils.dipToPixels(getContext(), 8);
    final int minHeight = Utils.dipToPixels(getContext(), 32);


    public RbtSugesstionListView(Context context) {
        this(context, null);
    }

    public RbtSugesstionListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public RbtSugesstionListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RbtSugesstionListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        setLayoutParams(layoutParams);
        setOrientation(LinearLayout.VERTICAL);
    }

    @TargetApi(Build.VERSION_CODES.DONUT)
    public void bindData(final RbtSuggestionListJson data, IMessageItem item) {
        // 检查是否是展开的视图的初始化
//        this.parent = parent;
//        this.ExpendedKey = "EXPENDED_"+id;
//        Object object = this.parent.getTag(sTagKey);
//        if(null == object || !(object instanceof HashSet )){
//            object = new HashSet<>();
//            this.parent.setTag(sTagKey,object);
//        }
//        expendedItemKey = (Set<String>) object;


        final boolean isEnable = true;

        this.data = data;

        final LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);


        // 添加一个带答案的内容消息
        if (!TextUtils.isEmpty(data.content)) {
            addSplitView(this);
            String title = data.content;
            TextView titleView = new TextView(getContext());
            titleView.setTextColor(getContext().getResources().getColor(R.color.atom_rtc_light_gray_33));
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            titleView.setLayoutParams(layoutParams);
            titleView.setPadding(lineSpace, lineSpace,
                    lineSpace, lineSpace);
            titleView.setText(title);
            titleView.setMinHeight(minHeight);
            addView(titleView);

        }

        // 添加问题的头列表
        if (!TextUtils.isEmpty(data.listTips)) {
            addSplitView(this);
            String tips = data.listTips;
            TextView titleView = new TextView(getContext());
            titleView.setTextColor(getContext().getResources().getColor(R.color.atom_rtc_light_gray_33));
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            titleView.setLayoutParams(layoutParams);
            titleView.setPadding(lineSpace, lineSpace,
                    lineSpace, lineSpace);
            titleView.setText(tips);
            titleView.setMinHeight(minHeight);
            addView(titleView);
        }

        // 获取到默认显示多少个问题
        int defaultSize = Integer.MAX_VALUE;
        if (null != data.listArea && null != data.listArea.style && 0 < data.listArea.style.defSize) {
            defaultSize = data.listArea.style.defSize;
            maxshow = defaultSize;
        }

        // 显示问题
        if (null != data.listArea
                && null != data.listArea.items
                && data.listArea.items.size() > 0) {

            subll = new LinearLayout(getContext());
            subll.setOrientation(VERTICAL);

            addSplitView(this);
            addView(subll);

            if (null != expendedItemKey && expendedItemKey.contains(ExpendedKey)) {
                for (int i = 0; i < data.listArea.items.size(); i++) {
                    // 画一个问题按钮
                    addSuggesstion(data.listArea.items.get(i));
                }
                addLessButton();

            } else {
                for (int i = 0; i < data.listArea.items.size(); i++) {

                    if (i >= defaultSize) {
                        // 画一个更多按钮
                        addMoreButton();
                        break;
                    } else {
                        // 画一个问题按钮
                        addSuggesstion(data.listArea.items.get(i));
                    }
                }
            }
        }

        // 显示hints
//        if (null != item && null!=item.getDownNoticeView()){
//            ViewGroup viewGroup  = item.getDownNoticeView();
//
//            TextView titleView = new TextView(getContext());
//            titleView.setTextColor(getContext().getResources().getColor(R.color.pub_imsdk_mm_light_gray_33));
//            titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
//            titleView.setLayoutParams(layoutParams);
//            titleView.setPadding(lineSpace, lineSpace,
//                    lineSpace, lineSpace);
//            titleView.setText("123123");
//            titleView.setMinHeight(minHeight);
//            viewGroup.addView(titleView);
//
//            viewGroup.setVisibility(VISIBLE);
//        }

    }


    private void addSplitView(ViewGroup viewGroup) {
        if (null == viewGroup)
            return;

        if (viewGroup.getChildCount() > 0) {
            final View view = new View(getContext());
            view.setBackgroundResource(R.color.atom_ui_light_gray_DD);
            view.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            viewGroup.addView(view);
        }
    }


    private void addLessButton() {
        addSplitView(this);
        final IconView packup = new IconView(getContext());

        packup.setTextColor(getResources().getColorStateList(R.color.atom_ui_new_color_unselect));
        packup.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        packup.setPadding(lineSpace, lineSpace,
                lineSpace, lineSpace);
        packup.setGravity(Gravity.CENTER);
        packup.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        String packupTxt = "收起 ";
//        SpannableStringBuilder sb = new SpannableStringBuilder(packupTxt + getContext().getResources().getString(R.string.atom_im_up_arrow));
//        ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.parseColor("#616161"));
//        sb.setSpan(redSpan, packupTxt.length() - 1, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        packup.setText(sb);
        packup.setText(packupTxt);

        packup.setOnClickListener(new OnClickListener() {
            @TargetApi(Build.VERSION_CODES.DONUT)
            @Override
            public void onClick(View v) {
                // 删除更多这个视图
                removeView(v);
                removeView(getChildAt(getChildCount() - 1));
                do {
                    int cnt = subll.getChildCount();
                    if (cnt > maxshow * 2) {
                        View view = subll.getChildAt(cnt - 1);
                        subll.removeView(view);
                        continue;
                    }
                    break;
                } while (true);

                addMoreButton();

                if (null != expendedItemKey && null != ExpendedKey && null != parent) {
                    expendedItemKey.remove(ExpendedKey);
                    parent.setTag(sTagKey, expendedItemKey);
                }

            }
        });
        addView(packup);
    }


    private void addMoreButton() {

        // 长长的分割线
        addSplitView(this);
        final IconView showMore = new IconView(getContext());
        showMore.setTextColor(getResources().getColorStateList(R.color.atom_ui_new_color_unselect));

        showMore.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        showMore.setPadding(lineSpace, lineSpace,
                lineSpace, lineSpace);
        showMore.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        String showMoreTxt = "查看更多问题 ";
//        SpannableStringBuilder sb = new SpannableStringBuilder(
//                showMoreTxt + getContext().getResources().getString(R.string.atom_im_down_arrow));
//        ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.parseColor("#616161"));
//        sb.setSpan(redSpan, showMoreTxt.length() - 1, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        showMore.setText(sb);
        showMore.setText(showMoreTxt);


        showMore.setOnClickListener(new OnClickListener() {
            @TargetApi(Build.VERSION_CODES.DONUT)
            @Override
            public void onClick(View v) {
                // 删除更多这个视图
                removeView(v);
                removeView(getChildAt(getChildCount() - 1));

                for (int i = maxshow; i < data.listArea.items.size(); i++) {
                    addSuggesstion(data.listArea.items.get(i));
                }
                addLessButton();

                Rect global = new Rect();
                Rect local = new Rect();
                getGlobalVisibleRect(global);
                getLocalVisibleRect(local);
//                int h = global.bottom - local.bottom;
//                if (getContext() instanceof IChatView)
//                    ((IChatView) getContext()).soomthRereshableView(h);

                if (null != expendedItemKey && null != ExpendedKey && null != parent) {
                    expendedItemKey.add(ExpendedKey);
                    parent.setTag(sTagKey, expendedItemKey);
                }
            }
        });

        addView(showMore);
    }


    private void addSuggesstion(RbtSuggestionListJson.Item item) {
        // 添加一个横线视图
        addSplitView(subll);
        // 添加问题视图

        final RbtSuggestionListJson.ItemEvent event = item.event;
        final String content = item.text;

        TextView itemView = new TextView(getContext());
        itemView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        itemView.setTextColor(getContext().getResources().getColorStateList(R.color.atom_ui_light_green2c));
        itemView.setText(content);
        itemView.setGravity(Gravity.CENTER_VERTICAL);
        itemView.setBackgroundResource(R.color.atom_ui_white);

        itemView.setPadding(lineSpace, lineSpace,
                lineSpace, lineSpace);
        itemView.setMinHeight(minHeight);

        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {

                do {
                    if (null == event)
                        break;

                    if (!TextUtils.isEmpty(event.msgText))
                        if (getContext() instanceof IChatView) {
                            ((IChatView) getContext()).sendRobotMsg(event.msgText);
                        }
//                        EventBus.getDefault().post(new QchatConsultEvent.showMsg(event.msgText));

                    if (TextUtils.isEmpty(event.url))
                        break;

                    if ("interface".equalsIgnoreCase(event.type)) {
                        String nameAndValues = Protocol.makeQVTHeader();
                        Map<String, String> cookie = new HashMap<>();
                        cookie.put("Cookie", "cookie=" + nameAndValues);
                        Logger.i("点击问题列表 url：" + event.url);

                        HttpUrlConnectionHandler.executeGet(event.url, cookie, new HttpRequestCallback() {
                            @Override
                            public void onComplete(InputStream response) {

                            }

                            @Override
                            public void onFailure(Exception e) {

                            }
                        });
                        break;
                    }

//                    if ("forward".equalsIgnoreCase(event.type)) {
//                        String schema = ProtocolUtils.makesureAdrSchema(event.url);
//                        SchemeDispatcher.sendScheme(getContext(), schema);
//                        break;
//                    }

                } while (false);
            }
        };
        itemView.setOnClickListener(onClickListener);

        subll.addView(itemView);
    }
}
