package com.qunar.im.ui.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qunar.im.base.module.Nick;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.ui.R;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.ui.view.recyclerview.BaseQuickAdapter;
import com.qunar.im.ui.view.recyclerview.BaseViewHolder;
import com.qunar.im.utils.ConnectionUtil;

public class WorkWorldAtListIsSelectAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    private Activity mActivity;
    private static String defaultHeadUrl = QtalkNavicationService.getInstance().getSimpleapiurl() + "/file/v2/download/perm/3ca05f2d92f6c0034ac9aee14d341fc7.png";
    private OnCancelLis onCancelLis;

    public WorkWorldAtListIsSelectAdapter(Activity mActivity) {
        super(R.layout.atom_ui_activity_work_world_at_list_isselect_item);
        this.mActivity = mActivity;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final String xmppid) {
//        final String xmppid = item.getXmppId();
        ConnectionUtil.getInstance().getUserCard(xmppid, new IMLogicManager.NickCallBack() {
            @Override
            public void onNickCallBack(final Nick nick) {
//                ((Activity) mContext).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
                if (nick != null) {

                    ProfileUtils.displayGravatarByImageSrc(mActivity, nick.getHeaderSrc(), (ImageView) helper.getView(R.id.user_header),
                            mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head), mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head));
                    ((TextView) helper.getView(R.id.user_name)).setText(TextUtils.isEmpty(nick.getName()) ? xmppid : nick.getName());
                } else {
                    ProfileUtils.displayGravatarByImageSrc(mActivity, defaultHeadUrl, (ImageView) helper.getView(R.id.user_header),
                            mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head), mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head));
                    ((TextView) helper.getView(R.id.user_name)).setText(xmppid);
                }
//                ((TextView) helper.getView(R.id.user_architecture)).setText(QtalkStringUtils.architectureParsing(nick.getDescInfo()));
//                ((TextView) helper.getView(R.id.user_name)).setTextColor(mActivity.getResources().getColor(R.color.atom_ui_new_color_4DC1B5));
//                    }
//                });


            }
        }, false, false);
        helper.itemView.setTag(xmppid);
        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getData().remove( helper.getLayoutPosition());
                remove(helper.getLayoutPosition());
                onCancelLis.onCancel((String) helper.itemView.getTag());
            }
        });
    }

    public void setOnCancelLis(OnCancelLis onCancelLis){
        this.onCancelLis = onCancelLis;
    }

    public interface  OnCancelLis{
        void   onCancel(String  str);
    }
}
