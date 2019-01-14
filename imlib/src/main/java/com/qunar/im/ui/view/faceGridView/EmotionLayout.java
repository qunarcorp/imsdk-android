package com.qunar.im.ui.view.faceGridView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qunar.im.base.util.EmotionUtils;
import com.qunar.im.base.view.faceGridView.EmoticionMap;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.AddEmotionActivity;

import java.io.File;
import java.util.Map;

/**
 * Created by zhaokai on 15-8-3.
 */
public class EmotionLayout extends LinearLayout {
    Context context;
    private FaceGridView faceGridView;
    private LinearLayout tabLayout;
    private TextView defaultEmotionView,defaultEmotionView1, favoriteEmotion;
    private ImageView deleteImageView;
    private OnClickListener defaultEmotionTabOnClickListener,defaultEmotionTabOnClickListener1, otherTabOnclickListener;
    private Map<String, EmoticionMap> map;
    private EmoticionMap defaultMap,defaultMap1, favoriteMap;
    private View selectedTab;
    private HorizontalScrollView scrollView;
    private boolean isInit;

    public EmotionLayout(Context context) {
        this(context, null);
    }

    public EmotionLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmotionLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public void resetFavoriteEmotion(EmoticionMap favoriteMap) {
        this.favoriteMap = favoriteMap;
        faceGridView.setEmojiconMaps(defaultMap,defaultMap1, favoriteMap, map);
        resetTab();
    }

    public void setAddFavoriteEmoticonClickListener(FaceGridView.AddFavoriteEmojiconClickListener onAddFavoriteEmojiconClickListener) {
        faceGridView.setAddFavoriteEmojiconClickListener(onAddFavoriteEmojiconClickListener);
    }

    public void setDefaultOnEmoticionsClickListener(FaceGridView.OnEmoticionsClickListener defaultOnEmoticionsClickListener) {
        faceGridView.setDefaultOnEmoticionsClickListener(defaultOnEmoticionsClickListener);
    }

    public void setOthersOnEmoricionsClickListener(FaceGridView.OnEmoticionsClickListener othersOnEmoricionsClickListener) {
        faceGridView.setOthersOnEmoricionsClickListener(othersOnEmoricionsClickListener);
    }

    public void setFavoriteEmojiconOnClickListener(FaceGridView.OnEmoticionsClickListener favoriteEmojiconOnClickListener) {
        faceGridView.setFavoriteEmojiconOnClickListener(favoriteEmojiconOnClickListener);
    }

    public void setDeleteImageViewOnClickListener(OnClickListener listener) {
        deleteImageView.setOnClickListener(listener);
    }

    private void init() {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.atom_ui_layout_emotion, this);
        this.faceGridView = findViewById(R.id.faceGridView);
        this.tabLayout = findViewById(R.id.tab_layout);
        this.defaultEmotionView = findViewById(R.id.default_emotion);
        this.defaultEmotionView1 = findViewById(R.id.default_emotion1);
        this.deleteImageView = findViewById(R.id.delete_emotion);
        ImageView addEmotion = findViewById(R.id.add_emotion);
        favoriteEmotion = findViewById(R.id.favorite_emotion);
        scrollView = findViewById(R.id.scroll);
        addEmotion.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddEmotionActivity.class);
                context.startActivity(intent);
            }
        });
        defaultEmotionTabOnClickListener = new DefaultTabOnClickListener();
        defaultEmotionTabOnClickListener1 = new DefaultTabOnClickListener1();
        otherTabOnclickListener = new OtherTabOnClickListener();
        favoriteEmotion.setOnClickListener(new FavoriteTabOnClickListener());
        faceGridView.setPageChangedListener(new FaceGridView.OnPageChangedListener() {
            @Override
            public void onChanged(boolean changeEmojicon, EmoticionMap map, int offset, int globalOffset) {
                int[] tabPos = new int[]{0, 0};
                int[] scrollPos = new int[]{0, 0};
                selectedTab.getLocationInWindow(tabPos);
                scrollView.getLocationInWindow(scrollPos);
                if (tabPos[0] < scrollPos[0]) {
                    //在ScrollView 左侧外
                    scrollView.smoothScrollBy(tabPos[0] - scrollPos[0], 0);
                }
                if (tabPos[0] + selectedTab.getWidth() > scrollPos[0] + scrollView.getWidth()) {
                    //在ScrollView 右侧外
                    scrollView.smoothScrollBy(-scrollPos[0] - scrollView.getWidth() + tabPos[0] + selectedTab.getWidth(),0);
                }
                if (!changeEmojicon) {
                    //没有改变当前的表情列表
                    return;
                }
                selectedTab.setBackgroundResource(R.color.atom_ui_light_gray);
                if (map.packgeId.equals(EmotionUtils.getDefaultEmotion(context).packgeId)) {
                    selectedTab = defaultEmotionView;
                }else if(map.packgeId.equals(EmotionUtils.getDefaultEmotion1(context).packgeId)){
                    selectedTab = defaultEmotionView1;
                } else if (map.packgeId.equals(EmotionUtils.FAVORITE_ID)) {
                    selectedTab = favoriteEmotion;
                } else {
                    View v = findViewWithTag(map.packgeId);
                    if (v != null) {
                        selectedTab = v;
                    }
                }
                selectedTab.setBackgroundResource(R.color.atom_ui_white);
            }
        });
        selectedTab = defaultEmotionView1;
    }

    public void initFaceGridView(Map<String, EmoticionMap> map, EmoticionMap defaultMap,EmoticionMap defaultMap1, EmoticionMap favoriteMap) {
        this.map = map;
        this.defaultMap = defaultMap;
        this.defaultMap1 = defaultMap1;
        this.favoriteMap = favoriteMap;
        initTabLayout();
        faceGridView.setEmojiconMaps(defaultMap,defaultMap1,favoriteMap, map);
        faceGridView.setPage(defaultMap1);
        defaultEmotionView.setOnClickListener(defaultEmotionTabOnClickListener);
        defaultEmotionView1.setOnClickListener(defaultEmotionTabOnClickListener1);
        selectedTab = defaultEmotionView1;
        isInit = true;
    }

    public boolean isInitialize() {
        return isInit;
    }

    private void initTabLayout() {
        if (map != null && map.size() > 0) {
            tabLayout.removeAllViews();
            for (String s : map.keySet()) {
                ImageView view = new ImageView(context);
//                view.setText(s);
                view.setContentDescription(s);
                EmoticionMap emoticionMap = map.get(s);
                setTextViewDrawable(emoticionMap.getEntity(0).fileFiexd,view);
                view.setTag(emoticionMap.packgeId);
                view.setLayoutParams(defaultEmotionView.getLayoutParams());
//                view.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultEmotionView.getTextSize());
//                view.setGravity(defaultEmotionView.getGravity());
                view.setBackgroundResource(R.color.atom_ui_light_gray);
                view.setPadding(defaultEmotionView.getPaddingLeft(), defaultEmotionView.getPaddingTop()
                        , defaultEmotionView.getPaddingRight(), defaultEmotionView.getPaddingBottom());
                view.setOnClickListener(otherTabOnclickListener);
                tabLayout.addView(view);
            }
        }
    }

    private void setTextViewDrawable(String path, final ImageView imageView){
        Glide.with(context)
                .load(Uri.fromFile(new File(path)))
                .asBitmap()
                .override(72, 72)
                .into(imageView);
    }

    public void resetTabLayout() {
        initFaceGridView(EmotionUtils.getExtEmotionsMap(context,true), EmotionUtils.getDefaultEmotion(context),EmotionUtils.getDefaultEmotion1(context), EmotionUtils.getFavoriteMap(context));
////        map = EmotionUtils.getExtEmotionsMap(context,true);
//        if (map.size() > tabLayout.getChildCount()) {
//            Object[] keys = map.keySet().toArray();
//            for (int i = tabLayout.getChildCount(); i < map.size(); i++) {
//                ImageView view = new ImageView(context);
//                String s = keys[i].toString();
//                view.setContentDescription(s);
//                EmoticionMap emoticionMap = map.get(s);
//                setTextViewDrawable(emoticionMap.getEntity(0).fileFiexd,view);
//                view.setTag(emoticionMap.packgeId);
//                view.setLayoutParams(defaultEmotionView.getLayoutParams());
//                view.setBackgroundResource(R.color.atom_ui_light_gray);
//                view.setPadding(defaultEmotionView.getPaddingLeft(), defaultEmotionView.getPaddingTop()
//                        , defaultEmotionView.getPaddingRight(), defaultEmotionView.getPaddingBottom());
//                view.setOnClickListener(otherTabOnclickListener);
//                tabLayout.addView(view);
//            }
//        } else if (map.size() < tabLayout.getChildCount()) {
//            defaultEmotionTabOnClickListener.onClick(defaultEmotionView);
//            tabLayout.removeAllViews();
//            for (String s : map.keySet()) {
//                ImageView view = new ImageView(context);
//                view.setContentDescription(s);
//                EmoticionMap emoticionMap = map.get(s);
//                setTextViewDrawable(emoticionMap.getEntity(0).fileFiexd,view);
//                view.setTag(emoticionMap.packgeId);
//                view.setLayoutParams(defaultEmotionView.getLayoutParams());
//                view.setBackgroundResource(R.color.atom_ui_light_gray);
//                view.setPadding(defaultEmotionView.getPaddingLeft(), defaultEmotionView.getPaddingTop()
//                        , defaultEmotionView.getPaddingRight(), defaultEmotionView.getPaddingBottom());
//                view.setOnClickListener(otherTabOnclickListener);
//                tabLayout.addView(view);
//            }
//        }
//        faceGridView.setExtEmojicons(EmotionUtils.getExtEmotionsMap(context,false));
    }


    public void resetFavoriteTab() {
        if (selectedTab != favoriteEmotion) {
            return;
        }
        faceGridView.setPage(favoriteMap);
    }

    public void resetTab() {
        if(selectedTab == favoriteEmotion) {
            faceGridView.setPage(favoriteMap);
        }else if(selectedTab == defaultEmotionView) {
            faceGridView.setPage(defaultMap);
        }else if(selectedTab == defaultEmotionView1) {
            faceGridView.setPage(defaultMap1);
        }
    }

    final class FavoriteTabOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (selectedTab == v || v == null) {
                return;
            }
            selectedTab.setBackgroundResource(R.color.atom_ui_light_gray);
            v.setBackgroundResource(R.color.atom_ui_white);
            selectedTab = v;
            resetFavoriteTab();
        }
    }

    final class OtherTabOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (selectedTab == v || v == null) {
                return;
            }
            selectedTab.setBackgroundResource(R.color.atom_ui_light_gray);
            v.setBackgroundResource(R.color.atom_ui_white);
            faceGridView.setPage(map.get(v.getContentDescription().toString()));
            selectedTab = v;
        }
    }

    final class DefaultTabOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (selectedTab == defaultEmotionView) {
                return;
            }
            selectedTab.setBackgroundResource(R.color.atom_ui_light_gray);
            defaultEmotionView.setBackgroundResource(R.color.atom_ui_white);
            faceGridView.setPage(defaultMap);
            selectedTab = v;
        }
    }

    final class DefaultTabOnClickListener1 implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (selectedTab == defaultEmotionView1) {
                return;
            }
            selectedTab.setBackgroundResource(R.color.atom_ui_light_gray);
            defaultEmotionView1.setBackgroundResource(R.color.atom_ui_white);
            faceGridView.setPage(defaultMap1);
            selectedTab = v;
        }
    }

}