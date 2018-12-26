package com.qunar.im.ui.view.faceGridView;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.qunar.im.base.view.faceGridView.EmoticionMap;
import com.qunar.im.base.view.faceGridView.EmoticonEntity;
import com.qunar.im.ui.adapter.FavoriteFaceAdapter;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xinbo.wang on 2015/2/5.
 */
public class FaceGridView extends LinearLayout {
    private static final String TAG = "FaceGridView";

    private Context context;
    private ViewPager viewPager;
    private LinearLayout bottomDot;
    private int smallVSpacing = Utils.dipToPixels(QunarIMApp.getContext(), 22);
    private int bigVSpacing = Utils.dipToPixels(QunarIMApp.getContext(), 22);
    private int viewPaperHeight = Utils.dipToPixels(QunarIMApp.getContext(), 151);
    private FaceGridView.OnEmoticionsClickListener defaultOnEmoticionsClickListener, othersOnEmoricionsClickListener, favoriteEmojiconOnClickListener;
    private EmoticionMap defaultMap,defaultMap1, favoriteMap;
    private Map<String, EmoticionMap> otherMap;
    private OnPageChangedListener pageChangedListener;
    private SparseArray<EmoticionMap> pageKey = new SparseArray<>();
    private AddFavoriteEmojiconClickListener addFavoriteEmojiconClickListener;
    private List<GridView> listViews;
    private ImageView[] imageViews;

    public FaceGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public FaceGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public FaceGridView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public void setPageChangedListener(OnPageChangedListener pageChangedListener) {
        this.pageChangedListener = pageChangedListener;
    }

    public void setDefaultOnEmoticionsClickListener(FaceGridView.OnEmoticionsClickListener defaultOnEmoticionsClickListener) {
        this.defaultOnEmoticionsClickListener = defaultOnEmoticionsClickListener;
    }

    public void setOthersOnEmoricionsClickListener(FaceGridView.OnEmoticionsClickListener othersOnEmoricionsClickListener) {
        this.othersOnEmoricionsClickListener = othersOnEmoricionsClickListener;
    }

    public void setFavoriteEmojiconOnClickListener(FaceGridView.OnEmoticionsClickListener favoriteEmojiconOnClickListener) {
        this.favoriteEmojiconOnClickListener = favoriteEmojiconOnClickListener;
    }

    public void setAddFavoriteEmojiconClickListener(AddFavoriteEmojiconClickListener addFavoriteEmojiconClickListener) {
        this.addFavoriteEmojiconClickListener = addFavoriteEmojiconClickListener;
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

    private void initDots(EmoticionMap eMap,int index) {
        if (eMap == null)
            return;
        bottomDot.removeAllViews();
        int itemCountPerPage = eMap.showAll == 0 ? 8 : 21;
        int viewPagerPageSize = (int) Math.ceil(((double) eMap.count)
                / ((double) itemCountPerPage));

        if (viewPagerPageSize > 0) {
            if (viewPagerPageSize == 1) {
                bottomDot.setVisibility(View.GONE);
            } else {
                bottomDot.setVisibility(View.VISIBLE);
                imageViews = new ImageView[viewPagerPageSize];
                int dotFileHeight = Utils.dipToPixels(context, 8);
                int dotFileWidth = Utils.dipToPixels(context, 8);
                int dotMargin = Utils.dipToPixels(context, 4);
                for (int i = 0; i < viewPagerPageSize; i++) {
                    ImageView image = new ImageView(context);
                    image.setTag(i);
                    LayoutParams params = new LayoutParams(dotFileWidth, dotFileHeight);
                    params.setMargins(dotMargin, dotMargin, dotMargin, dotMargin);
                    image.setBackgroundDrawable(new DotFile(context).setStateDrawable());
                    image.setEnabled(false);
                    bottomDot.addView(image, params);
                    imageViews[i] = image;
                }
                imageViews[index].setEnabled(true);
            }
        }
    }


    private void setupEmoticons() {
        listViews = new ArrayList<>();

        int columnsNumber = 4;
        int itemCountPerPage = 8;

        int viewPagerPageSize = (int) Math.ceil(((double) favoriteMap.count)
                / itemCountPerPage);
        for (int i = 0; i < viewPagerPageSize; i++) {
            listViews.add(getGridView(i, itemCountPerPage, true, favoriteMap, columnsNumber, favoriteEmojiconOnClickListener));
        }
        pageKey.put(listViews.size(), favoriteMap);

        viewPagerPageSize = (int) Math.ceil(((double) defaultMap1.count)
                / itemCountPerPage);

        for (int i = 0; i < viewPagerPageSize; i++) {
            listViews.add(getGridView(i, itemCountPerPage, false, defaultMap1, columnsNumber, othersOnEmoricionsClickListener));
        }
        pageKey.put(listViews.size(), defaultMap1);


        itemCountPerPage = 21;
        columnsNumber = 7;
        viewPagerPageSize = (int) Math.ceil(((double) defaultMap.count)
                / itemCountPerPage);

        for (int i = 0; i < viewPagerPageSize; i++) {
            listViews.add(getGridView(i, itemCountPerPage, false, defaultMap, columnsNumber, defaultOnEmoticionsClickListener));
        }
        pageKey.put(listViews.size(), defaultMap);


        for (EmoticionMap map : otherMap.values()) {
//            EmoticionMap emoticionMap = otherMap.get(key);
            map.count=map.emoticonEntityMap.size();
            FaceGridView.OnEmoticionsClickListener listener;
            if (map.showAll == 1) {
                itemCountPerPage = 21;
                columnsNumber = 7;
                listener = defaultOnEmoticionsClickListener;
            } else {
                itemCountPerPage = 8;
                columnsNumber = 4;
                listener = othersOnEmoricionsClickListener;
            }
            viewPagerPageSize = (int) Math.ceil(((double) map.count)
                    / itemCountPerPage);

            for (int i = 0; i < viewPagerPageSize; i++) {
                listViews.add(getGridView(i, itemCountPerPage, false, map, columnsNumber, listener));
            }
            pageKey.put(listViews.size(),map);
        }
        initDots(defaultMap1, 0);
        viewPager.setAdapter(new ViewPaperAdapter(listViews));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int preIndex = 0;
            int prePosition = 0;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int index = 0;
                while (position >= pageKey.keyAt(index)) {
                    index++;
                }
                int key = pageKey.keyAt(index);
                int offset = index == 0 ? 0:pageKey.keyAt(index - 1);
                EmoticionMap map = pageKey.get(key);
                if (preIndex != index) {
                    //底部dot 重新初始化
                    initDots(map,position - offset);
                } else {
                    //仅仅改变当前选择
                    imageViews[position - offset].setEnabled(true);
                    imageViews[prePosition - offset].setEnabled(false);
                }
                if (pageChangedListener != null){
                    pageChangedListener.onChanged(preIndex != index,map,position - offset,position);
                }
                preIndex = index;
                prePosition = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private GridView getGridView(final int page, final int itemCountPerPage, boolean isFavorite, final EmoticionMap emoticionMap, int columnsNumber, final OnEmoticionsClickListener listener) {
        GridView gridView = new GridView(context);
        gridView.setLayoutParams(
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
        gridView.setNumColumns(columnsNumber);

        gridView.setVerticalScrollBarEnabled(false);
        gridView.setHorizontalScrollBarEnabled(false);
        gridView.setBackgroundColor(Color.TRANSPARENT);
        if (itemCountPerPage == 21) {
            gridView.setVerticalSpacing(smallVSpacing);
            gridView.setPadding(gridView.getPaddingLeft(), smallVSpacing, gridView.getPaddingRight(),
                    gridView.getPaddingBottom());
        } else {
            gridView.setVerticalSpacing(bigVSpacing);
            gridView.setPadding(gridView.getPaddingLeft(), bigVSpacing, gridView.getPaddingRight(),
                    gridView.getPaddingBottom());
        }
        if (!isFavorite) {
            //不是自定义表情
            gridView.setAdapter(new FaceAdapter(context, emoticionMap, page, itemCountPerPage));
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int index = page * itemCountPerPage + position;
                    listener.onEmoticonClick(emoticionMap.getEntity(index), emoticionMap.packgeId);
                }
            });
        } else {
            gridView.setAdapter(new FavoriteFaceAdapter(context, emoticionMap, page, itemCountPerPage));
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int index = page * itemCountPerPage + position;
                    if (index == 0) {
                        //TODO 调起添加自定义表情Activity
                        if (addFavoriteEmojiconClickListener != null) {
                            addFavoriteEmojiconClickListener.onAddFavoriteEmojiconClick();
                        }

                        LogUtil.d(TAG, "添加自定义表情");
                        return;
                    }
                    listener.onEmoticonClick(emoticionMap.getEntity(index), emoticionMap.packgeId);
                }
            });
        }
        return gridView;
    }


    public void setPage(EmoticionMap map){
        int index = pageKey.indexOfValue(map);
        int key = pageKey.keyAt(index);
        int itemCountPerPage = map.showAll == 0 ? 8 : 21;
        int viewPagerPageSize = (int) Math.ceil(((double) map.count)
                / ((double) itemCountPerPage));
        int currentItem = key - viewPagerPageSize;
        viewPager.setCurrentItem(currentItem < 0 ? 0 : currentItem, false);
        initDots(map, 0);
    }

    public void setEmojiconMaps(EmoticionMap defaultMap,EmoticionMap defaultMap1, EmoticionMap favoriteMap, Map<String, EmoticionMap> otherMap) {
        this.defaultMap = defaultMap;
        this.defaultMap1 = defaultMap1;
        this.favoriteMap = favoriteMap;
        this.otherMap = otherMap;
        setupEmoticons();
    }

    public void setExtEmojicons(Map<String,EmoticionMap> extEmojicons){
        this.otherMap = extEmojicons;
        setupEmoticons();
    }

    public interface OnEmoticionsClickListener {
        void onEmoticonClick(EmoticonEntity entity, String pkgId);
    }

    public interface AddFavoriteEmojiconClickListener {
        void onAddFavoriteEmojiconClick();
    }

    /**
     * @author zhaokai
     * 当表情的ViewPager滑动时的回调监听
     */
    public interface OnPageChangedListener {
        /**
         * @param changeEmojicon 是否改变表情页
         * @param map            当前的表情页
         * @param offset         当前页在本表情map下的偏移量
         * @param globalOffset   当前页在整体ViewPager的偏移量
         */
        void onChanged(boolean changeEmojicon, EmoticionMap map, int offset, int globalOffset);
    }



}