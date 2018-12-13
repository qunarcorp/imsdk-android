package com.qunar.im.ui.activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.qunar.im.base.util.LogUtil;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.FavouriteMessageAdapter;
import com.qunar.im.base.module.FavouriteMessage;
import com.qunar.im.base.presenter.IFavourityMessagePresenter;
import com.qunar.im.base.presenter.impl.FavourityMessagePresenter;
import com.qunar.im.base.presenter.views.IFavourityMsgView;
import com.qunar.im.ui.view.QtActionBar;

import java.util.ArrayList;
import java.util.List;


public class MyFavourityMessageActivity extends SwipeActivity implements IFavourityMsgView {
    ListView favorite_chat_region;

    private List<FavouriteMessage> selectMsgs = new ArrayList<>();
    private FavouriteMessageAdapter mFavouriteMessageAdapter;
    private IFavourityMessagePresenter favourityMessagePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_favourite_activity_chat);
        bindViews();
        initData();
        favourityMessagePresenter = new FavourityMessagePresenter();
        favourityMessagePresenter.setFavourity(this);
    }

    private void bindViews()
    {
        favorite_chat_region = (ListView) findViewById(R.id.favorite_chat_region);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        favourityMessagePresenter.getAllFavourity();
    }

    public void initData() {
        QtActionBar actionBar = (QtActionBar) this.findViewById(R.id.my_action_bar);
//        setActionBar(actionBar);
        actionBar.getTitleTextview().setText("我的收藏");
        mFavouriteMessageAdapter = new FavouriteMessageAdapter(MyFavourityMessageActivity.this);
        favorite_chat_region.setAdapter(mFavouriteMessageAdapter);
        favorite_chat_region.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtil.d("clickposition",""+position);
                Object obj = parent.getItemAtPosition(position);
                if(obj!=null)
                {
                    FavouriteMessage selectMsg=(FavouriteMessage)obj;
                    LogUtil.d("clickmsg",""+selectMsg);
                    Intent intent=new Intent(MyFavourityMessageActivity.this,MyFavourityMessageInfoActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("msg",selectMsg);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
        favorite_chat_region.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {

//                AlertDialog.Builder builder = new AlertDialog.Builder(MyFavourityMessageActivity.this);
                commonDialog.setMessage("你确认要删除这条收藏吗？");
                commonDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Object obj = parent.getItemAtPosition(position);
                        if(obj!=null&&FavouriteMessage.class.isInstance(obj))
                        {
                            selectMsgs.clear();
                            selectMsgs.add((FavouriteMessage) obj);
                            favourityMessagePresenter.deleteFavourity();
                            selectMsgs.clear();
                            favourityMessagePresenter.getAllFavourity();
                        }
                    }
                });
                commonDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                commonDialog.show();
                return true;
            }

        });

    }

    @Override
    public List<FavouriteMessage> getSelectedMsgs() {
        return selectMsgs;
    }

    @Override
    public void setFavourityMessages(List<FavouriteMessage> list) {
        mFavouriteMessageAdapter.setDatas(list);
    }
}
