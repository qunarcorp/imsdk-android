package com.qunar.im.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.qunar.im.base.module.MultiItemEntity;
import com.qunar.im.base.presenter.impl.CollectionPresenter;
import com.qunar.im.base.presenter.views.ICollectionPresenter;
import com.qunar.im.base.presenter.views.ICollectionView;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.CollectionAdapter;

import com.qunar.im.ui.view.QtNewActionBar;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by hubin on 2017/11/20.
 */

public class CollectionActivity extends IMBaseActivity implements ICollectionView {
    private RecyclerView mRecyclerView;
    private ICollectionPresenter collectionPresenter;
    private CollectionAdapter collectionAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_collection);
        initView();
        initActionBar();
        initData();


    }

    private void initData() {
        collectionPresenter = new CollectionPresenter();
        collectionPresenter.setView(this);
        collectionAdapter = new CollectionAdapter(null,this);
        final LinearLayoutManager manager = new LinearLayoutManager(this);


        mRecyclerView.setAdapter(collectionAdapter);
        // important! setLayoutManager should be called after setAdapter
        mRecyclerView.setLayoutManager(manager);
        collectionAdapter.expandAll();

        collectionPresenter.reloadMessages();

        collectionPresenter.getBindUser();
    }
//    atom_ui_new_management
    private void initActionBar() {
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(R.string.atom_ui_setting_account_management);
        setActionBarRightIcon(R.string.atom_ui_new_management);
        //设置按钮单机事件,开启代收管理页 web端
        setActionBarRightIconClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = String.format(QtalkNavicationService.getInstance().getMconfig() + "?u=%s&d=%s&navBarBg=208EF2",
                        CurrentPreference.getInstance().getUserid(), QtalkNavicationService.getInstance().getXmppdomain());
                Intent intent = new Intent(CollectionActivity.this, QunarWebActvity.class);
                intent.setData(Uri.parse(url));
                intent.putExtra(WebMsgActivity.IS_HIDE_BAR, true);
                startActivity(intent);
            }
        });

    }

    private void initView() {
      mRecyclerView = (RecyclerView) findViewById(R.id.collection_rv);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        collectionPresenter.removeEvent();
    }

    @Override
    public Context getContext() {
        return getApplication();
    }

    @Override
    public void setList(ArrayList<MultiItemEntity> list) {
        collectionAdapter.setNewData(list);
        Map<Integer,Boolean> map = collectionAdapter.getMap();
       for (Map.Entry<Integer,Boolean> entry :map.entrySet()){
           if(entry.getValue()){

               collectionAdapter.expand(entry.getKey());
           }
       }

//        collectionAdapter.expandAll();
//        collectionAdapter.ex
    }


}
