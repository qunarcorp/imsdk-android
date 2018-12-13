package com.qunar.im.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.qunar.im.base.module.RobotConversation;
import com.qunar.im.base.presenter.IRobotConvPresenter;
import com.qunar.im.base.presenter.impl.RobotConvPresenter;
import com.qunar.im.base.presenter.views.IRobotConvView;
import com.qunar.im.utils.QtalkStringUtils;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.RobotChatActivity;
import com.qunar.im.ui.adapter.RobotConvAdapter;

import java.util.List;

/**
 * Created by xinbo.wang on 2016/7/25.
 */
public class RobotConvFragment extends BaseFragment implements IRobotConvView {
    ListView list;
    TextView tvEmpty;
    RobotConvAdapter convAdapter;
    IRobotConvPresenter robotConvPresenter;
    @Override
    public void setRobotConvList(List<RobotConversation> list) {
        convAdapter.setRecentConversationList(list);
    }

    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        robotConvPresenter = new RobotConvPresenter();
        robotConvPresenter.setIRobotConvView(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.atom_ui_fragment_conversation, container, false);
        list = (ListView) view.findViewById(R.id.list);
        tvEmpty = (TextView) view.findViewById(R.id.empty);
        initViews();
        return view;
    }

    private void initViews() {
        if(convAdapter==null)
            convAdapter = new RobotConvAdapter(getContext());
        list.setEmptyView(tvEmpty);
        list.setAdapter(convAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                recentConvClick(((RobotConversation) parent.getAdapter().getItem(position)));
            }
        });
    }
    void recentConvClick(RobotConversation item) {
        String id = QtalkStringUtils.userId2Jid(item.id);
        Intent intent =new Intent(getContext(),RobotChatActivity.class);
        intent.putExtra(RobotChatActivity.ROBOT_ID_EXTRA, id);
        startActivity(intent);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        robotConvPresenter.loadConvList();
    }
}
