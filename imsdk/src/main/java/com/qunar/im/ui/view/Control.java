package com.qunar.im.ui.view;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/22 17:20
 * 修改人员：Robi
 * 修改时间：2016/12/22 17:20
 * 修改备注：
 * Version: 1.0.0
 */
public class Control {
    ArrayList<String> datas = new ArrayList<>();
    RSoftInputLayout mSoftInputLayout;
    Activity mActivity;
    private RecyclerView mRecyclerView;

    public Control(RecyclerView recyclerView, RSoftInputLayout softInputLayout, Activity activity) {
        mRecyclerView = recyclerView;
        mSoftInputLayout = softInputLayout;
        mActivity = activity;
    }
    public Control(RSoftInputLayout softInputLayout, Activity activity){
        mSoftInputLayout = softInputLayout;
        mActivity = activity;
    }

    protected void initContentLayout() {
        mSoftInputLayout.addOnEmojiLayoutChangeListener(new RSoftInputLayout.OnEmojiLayoutChangeListener() {
            @Override
            public void onEmojiLayoutChange(boolean isEmojiShow, boolean isKeyboardShow, int height) {
//                Log.w("Robi", "表情显示:" + mSoftInputLayout.isEmojiShow() + " 键盘显示:" + mSoftInputLayout.isKeyboardShow()
//                        + " 表情高度:" + mSoftInputLayout.getShowEmojiHeight() + " 键盘高度:" + mSoftInputLayout.getKeyboardHeight());
//                String log = "表情显示:" + isEmojiShow + " 键盘显示:" + isKeyboardShow + " 高度:" + height;
//                Log.e("Robi", log);
//                datas.add(log);
//                mRecyclerView.getAdapter().notifyItemInserted(datas.size());
//                mRecyclerView.smoothScrollToPosition(datas.size());
            }
        });
//        datas.add("内容顶部");
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
//        mRecyclerView.setAdapter(new RecyclerView.Adapter() {
//            @Override
//            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                TextView textView = new TextView(mActivity);
//                return new RecyclerView.ViewHolder(textView) {
//                };
//            }
//
//            @Override
//            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//                ((TextView) holder.itemView).setText(datas.get(position));
//            }
//
//            @Override
//            public int getItemCount() {
//                return datas.size();
//            }
//        });
    }

    public boolean onBackPressed() {
        return mSoftInputLayout.requestBackPressed();
    }

    public void onSetPadding100Click() {
        mSoftInputLayout.showEmojiLayout(dpToPx(100));
    }

    public void onSetPadding400Click() {
        mSoftInputLayout.showEmojiLayout(dpToPx(400));
    }

    public void onShowClick() {
        mSoftInputLayout.showEmojiLayout();
    }

    public void onHideClick() {
        mSoftInputLayout.hideEmojiLayout();
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, mActivity.getResources().getDisplayMetrics());
    }

    public void onLayoutFullScreen() {
        mSoftInputLayout.requestLayout();
        mSoftInputLayout.post(new Runnable() {
            @Override
            public void run() {
                mSoftInputLayout.requestLayout();
            }
        });
    }
}
