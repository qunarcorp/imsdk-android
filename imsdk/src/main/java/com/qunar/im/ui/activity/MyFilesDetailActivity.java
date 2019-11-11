package com.qunar.im.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.common.CommonDownloader;
import com.qunar.im.base.jsonbean.DownloadImageResult;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.base.protocol.ProgressResponseListener;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.structs.TransitFileJSON;
import com.qunar.im.base.transit.DownloadRequest;
import com.qunar.im.base.transit.IDownloadRequestComplete;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DateTimeUtils;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.FileUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.MessageUtils;
import com.qunar.im.base.util.Utils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.R;
import com.qunar.im.ui.imagepicker.util.ProviderUtil;
import com.qunar.im.ui.util.FileTypeUtil;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.progressbarview.NumberProgressBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;
import com.qunar.im.utils.QtalkStringUtils;

import java.io.File;
import java.io.Serializable;

import de.greenrobot.event.EventBus;

/**
 * Created by Lex lex on 2018/5/30.
 */

public class MyFilesDetailActivity extends SwipeBackActivity implements View.OnClickListener {
    private static final String TAG = "MyFilesDetailActivity";

    private ImageView myfile_detail_icon;
    private TextView myfile_detail_name, myfile_detail_size, myfile_detail_time, myfile_detail_from, myfile_detail_tips, myfile_detail_path;
    private TextView myfile_detail_transfer, myfile_detail_download;
    private NumberProgressBar numberProgressBar;

    private IMMessage imMessage;

    private FileDownloadHandler fileDownloadHandler;

    private static final int DOWNLOAD_FINISH = 1000;
    private static final int UPDATE_PROGRESS = 1001;
    private static final String PROGRESS = "progress";

    private TransitFileJSON transitFileJSON;
    private String transferId;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_myfiles_detail_layout);
        initView();
        initActionBar();
        initData();

        EventBus.getDefault().register(this);
    }

    private void initView() {
        myfile_detail_icon = (ImageView) findViewById(R.id.myfile_detail_icon);
        myfile_detail_name = (TextView) findViewById(R.id.myfile_detail_name);
        myfile_detail_size = (TextView) findViewById(R.id.myfile_detail_size);
        myfile_detail_time = (TextView) findViewById(R.id.myfile_detail_time);
        myfile_detail_from = (TextView) findViewById(R.id.myfile_detail_from);
        myfile_detail_tips = (TextView) findViewById(R.id.myfile_detail_tips);
        myfile_detail_path = (TextView) findViewById(R.id.myfile_detail_path);
        numberProgressBar = (NumberProgressBar) findViewById(R.id.number_progress_bar);

        myfile_detail_transfer = (TextView) findViewById(R.id.myfile_detail_transfer);
        myfile_detail_download = (TextView) findViewById(R.id.myfile_detail_download);

        myfile_detail_icon.setOnClickListener(this);
        myfile_detail_name.setOnClickListener(this);
        myfile_detail_size.setOnClickListener(this);
        myfile_detail_time.setOnClickListener(this);
        myfile_detail_from.setOnClickListener(this);
        myfile_detail_tips.setOnClickListener(this);
        myfile_detail_transfer.setOnClickListener(this);
        myfile_detail_download.setOnClickListener(this);
    }

    private void initActionBar() {
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(R.string.atom_ui_mine_myfile_preview);
    }

    private void initData(){
        fileDownloadHandler = new FileDownloadHandler();
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            imMessage = (IMMessage) bundle.getSerializable("message");
            if(imMessage == null) {
                finish();
                return;
            }
            String conntent = imMessage.getBody();
            if (!TextUtils.isEmpty(conntent)) {
                transitFileJSON = JsonUtils.getGson().fromJson(conntent, TransitFileJSON.class);
                if (transitFileJSON != null) {
                    myfile_detail_name.setText(transitFileJSON.FileName);
                    myfile_detail_size.setText(transitFileJSON.FileSize);
                    myfile_detail_time.setText(DateTimeUtils.getTime(imMessage.getTime().getTime(), false, true));
                    int ids = transitFileJSON.FileName.lastIndexOf(".");
                    int fileType = R.drawable.atom_ui_icon_zip_video;;
                    if (ids > 0) {
                        String fExt = transitFileJSON.FileName.substring(ids + 1);
                        fileType = FileTypeUtil.getInstance().getFileTypeBySuffix(fExt);
                    }
                    myfile_detail_icon.setImageResource(fileType);

                    file = new File(FileUtils.savePath + transitFileJSON.FileName);
                    refreshBtn();
//                    if (file.exists()) {
//                        myfile_detail_tips.setText("点击查看");
//                        myfile_detail_download.setText(R.string.atom_ui_btn_download_file);
//                    } else {
//                        myfile_detail_download.setText("删除文件");
//                        myfile_detail_tips.setText("该文件不支持在线预览，请下载后查看");
//                    }
                    if (CurrentPreference.getInstance().getPreferenceUserId().equals(imMessage.getFromID())) {
                        if (imMessage.getToID().contains("@11111111111")) {
                            ConnectionUtil.getInstance().getMucCard(imMessage.getToID(), new IMLogicManager.NickCallBack() {
                                @Override
                                public void onNickCallBack(Nick nick) {
                                    StringBuilder builder = new StringBuilder();
                                    builder.append("发给 ");
                                    if (nick != null) {
                                        builder.append(nick.getName());
                                    } else {
                                        builder.append(QtalkStringUtils.parseId(imMessage.getFromID()));
                                    }
                                    myfile_detail_from.setText(builder.toString());

                                }
                            }, false, true);
                        } else {
                            ConnectionUtil.getInstance().getMucCard(imMessage.getToID(), new IMLogicManager.NickCallBack() {
                                @Override
                                public void onNickCallBack(Nick nick) {
                                    StringBuilder builder = new StringBuilder();
                                    builder.append("发给 ");
                                    if (nick != null) {
                                        builder.append(nick.getName());
                                    } else {
                                        builder.append(QtalkStringUtils.parseId(imMessage.getFromID()));
                                    }
                                    myfile_detail_from.setText(builder.toString());
                                }
                            }, false, true);
                        }

                    } else {
                        ConnectionUtil.getInstance().getUserCard(imMessage.getFromID(), new IMLogicManager.NickCallBack() {
                            @Override
                            public void onNickCallBack(Nick nick) {
                                StringBuilder builder = new StringBuilder();
                                builder.append("来自 ");
                                if (nick != null && !TextUtils.isEmpty(nick.getName())) {
                                    builder.append(nick.getName());
                                } else {
                                    builder.append(QtalkStringUtils.parseId(imMessage.getFromID()));
                                }
                                myfile_detail_from.setText(builder.toString());

                            }
                        }, false, true);
                    }
                }
            } else {
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.myfile_detail_transfer){
            Intent selUser = new Intent(this, SearchUserActivity.class);
            selUser.putExtra(Constants.BundleKey.IS_TRANS, true);
            selUser.putExtra(Constants.BundleKey.TRANS_MSG, imMessage);
            startActivity(selUser);
        } else if(v.getId() == R.id.myfile_detail_download) {
            if(file.exists()) {
                file.delete();
                refreshBtn();
                Toast.makeText(MyFilesDetailActivity.this, "已删除", Toast.LENGTH_LONG).show();
            }else {
                downloadFile();
            }
        } else if(v.getId() == R.id.myfile_detail_tips){
            Intent intent = getFileIntent(FileUtils.savePath + transitFileJSON.FileName);
            try {
                startActivity(intent);
            } catch (Exception e) {
                LogUtil.e(TAG,"ERROR",e);
                Toast.makeText(this,R.string.atom_ui_tip_file_open_failed,Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 转发消息
     *
     * @param sendTransMsg
     */
    public void onEventMainThread(EventBusEvent.SendTransMsg sendTransMsg) {
        final Serializable imMessage = sendTransMsg.msg;
        final String transJid = sendTransMsg.transId;
        if (imMessage == null) return;
        // TODO: 2017/8/24 转发消息
        if (IMMessage.class.isInstance(imMessage)) {
            BackgroundExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    transferId = transJid;
                    transferMessage();
                    transferId = null;
                }
            });
            Toast.makeText(MyFilesDetailActivity.this, getString(R.string.atom_ui_tip_send_success), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 转发文件
     */
    public void transferMessage() {
        IMMessage newMsg;
        if (transferId.contains("@conference")) {
            newMsg = MessageUtils.generateMucIMMessage(CurrentPreference.getInstance().getPreferenceUserId(), transferId);
        } else {
            newMsg = MessageUtils.generateSingleIMMessage(CurrentPreference.getInstance().getPreferenceUserId(), transferId, "", "", "");
        }
        final IMMessage originMsg = imMessage;
        String toId = transferId;
        newMsg.setToID(toId);
        newMsg.setBody(originMsg.getBody());
        newMsg.setDirection(IMMessage.DIRECTION_SEND);
        newMsg.setConversationID(toId);
        newMsg.setMsgType(originMsg.getMsgType());
        newMsg.setExt(originMsg.getExt());
        if (toId.contains("@conference")) {
            newMsg.setType(ConversitionType.MSG_TYPE_GROUP);
            ConnectionUtil.getInstance().sendGroupTextOrEmojiMessage(newMsg);
        } else {
            newMsg.setType(ConversitionType.MSG_TYPE_CHAT);
            ConnectionUtil.getInstance().sendTextOrEmojiMessage(newMsg);
            newMsg.setFromID(QtalkStringUtils.userId2Jid(CurrentPreference.getInstance().getUserid()));
        }
    }

    protected class FileDownloadHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOAD_FINISH:
                    refreshBtn();
                    break;
                case UPDATE_PROGRESS:
                    int progress = msg.getData().getInt(PROGRESS);
                    myfile_detail_download.setEnabled(false);
                    numberProgressBar.setVisibility(View.VISIBLE);
                    numberProgressBar.setVisibility(View.VISIBLE);
                    numberProgressBar.setProgress(progress);
                    break;
            }
        }
    }

    /**
     * 更新按钮状态
     */
    private void refreshBtn() {
        if (file.exists()) {
            myfile_detail_download.setText("删除文件");
            myfile_detail_tips.setText(R.string.atom_ui_tip_open_other_app);
            myfile_detail_tips.setBackgroundResource(R.drawable.atom_ui_common_button_blue_selector);
            numberProgressBar.setVisibility(View.GONE);
            myfile_detail_path.setText(FileUtils.savePath + transitFileJSON.FileName);
            myfile_detail_path.setVisibility(View.VISIBLE);
            myfile_detail_download.setEnabled(true);
        } else {
            myfile_detail_download.setText(R.string.atom_ui_btn_download_file);
            myfile_detail_tips.setBackground(null);
            myfile_detail_tips.setText("该文件不支持在线预览，请下载后查看");
            myfile_detail_path.setVisibility(View.GONE);
            numberProgressBar.setVisibility(View.GONE);
        }
    }

    /**
     * 下载文件
     */
    public void downloadFile() {
        if(transitFileJSON == null) return;

        String url = QtalkStringUtils.addFilePathDomain(transitFileJSON.HttpUrl, true);
        StringBuilder urlbuilder =new StringBuilder(url);
        Protocol.addBasicParamsOnHead(urlbuilder);
        url = urlbuilder.toString();

        myfile_detail_download.setEnabled(false);
        final DownloadRequest request = new DownloadRequest();
        request.savePath = FileUtils.savePath + transitFileJSON.FileName;
        request.url = url;
        request.requestComplete = new IDownloadRequestComplete() {
            @Override
            public void onRequestComplete(DownloadImageResult result) {
                if(fileDownloadHandler != null) {
                    Message msg = fileDownloadHandler.obtainMessage();
                    msg.what = DOWNLOAD_FINISH;
                    fileDownloadHandler.sendMessage(msg);
                }
            }
        };
        request.progressListener = new ProgressResponseListener() {
            @Override
            public void onResponseProgress(long bytewriten, long length, boolean complete) {
                if(fileDownloadHandler != null) {
                    int current = (int) (bytewriten * 100 / length);
                    Message msg = fileDownloadHandler.obtainMessage();
                    msg.what = UPDATE_PROGRESS;
                    Bundle b = new Bundle();
                    b.putInt(PROGRESS, current);
                    msg.setData(b);
                    fileDownloadHandler.sendMessage(msg);
                }
            }
        };
        CommonDownloader.getInsatnce().setDownloadRequest(request);
    }

    //android获取一个用于打开文件的intent
    public Intent getFileIntent(String param) {
        String prefix = param;
        String fileType = "text/plain";
        int index = param.lastIndexOf(".");
        if(index != -1){
            prefix = param.substring(index);
        }
        for(int i = 0; i< Utils.MIME_MapTable.length; i++){
            if(prefix.equals(Utils.MIME_MapTable[i][0])){
                fileType = Utils.MIME_MapTable[i][1];
                break;
            }
        }
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        File file = new File(param);
        if (Build.VERSION.SDK_INT >= 24) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri fileUri = FileProvider.getUriForFile(CommonConfig.globalContext, ProviderUtil.getFileProviderName(MyFilesDetailActivity.this), file);//android 7.0以上
            intent.setDataAndType(fileUri, fileType);
        }else {
            intent.setDataAndType(Uri.fromFile(file), fileType);
        }
        return intent;
    }
}
