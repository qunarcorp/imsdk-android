package com.qunar.im.ui.view.quickreply;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.util.Utils;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.recyclerview.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class QuickReplyListView extends LinearLayout {
    private static final String TAG = "FaceGridView";

    private Context context;
    private ViewPager viewPager;
    private LinearLayout bottomDot;
    private int smallVSpacing = Utils.dipToPixels(QunarIMApp.getContext(), 22);
    private int bigVSpacing = Utils.dipToPixels(QunarIMApp.getContext(), 22);
    private int viewPaperHeight = Utils.dipToPixels(QunarIMApp.getContext(), 151);
    private QuickReplyListView.OnQuickRepliesClickListener defaultOnQuickReplyClickListener;
    private Map<String, List<String>> quickReplies = new LinkedHashMap<>();
    private OnPageChangedListener pageChangedListener;
    private SparseArray<String> pageKey = new SparseArray<>();
    private AddQuickReplyClickListener addFavoriteEmojiconClickListener;
    private List<RecyclerView> listViews;
    private ImageView[] imageViews;

    public QuickReplyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public QuickReplyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public QuickReplyListView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public void setPageChangedListener(OnPageChangedListener pageChangedListener) {
        this.pageChangedListener = pageChangedListener;
    }

    public void setDefaultOnQuickRepliesClickListener(QuickReplyListView.OnQuickRepliesClickListener defaultOnQuickRepliesClickListener) {
        this.defaultOnQuickReplyClickListener = defaultOnQuickRepliesClickListener;
    }

    private void init() {
        setOrientation(VERTICAL);
        viewPager = new ViewPager(context);
        bottomDot = new LinearLayout(context);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        params.weight = 1;
        viewPager.setLayoutParams(params);
        bottomDot.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                Utils.dipToPixels(context, 16)));
        bottomDot.setGravity(Gravity.CENTER);
        bottomDot.setOrientation(HORIZONTAL);
        addView(viewPager);
        addView(bottomDot);
    }


    private void setupEmoticons() {
        listViews = new ArrayList<>();

        int columnsNumber = 4;
        int itemCountPerPage = 8;

        int viewPagerPageSize = quickReplies.keySet().size();
        for(String groupname : quickReplies.keySet()) {
            listViews.add(getListView(quickReplies.get(groupname), defaultOnQuickReplyClickListener));
            pageKey.put(listViews.size(), groupname);
        }


        viewPager.setAdapter(new QuickReplyViewpagerAdapter(listViews));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int prePosition = 0;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int key = pageKey.keyAt(position);
                List<String> list = quickReplies.get(pageKey.get(key));
                if (pageChangedListener != null){
                    pageChangedListener.onChanged(prePosition > position, pageKey.get(key), list);
                }
                prePosition = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private RecyclerView getListView(final List<String> quickReplies, final OnQuickRepliesClickListener listener) {
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setVerticalScrollBarEnabled(false);
        recyclerView.setHorizontalScrollBarEnabled(false);
        recyclerView.setBackgroundColor(Color.TRANSPARENT);

        QuickReplyAdapter quickReplyAdapter = new QuickReplyAdapter(R.layout.atom_ui_quickreply_item, quickReplies);
        quickReplyAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                listener.onQuickReplyClick(quickReplies.get(position));
            }

        });
        recyclerView.setAdapter(quickReplyAdapter);
        return recyclerView;
    }

    public void setPage(String key){
        int index = pageKey.indexOfValue(key);
        viewPager.setCurrentItem(index < 0 ? 0 : index, false);
    }

    public void setQuickReplyMaps(Map<String, List<String>> quickReplies) {
        this.quickReplies = quickReplies;
        setupEmoticons();
    }

    public interface OnQuickRepliesClickListener {
        void onQuickReplyClick(String content);
    }

    public interface AddQuickReplyClickListener {
        void onAddQuickReplyClick();
    }

    /**
     * 快捷回复的ViewPager滑动时的回调监听
     */
    public interface OnPageChangedListener {
        /**
         *
         * @param list
         */
        void onChanged(boolean isleft, String groupname, List<String> list);
    }

}