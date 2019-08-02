package com.qunar.im.ui.view.quickreply;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qunar.im.base.util.Utils;
import com.qunar.im.ui.R;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class QuickReplyLayout extends LinearLayout {
    Context context;
    private QuickReplyListView quickReplyListView;
    private LinearLayout tabLayout;
    private View selectedTab;
    private TextView emptyView;
    private HorizontalScrollView scrollView;
    private boolean isInit;
    private Map<String, List<String>> quickReplies;
    private int selectTabColor = R.color.atom_ui_qchat_logo_color;

    public QuickReplyLayout(Context context) {
        this(context, null);
    }

    public QuickReplyLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void setOnQuickReplyClickListenter(QuickReplyListView.OnQuickRepliesClickListener listenter){
        quickReplyListView.setDefaultOnQuickRepliesClickListener(listenter);
    }

    public QuickReplyLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.atom_ui_layout_quickreply, this);
        this.quickReplyListView = findViewById(R.id.quickreply_rview);
        this.tabLayout = findViewById(R.id.quickreply_tab_layout);
        scrollView = findViewById(R.id.quickreply_scroll);
        emptyView = findViewById(R.id.empty_view);
        quickReplyListView.setPageChangedListener(new QuickReplyListView.OnPageChangedListener() {
            @Override
            public void onChanged(boolean isleft, String groupname, List<String> list) {
                int[] tabPos = new int[]{0, 0};
                int[] scrollPos = new int[]{0, 0};
                selectedTab.getLocationInWindow(tabPos);
                scrollView.getLocationInWindow(scrollPos);
                if(isleft) {
                    if (tabPos[0] - selectedTab.getWidth() < scrollPos[0]) {
                        //在ScrollView 左侧外
                        scrollView.smoothScrollBy(tabPos[0] - selectedTab.getWidth() - scrollPos[0], 0);
                    }
                } else {
                    if (tabPos[0] + 2 * selectedTab.getWidth() > scrollPos[0] + scrollView.getWidth()) {
                        //在ScrollView 右侧外
                        scrollView.smoothScrollBy(-scrollPos[0] - scrollView.getWidth() + tabPos[0] + 2 * selectedTab.getWidth(),0);
                    }
                }

                selectedTab.setBackgroundResource(R.color.atom_ui_white);
                View v = findViewWithTag(groupname);
                if (v != null) {
                    selectedTab = v;
                }

                selectedTab.setBackgroundResource(selectTabColor);
            }
        });
    }

    public void initFaceGridView(Map<String, List<String>> quickReplies) {
        this.quickReplies = quickReplies;
        if(quickReplies != null && quickReplies.size() > 0) {
            emptyView.setVisibility(View.GONE);
            quickReplyListView.setVisibility(View.VISIBLE);
            initTabLayout();
            quickReplyListView.setQuickReplyMaps(quickReplies);
            quickReplyListView.setPage(quickReplies.keySet().iterator().next());
            isInit = true;
        } else {
            emptyView.setVisibility(View.VISIBLE);
            quickReplyListView.setVisibility(View.GONE);
        }
    }

    public boolean isInitialize() {
        return isInit;
    }

    private void initTabLayout() {
        if (quickReplies != null && quickReplies.keySet().size() > 0) {
            tabLayout.removeAllViews();
            for (String s : quickReplies.keySet()) {
                TextView textView = new TextView(context);
                textView.setLayoutParams(new LayoutParams(Utils.getScreenWidth(context) / 4, ViewGroup.LayoutParams.MATCH_PARENT));
                textView.setText(s);
                textView.setMaxLines(1);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setTag(s);
                textView.setPadding(8, 0, 8, 0);
                textView.setGravity(Gravity.CENTER);
                textView.setOnClickListener(new TabOnClickListener());

                TextView splitView = new TextView(context);
                splitView.setLayoutParams(new LayoutParams(Utils.dpToPx(context, 1), ViewGroup.LayoutParams.MATCH_PARENT));
                splitView.setBackgroundResource(R.color.atom_ui_light_gray_DD);

                tabLayout.addView(textView);
                tabLayout.addView(splitView);
            }
            selectedTab = tabLayout.getChildAt(0);
            selectedTab.setBackgroundResource(selectTabColor);
        }
    }

    private void setTextViewDrawable(String path, final ImageView imageView){
        Glide.with(context)
                .load(Uri.fromFile(new File(path)))
                .asBitmap()
                .override(72, 72)
                .into(imageView);
    }


    final class TabOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (selectedTab == v || v == null) {
                return;
            }
            v.setBackgroundResource(selectTabColor);
            selectedTab.setBackgroundResource(R.color.atom_ui_white);
            quickReplyListView.setPage(v.getTag().toString());
            selectedTab = v;
        }
    }


}