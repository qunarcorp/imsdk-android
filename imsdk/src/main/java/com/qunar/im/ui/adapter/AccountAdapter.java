package com.qunar.im.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qunar.im.base.jsonbean.AccountPassword;
import com.qunar.im.base.util.AccountSwitchUtils;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.ui.R;

import java.util.List;

/**
 * Created by lihaibin.li on 2017/9/8.
 */

public class AccountAdapter extends CommonAdapter<AccountPassword> {
    public AccountAdapter(Context cxt, List<AccountPassword> datas, int itemLayoutId) {
        super(cxt, datas, itemLayoutId);
    }

    @Override
    protected void changeData(List datas) {
        super.changeData(datas);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public AccountPassword getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @Override
    public void convert(CommonViewHolder viewHolder, AccountPassword item) {
        TextView account_name = viewHolder.getView(R.id.account_name);
        account_name.setText(item.userid + "-" + item.navname);
        ImageView imageView = viewHolder.getView(R.id.icon_state);
        imageView.setVisibility((item.userid + item.navname).equals(CurrentPreference.getInstance().getUserid() + getCurrentNavName()) ? View.VISIBLE : View.GONE);
    }

    //获取当前share存储的导航名称
    private String getCurrentNavName(){
        return DataUtils.getInstance(CommonConfig.globalContext).getPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_NAME, AccountSwitchUtils.defalt_nav_name);
    }
}
