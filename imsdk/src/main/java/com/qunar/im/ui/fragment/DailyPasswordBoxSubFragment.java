package com.qunar.im.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.qunar.im.base.jsonbean.DailyMindSub;
import com.qunar.im.ui.presenter.IDailyMindPresenter;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.DailyPasswordBoxSubActivity;
import com.qunar.im.ui.activity.DailyPasswordBoxSubEditActivity;
import com.qunar.im.ui.adapter.DailyPasswordBoxSubAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 密码箱子密码
 * Created by lihaibin.li on 2017/8/23.
 */

public class DailyPasswordBoxSubFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener {

    private String TAG = DailyPasswordBoxSubFragment.class.getSimpleName();

    private DailyPasswordBoxSubActivity activity;

    private PullToRefreshListView password_box_listview;
    private IDailyMindPresenter iDailyMindPresenter;

    private List<DailyMindSub> dailyMindSubs = new ArrayList<>();
    private DailyPasswordBoxSubAdapter adapter;
    private int updateIndex;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (DailyPasswordBoxSubActivity) getActivity();
        iDailyMindPresenter = activity.getPasswordPresenter();
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.actionBar.getRightText().setVisibility(View.VISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.atom_ui_fragment_daily_password_box, null);
        password_box_listview = (PullToRefreshListView) view.findViewById(R.id.password_box_listview);
        password_box_listview.setOnRefreshListener(this);
        password_box_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        adapter = new DailyPasswordBoxSubAdapter(activity, dailyMindSubs, R.layout.atom_ui_item_password_box_sub);
        password_box_listview.setAdapter(adapter);
        password_box_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(activity, DailyPasswordBoxSubEditActivity.class);
                updateIndex = i - 1;
                intent.putExtra("dailyMindSub", adapter.getItem(updateIndex));
                intent.putExtra("passwordMain", activity.getMain_password());
                startActivityForResult(intent, DailyPasswordBoxSubEditActivity.PASSWORD_BOX_EDIT_REQUEST_CODE);
            }
        });
        return view;
    }

    public void setDailyMindSubs(List<DailyMindSub> dailyMindSubs) {
        this.dailyMindSubs.clear();
        this.dailyMindSubs.addAll(dailyMindSubs);
        password_box_listview.setMode(dailyMindSubs.size() < activity.number ? PullToRefreshBase.Mode.DISABLED : PullToRefreshBase.Mode.PULL_FROM_END);
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    public void addPasswordBoxSub(DailyMindSub dailyMindSub) {
        dailyMindSubs.add(0, dailyMindSub);
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    private void updatePasswordBoxSub(DailyMindSub dailyMindSub) {
        dailyMindSubs.set(updateIndex, dailyMindSub);
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    private void onComplete(List<DailyMindSub> datas) {
        password_box_listview.onRefreshComplete();
        password_box_listview.setMode(datas == null || datas.size() < activity.number ? PullToRefreshBase.Mode.DISABLED : PullToRefreshBase.Mode.PULL_FROM_END);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        activity.offset += dailyMindSubs.size();
        final List<DailyMindSub> datas = iDailyMindPresenter.getDailySubFromDB(activity.offset, activity.number, activity.getDailyMindMain().qid);
        if (datas != null) {
            dailyMindSubs.addAll(datas);
            adapter.notifyDataSetChanged();
        }
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                onComplete(datas);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == DailyPasswordBoxSubEditActivity.PASSWORD_BOX_EDIT_RESULT_CODE && data != null) {
            final DailyMindSub dailyMindSub = (DailyMindSub) data.getSerializableExtra("data");
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    updatePasswordBoxSub(dailyMindSub);
                }
            });
        }
    }
}
