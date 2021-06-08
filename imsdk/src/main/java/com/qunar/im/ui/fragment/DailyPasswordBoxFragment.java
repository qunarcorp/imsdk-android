package com.qunar.im.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.qunar.im.base.common.DailyMindConstants;
import com.qunar.im.base.jsonbean.DailyMindMain;
import com.qunar.im.base.util.AESTools;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.DailyPasswordBoxMainActivity;
import com.qunar.im.ui.activity.DailyPasswordBoxSubActivity;
import com.qunar.im.ui.adapter.DailyPasswordBoxAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 密码箱主密码列表
 * Created by lihaibin.li on 2017/8/22.
 */

public class DailyPasswordBoxFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener, AdapterView.OnItemClickListener {
    private static String TAG = DailyPasswordBoxFragment.class.getSimpleName();

    private DailyPasswordBoxMainActivity activity;
    private PullToRefreshListView password_box_listview;
    private DailyPasswordBoxAdapter adapter;
    private List<DailyMindMain> dailyMindMains = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (DailyPasswordBoxMainActivity) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.actionBar.getRightText().setVisibility(View.VISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.atom_ui_fragment_daily_password_box, null);
        password_box_listview = (PullToRefreshListView) view.findViewById(R.id.password_box_listview);
        password_box_listview.setOnRefreshListener(this);
        password_box_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        adapter = new DailyPasswordBoxAdapter(activity, dailyMindMains, R.layout.atom_ui_item_password_box);
        password_box_listview.getRefreshableView().setAdapter(adapter);
        password_box_listview.getRefreshableView().setOnItemClickListener(this);
        password_box_listview.getRefreshableView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                showEditDialog(adapter.getItem(i-1));
                return true;
            }
        });
        return view;
    }

    private void showEditDialog(final DailyMindMain dailyMindMain) {
        String items[] = {getString(R.string.atom_ui_common_delete)};
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Map<String,String> params = new HashMap<String, String>();
                params.put("qid",String.valueOf(dailyMindMain.qid));
                activity.getPasswordBoxPresenter().operateDailyMindFromHttp(DailyMindConstants.DELETE_MAIN,params);
            }
        });
        builder.create().show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
        final EditText editText = new EditText(activity);
        editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(activity);
        inputDialog.setTitle("验证主密码").setView(editText);
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String s = editText.getText().toString();
                        DailyMindMain data = adapter.getItem(i - 1);
                        try {
                            String value = AESTools.decodeFromBase64(s, data.content);
                            if (TextUtils.isEmpty(value)) {
                                toast("主密码不正确，请重新输入！");
                            } else {
                                Intent intent = new Intent(activity, DailyPasswordBoxSubActivity.class);
                                intent.putExtra("data", data);
                                intent.putExtra("main_password", s);
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            toast("解密失败！");
                        }

                    }
                }).show();
    }

    public void setDailyMindMains(List<DailyMindMain> dailyMindMains) {
        this.dailyMindMains.clear();
        this.dailyMindMains.addAll(dailyMindMains);
        if (password_box_listview != null)
            password_box_listview.setMode(dailyMindMains == null || dailyMindMains.size() < activity.number ? PullToRefreshBase.Mode.DISABLED : PullToRefreshBase.Mode.PULL_FROM_END);
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    public void addPasswordBoxMain(DailyMindMain dailyMindMain) {
        if (dailyMindMains != null) {
            dailyMindMains.add(0, dailyMindMain);
            if (adapter != null)
                adapter.notifyDataSetChanged();
        }
    }

    private void onComplete(List<DailyMindMain> datas) {
        password_box_listview.onRefreshComplete();
        password_box_listview.setMode(datas == null || datas.size() < activity.number ? PullToRefreshBase.Mode.DISABLED : PullToRefreshBase.Mode.PULL_FROM_END);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        activity.offset = dailyMindMains.size();
        final List<DailyMindMain> datas = activity.getPasswordBoxPresenter().getDailyMainFromDB(DailyMindConstants.PASSOWRD,activity.offset, activity.number);
        if (datas != null) {
            dailyMindMains.addAll(datas);
            adapter.notifyDataSetChanged();
        }
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                onComplete(datas);
            }
        });
    }

    private void toast(String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }
}
