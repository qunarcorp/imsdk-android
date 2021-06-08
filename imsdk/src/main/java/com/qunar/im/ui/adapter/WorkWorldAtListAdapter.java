package com.qunar.im.ui.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qunar.im.base.module.Nick;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.ui.R;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.ui.view.IconView;
import com.qunar.im.ui.view.recyclerview.BaseQuickAdapter;
import com.qunar.im.ui.view.recyclerview.BaseViewHolder;
import com.qunar.im.utils.ConnectionUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkWorldAtListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private Activity mActivity;
    private static String defaultHeadUrl = QtalkNavicationService.getInstance().getSimpleapiurl() + "/file/v2/download/perm/3ca05f2d92f6c0034ac9aee14d341fc7.png";
    private Map<String,Object> selectMap;
    private List<String> isSelectList;

    private OnSelectChanage onSelectChanage;


    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param activity A new list is created  out of this one to avoid mutable list
     */
    public WorkWorldAtListAdapter(Activity activity) {
        super(R.layout.atom_ui_activity_work_world_at_list_item);
        this.mActivity = activity;
        selectMap = new HashMap<>();
        isSelectList = new ArrayList<>();
    }


    public void setSelectMap(Map<String,Object> map){
        this.selectMap = map;

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void convert(final BaseViewHolder helper, final String xmppid) {
        //获取头像地址
//        final String xmppid = item.getXmppId();
        boolean isSelect = false;
        if(!(selectMap.get(xmppid)==null)){
            isSelect = (boolean) selectMap.get(xmppid);
        }

        isShowSelect(helper, isSelect);


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
        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.N)
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
//              boolean isSelect = (boolean) selectMap.getOrDefault(xmppid,false);
                boolean isSelect = false;
                if(!(selectMap.get(xmppid)==null)){
                    isSelect = (boolean) selectMap.get(xmppid);
                }
              if(isSelect){
                  selectMap.put(xmppid,false);
                  isSelectList.remove(xmppid);
              }else{
                  selectMap.put(xmppid,true);
                  isSelectList.add(xmppid);
              }
              isShowSelect(helper,!isSelect );
              onSelectChanage.onChanage(isSelectList);
            }
        });
    }

    private void isShowSelect(BaseViewHolder helper, boolean isSelect) {
        if(isSelect){
            ((IconView) helper.getView(R.id.at_select)).setTextColor(mActivity.getResources().getColor(R.color.atom_ui_new_like_select));
            ((IconView) helper.getView(R.id.at_select)).setText(R.string.atom_ui_check_the_circle);
        }else{
            ((IconView) helper.getView(R.id.at_select)).setTextColor(mActivity.getResources().getColor(R.color.atom_ui_light_gray_99));
            ((IconView) helper.getView(R.id.at_select)).setText(R.string.atom_ui_new_uncheck_circle);
        }
    }

    public void setCancelInfo(String  str){
        selectMap.put(str,false);
        for (int i = 0; i < getData().size(); i++) {
            if(getData().get(i).equals(str)){
                notifyItemChanged(i);
            }
        }
    }


    public void setOnSelectChanage(OnSelectChanage onSelectChanage){
        this.onSelectChanage = onSelectChanage;
    }


    public interface OnSelectChanage{
        void onChanage(List<String> list);
    }

    public class selectInfo{
        boolean isSelect;

    }
}
