package com.qunar.im.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.module.MultiItemEntity;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.structs.TransitFileJSON;
import com.qunar.im.base.util.DateTimeUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.MyFilesAdapter;
import com.qunar.im.ui.entity.MyFilesItem;
import com.qunar.im.ui.entity.MyFilesTitle;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lex lex on 2018/5/30.
 */

public class MyFilesActivity extends SwipeBackActivity {

    public static final int REQUEST_FILE_DETAIL = 1111;

    private RecyclerView mRecyclerView;
    private MyFilesAdapter myFilesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_myfiles_layout);
        initView();
        initActionBar();
        initData();
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.myfiles_list);

        myFilesAdapter = new MyFilesAdapter(this, null);
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setAdapter(myFilesAdapter);
        mRecyclerView.setLayoutManager(manager);
    }

    private void initActionBar() {
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(R.string.atom_ui_mine_myfile);
    }

    private void initData(){
        //获取数据库文件消息列表
        List<IMMessage> list = ConnectionUtil.getInstance().searchFilesMsg();
        ArrayList<MultiItemEntity> multiItemEntities = new ArrayList<>();
        try {
            Map<Long, List<MyFilesItem>> map = new LinkedHashMap<>();
            List<MyFilesItem> items = new ArrayList<>();
            for(int i = 0;i < list.size() ;i++) {
                IMMessage imMessage = list.get(i);
                try {
                    TransitFileJSON transitFileJSON = JsonUtils.getGson().fromJson(imMessage.getBody(), TransitFileJSON.class);
                    if(transitFileJSON == null){
                        Logger.i("我的文件解析异常不添加到列表  body=" + imMessage.getBody());
                        continue;
                    }
                } catch (Exception e) {
                    Logger.i("我的文件解析异常不添加到列表  body=" + imMessage.getBody());
                    continue;
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String datastr = dateFormat.format(imMessage.getTime());
                long time = dateFormat.parse(datastr).getTime();
                if(!map.containsKey(time)){
                    items = new ArrayList<>();

                } else {
                    items = map.get(time);
                }
                MyFilesItem item = new MyFilesItem();
                item.filemessage = imMessage;
                items.add(item);
                map.put(time, items);
            }
            for(Long time : map.keySet()) {
                MyFilesTitle myFilesTitle = new MyFilesTitle();
                myFilesTitle.title = DateTimeUtils.getTime(time,false, false);
                for(int i = 0;i < map.get(time).size();i++){
                    myFilesTitle.addSubItem(map.get(time).get(i));
                }
                multiItemEntities.add(myFilesTitle);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (myFilesAdapter != null){
            myFilesAdapter.setNewData(multiItemEntities);
            myFilesAdapter.expandAll();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MyFilesActivity.REQUEST_FILE_DETAIL) {
            initData();
        }
    }
}
