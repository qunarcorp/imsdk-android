package com.qunar.im.ui.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qunar.im.base.module.MultiItemEntity;
import com.qunar.im.base.module.WorkWorldItem;
import com.qunar.im.base.module.WorkWorldNoticeItem;
import com.qunar.im.base.util.Utils;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.recyclerview.BaseMultiItemQuickAdapter;
import com.qunar.im.ui.view.recyclerview.BaseViewHolder;
import com.qunar.im.utils.ConnectionUtil;

import java.util.List;

import static com.qunar.im.ui.adapter.PublicWorkWorldAdapterDraw.showNoticeInit;
import static com.qunar.im.ui.adapter.PublicWorkWorldAdapterDraw.showWorkWorld;

public class WorkWorldAtShowAdapter  extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {


    public static final int COMMENTATMESSAGE= 4;
    public static final int WORKWORLDATMESSAGE= 3;



    public String postOwner;
    public String postOwnerHost;


    private Activity mActivity;


    private View.OnClickListener onClickListener;

    private View.OnClickListener openDetailsListener;

    private RecyclerView mRecyclerView;






    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public WorkWorldAtShowAdapter(List<? extends MultiItemEntity> data, Activity mActivity,RecyclerView mRecyclerView) {
        super((List<MultiItemEntity>) data);
        addItemType(COMMENTATMESSAGE, R.layout.atom_ui_work_world_notice_item);
        addItemType(WORKWORLDATMESSAGE,R.layout.atom_ui_work_world_item);
        this.mActivity = mActivity;
        this.mRecyclerView = mRecyclerView;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.onClickListener = listener;
    }

    public void setOpenDetailsListener(View.OnClickListener listener) {
        this.openDetailsListener = listener;
    }







    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceAsColor")
    @Override
    protected void convert(final BaseViewHolder helper, final MultiItemEntity item) {
        switch (helper.getItemViewType()) {





            case COMMENTATMESSAGE:

                showNoticeInit(helper, (WorkWorldNoticeItem) item,mActivity);
                RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(Utils.dipToPixels(mActivity,43),Utils.dipToPixels(mActivity,43));
                params.leftMargin=Utils.dipToPixels(mActivity,15);
                helper.getView(R.id.user_header).setLayoutParams(params);
                LinearLayout.LayoutParams ap = (LinearLayout.LayoutParams) ((TextView)helper.getView(R.id.comment_item_text)).getLayoutParams();
                ap.leftMargin=Utils.dipToPixels(mActivity,9);
                helper.getView(R.id.comment_item_text).setLayoutParams(ap);

                LinearLayout.LayoutParams bp = (LinearLayout.LayoutParams) ((TextView)helper.getView(R.id.notice_time)).getLayoutParams();
                ap.leftMargin=Utils.dipToPixels(mActivity,9);
                helper.getView(R.id.notice_time).setLayoutParams(ap);

                break;

            case WORKWORLDATMESSAGE:

                ConnectionUtil.getInstance().getWorkWorldByUUID(((WorkWorldNoticeItem) item).getPostUUID(), new ConnectionUtil.WorkWorldCallBack() {
                    @Override
                    public void callBack(final WorkWorldItem item) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                helper.getView(R.id.workworldhave).setVisibility(View.VISIBLE);
                                helper.getView(R.id.workworldnohave).setVisibility(View.GONE);
                                showWorkWorld(helper, item,mActivity,mRecyclerView,openDetailsListener,null, false);
                            }
                        });

                    }

                    @Override
                    public void goToNetWork() {
                        helper.getView(R.id.workworldhave).setVisibility(View.GONE);
                        helper.getView(R.id.workworldnohave).setVisibility(View.VISIBLE);
                    }
                });


                break;


        }
    }














}
