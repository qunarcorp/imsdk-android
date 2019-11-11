package com.qunar.im.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qunar.im.base.common.CommonDownloader;
import com.qunar.im.base.jsonbean.DownloadImageResult;
import com.qunar.im.base.jsonbean.ImgVideoBean;
import com.qunar.im.base.jsonbean.VideoMessageResult;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.ui.presenter.impl.ChatSearchPresenter;
import com.qunar.im.ui.presenter.views.IChatSearchView;
import com.qunar.im.base.transit.DownloadRequest;
import com.qunar.im.base.transit.IDownloadRequestComplete;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DateTimeUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.core.utils.GlobalConfigManager;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.MuliSizeImageAdapter;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.utils.QtalkStringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *会话内图片视频搜索
 */
public class SearchChatPicVideoActivity extends SwipeActivity implements IChatSearchView,MuliSizeImageAdapter.OnSelectedChangeListener {
    public static void launch(Context context,String xmppId,String realJid){
        Intent intent = new Intent(context,SearchChatPicVideoActivity.class);
        intent.putExtra(PbChatActivity.KEY_JID,xmppId);
        intent.putExtra(PbChatActivity.KEY_REAL_JID,realJid);
        context.startActivity(intent);
    }
    private LinearLayout operate_buttons;

    TextView no_content;
    RecyclerView recyclerView;
    ChatSearchPresenter presenter;

    MuliSizeImageAdapter adapter;

    String xmppId;
    String realJid;

    ArrayList<ImgVideoBean> selectedData;
    LinkedHashMap<String,List<ImgVideoBean>> map = new LinkedHashMap<>();
    List<String> keys = new ArrayList<>();

    private boolean isSelecting;
    private int start;
    private static final int pageSize = 50;

    private boolean isHasMore = true;
    private boolean isLoaddingMore;

    QtNewActionBar actionBar;

    int progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_recyclerview_layout);

        initActionBar();

        no_content = (TextView) findViewById(R.id.no_content);
        operate_buttons = (LinearLayout) findViewById(R.id.operate_buttons);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        adapter = new MuliSizeImageAdapter(this);
        adapter.setOnSelectedChangeListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, true));

        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING){
                    LinearLayoutManager l = (LinearLayoutManager)recyclerView.getLayoutManager();
                    int lastVisiblePos = l.findLastVisibleItemPosition();
                    int count = l.getItemCount();
                    if(lastVisiblePos == count -1){//滑动到了最上面
                        if(isHasMore && !isLoaddingMore){
                            isLoaddingMore = true;
                            start += pageSize;
                            getHandler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loadDataFromDB();
                                }
                            },500);
                        }
                    }
                }
            }

        });

        presenter = new ChatSearchPresenter();

        handleData();
        if(savedInstanceState !=null){
            handleExtraData(savedInstanceState);
        }
        presenter.setView(this);
        loadDataFromDB();
    }

    private void initActionBar() {
        actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        actionBar.getRightIcon().setVisibility(View.GONE);
        setActionBarTitle("图片视频");
        setActionBarRightText(getString(R.string.atom_ui_user_selected));
        setActionBarRightTextClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelecting = !isSelecting;
                setActionBarRightText(isSelecting ? getString(R.string.atom_ui_cancel) : getString(R.string.atom_ui_user_selected));
                operate_buttons.setVisibility(isSelecting ? View.VISIBLE : View.GONE);
                if(!isSelecting){
                    setActionBarTitle("图片视频");
                }
                adapter.setSelecting(isSelecting);
                adapter.notifyDataSetChanged();
            }
        });
        setActionBarLeftClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadDataFromDB(){
        presenter.searchImgAndVideo(xmppId,realJid,start,pageSize);
    }

    public void handleData(){
        Intent intent  = getIntent();
        xmppId = intent.getStringExtra(PbChatActivity.KEY_JID);
        realJid = intent.getStringExtra(PbChatActivity.KEY_REAL_JID);
    }

    public void handleExtraData(Bundle savedInstanceState){
        xmppId = savedInstanceState.getString(PbChatActivity.KEY_JID);
        realJid = savedInstanceState.getString(PbChatActivity.KEY_REAL_JID);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PbChatActivity.KEY_JID, xmppId);
        outState.putString(PbChatActivity.KEY_REAL_JID, realJid);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        handleExtraData(savedInstanceState);
    }

    @Override
    public void onSelected(ArrayList<ImgVideoBean> beans) {
        selectedData = beans;
        if(!isEmptySelected()){
            setActionBarTitle("已选择" + selectedData.size() + "个文件");
        }
    }

    public void onTransfer(View view){
        if(isEmptySelected()){
            return;
        }
        Intent intent = new Intent(this,SearchUserActivity.class);
        intent.putExtra(Constants.BundleKey.IS_TRANS_MULTI_IMG,true);
        intent.putParcelableArrayListExtra(Constants.BundleKey.TRANS_MULTI_IMG,selectedData);
        startActivity(intent);
    }

    public void onSave(View view){
        if(isEmptySelected()){
            return;
        }
        createProgressDialog(selectedData.size());
        for(ImgVideoBean bean : selectedData){
            downloadFile(bean.url,bean.fileName);
        }
    }

    private boolean isEmptySelected(){
        return selectedData == null || selectedData.isEmpty();
    }



    @Override
    public void setSearchResult(List<IMMessage> data) {
        no_content.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
        isHasMore = !(data.size() < pageSize);
        isLoaddingMore = false;
        for(IMMessage message : data){
            setData(message);
        }
        adapter.setData(map,keys);
        adapter.notifyDataSetChanged();
    }

    private void setData(IMMessage message){
        String time = DateTimeUtils.getTimeForSearch(message.getTime().getTime());
        List<ImgVideoBean> datas = map.get(time);
        if(datas == null){
            datas = new ArrayList<>();
            keys.add(time);
        }
        if(message.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeSmallVideo_VALUE){//视频
            VideoMessageResult result = JsonUtils.getGson().fromJson(message.getExt(), VideoMessageResult.class);
            if (result == null) {
                result = JsonUtils.getGson().fromJson(message.getBody(), VideoMessageResult.class);
            }
            if(result != null){
                ImgVideoBean imgVideoBean = new ImgVideoBean();
                imgVideoBean.type = ImgVideoBean.VIDEO;
                imgVideoBean.url = result.FileUrl;
                imgVideoBean.thumbUrl = result.ThumbUrl;
                imgVideoBean.fileName = result.FileName;
                imgVideoBean.Duration = result.Duration;
                imgVideoBean.Width = result.Width;
                imgVideoBean.Height = result.Height;
                imgVideoBean.fileSize = result.FileSize;
                datas.add(imgVideoBean);
            }
        }else {//图片
            List<Map<String, String>> list = ChatTextHelper.getObjList(message.getBody());
            for(Map<String, String> map : list){
                ImgVideoBean imgVideoBean = new ImgVideoBean();
                String type = map.get("type");
                if("image".equals(type)){
                    imgVideoBean.type = ImgVideoBean.IMG;
                    imgVideoBean.url = map.get("value");
                    try{
                        Uri uri = Uri.parse(imgVideoBean.url);
                        String fileName = uri.getQueryParameter("name");
                        imgVideoBean.fileName = fileName;
                    }catch (Exception e){

                    }
                    datas.add(imgVideoBean);
                }
            }
        }

        map.put(time,datas);
    }

    private void downloadFile(String url,String fileName)
    {
        final File appDir = new File(Environment.getExternalStorageDirectory(), GlobalConfigManager.getAppName());
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        if(TextUtils.isEmpty(fileName)){
            fileName = System.currentTimeMillis() + ".temp";
        }
        File outfile = new File(appDir, fileName);
        String savePath = outfile.getPath();
        url = QtalkStringUtils.addFilePathDomain(url, true);
        DownloadRequest request = new DownloadRequest();
        request.savePath = savePath;
        request.url = url;
        request.requestComplete = new IDownloadRequestComplete() {
            @Override
            public void onRequestComplete(DownloadImageResult result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(dialog != null){
                            progress++;
                            dialog.setProgress(progress);
                            if(progress == selectedData.size()){
                                dialog.dismiss();
                                toast("文件已保存到" + appDir.getAbsolutePath());
                                progress = 0;
                                actionBar.getRightText().performClick();
                            }
                        }
                    }
                });
            }
        };
        CommonDownloader.getInsatnce().setDownloadRequest(request);
    }

    ProgressDialog dialog;
    private void createProgressDialog(int max){
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
        dialog.setCancelable(true);// 设置是否可以通过点击Back键取消
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        dialog.setTitle("保存文件");
        dialog.setMax(max);
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if(isSelecting){
            actionBar.getRightText().performClick();
        }else {
            super.onBackPressed();
        }
    }
}
