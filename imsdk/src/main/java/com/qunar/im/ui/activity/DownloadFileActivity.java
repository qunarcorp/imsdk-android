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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.qunar.im.base.common.CommonDownloader;
import com.qunar.im.base.jsonbean.DownloadImageResult;
import com.qunar.im.base.jsonbean.EncryptMsg;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.protocol.ProgressResponseListener;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.structs.TransitFileJSON;
import com.qunar.im.base.transit.DownloadRequest;
import com.qunar.im.base.transit.IDownloadRequestComplete;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.base.util.FileUtils;
import com.qunar.im.base.util.HttpUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.Utils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.protobuf.dispatch.DispatchHelper;
import com.qunar.im.ui.R;
import com.qunar.im.ui.imagepicker.util.ProviderUtil;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.progressbarview.NumberProgressBar;
import com.qunar.im.utils.QtalkStringUtils;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by huayu on 2016/7/12.
 */
public class DownloadFileActivity extends IMBaseActivity implements View.OnClickListener {
    private static final String TAG = DownloadFileActivity.class.getSimpleName();
    private Handler mHandler;
    private static final int DOWNLOAD_FINISH = 1000;
    private static final int UPDATE_PROGRESS = 1001;
    private static final String PROGRESS = "progress";
    private NumberProgressBar numberProgressBar;
    private TextView tvFileName, tvFileSize, tvfile_path;
    private Button btnDownload;
    private String url;
    private String fileName;
    private String fileSize;
    private String fileMd5Path;
    private String localFile;
    private TransitFileJSON jsonObject;

    private IMMessage message;

    protected class FileDownloadHandler extends Handler {
        WeakReference<DownloadFileActivity> weakReference;

        public FileDownloadHandler(WeakReference<DownloadFileActivity> w) {
            weakReference = w;
        }

        @Override
        public void handleMessage(Message msg) {
            DownloadFileActivity activity = weakReference.get();
            switch (msg.what) {
                case DOWNLOAD_FINISH:
                    btnDownload.setText(R.string.atom_ui_tip_open_other_app);
                    btnDownload.setBackgroundResource(R.drawable.atom_ui_common_button_blue_selector);
                    btnDownload.setId(R.id.atom_ui_open_file);
                    activity.numberProgressBar.setVisibility(View.GONE);
                    tvfile_path.setText(FileUtils.savePath + fileName);
                    tvfile_path.setVisibility(View.VISIBLE);
                    btnDownload.setEnabled(true);
                    showShareTitleView(new File(FileUtils.savePath + fileName));
                    break;
                case UPDATE_PROGRESS:
                    int progress = msg.getData().getInt(PROGRESS);
                    activity.btnDownload.setEnabled(false);
                    activity.numberProgressBar.setVisibility(View.VISIBLE);
                    weakReference.get().numberProgressBar.setVisibility(View.VISIBLE);
                    weakReference.get().numberProgressBar.setProgress(progress);
                    break;
            }
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.atom_ui_activity_download_file);
        bindViews();
        IMMessage message = (IMMessage) getIntent().getSerializableExtra("file_message");
        jsonObject = null;
        //加密消息 先解密
        if (message.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeEncrypt_VALUE) {
            EncryptMsg encryptMsg = ChatTextHelper.getEncryptMessageBody(message);
            if (encryptMsg != null)
                jsonObject = JsonUtils.getGson().fromJson(encryptMsg.Content, TransitFileJSON.class);
        } else {
            jsonObject = JsonUtils.getGson().fromJson(message.getBody(), TransitFileJSON.class);
            if(jsonObject == null || TextUtils.isEmpty(jsonObject.HttpUrl)){
                jsonObject = JsonUtils.getGson().fromJson(message.getExt(), TransitFileJSON.class);
            }
        }
        url = QtalkStringUtils.addFilePathDomain(jsonObject.HttpUrl, true);
        StringBuilder urlbuilder = new StringBuilder(url);
        Protocol.addBasicParamsOnHead(urlbuilder);
        url = urlbuilder.toString();

        fileName = jsonObject.FileName;
        fileSize = jsonObject.FileSize;
        localFile = jsonObject.LocalFile;

        tvFileName.setText(getText(R.string.atom_ui_tip_filename) + ": " + fileName);

        Uri uri = Uri.parse(url);

        if (TextUtils.isEmpty(fileName)) {//json没有的filename从url里面获取
            String temp = uri.getQueryParameter("name");
            if (TextUtils.isEmpty(temp)) {//url 没有默认一个.temp后缀的文件
                fileName = System.currentTimeMillis() + ".temp";
            } else {
                fileName = temp;
            }
        }
//        fileMd5Path="";
        if (TextUtils.isEmpty(jsonObject.FILEMD5)) {
            if (!jsonObject.noMD5) {
                fileMd5Path = uri.getLastPathSegment();
                if (fileMd5Path != null && fileMd5Path.lastIndexOf(".") != -1) {//含后缀的md5需要截取
                    fileMd5Path = fileMd5Path.substring(0, fileMd5Path.lastIndexOf("."));
                }
            }

        } else {
            fileMd5Path = jsonObject.FILEMD5;
        }

         if (!TextUtils.isEmpty(fileMd5Path)) {
            fileName = fileMd5Path + File.separator + fileName;
        }
//        }

        mHandler = new FileDownloadHandler(new WeakReference<DownloadFileActivity>(this));
        initViews();
    }

    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        super.onDestroy();
    }

    private void bindViews() {
        tvFileName = (TextView) findViewById(R.id.file_name);
        tvFileSize = (TextView) findViewById(R.id.file_size);
        btnDownload = (Button) findViewById(R.id.download);
        tvfile_path = (TextView) findViewById(R.id.file_path);
        numberProgressBar = (NumberProgressBar) findViewById(R.id.number_progress_bar);
    }

    public void downloadFile() {
        btnDownload.setEnabled(false);
        final DownloadRequest request = new DownloadRequest();
        request.savePath = FileUtils.savePath + fileName;
        request.url = url;
        request.requestComplete = result -> {
            if (mHandler != null) {
                Message msg = mHandler.obtainMessage();
                msg.what = DOWNLOAD_FINISH;
                mHandler.sendMessage(msg);
            }
        };
        request.progressListener = (bytewriten, length, complete) -> {
            if (mHandler != null) {
                int current = (int) (bytewriten * 100 / length);
                Message msg = mHandler.obtainMessage();
                msg.what = UPDATE_PROGRESS;
                Bundle b = new Bundle();
                b.putInt(PROGRESS, current);
                msg.setData(b);
                mHandler.sendMessage(msg);
            }
        };
        CommonDownloader.getInsatnce().setDownloadRequest(request);
    }

    //android获取一个用于打开文件的intent
    public Intent getFileIntent(String param) {
        String prefix = param;
        String fileType = "text/plain";
        int index = param.lastIndexOf(".");
        if (index != -1) {
            prefix = param.substring(index);
        }
        for (int i = 0; i < Utils.MIME_MapTable.length; i++) {
            if (prefix.equals(Utils.MIME_MapTable[i][0])) {
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
            Uri fileUri = FileProvider.getUriForFile(CommonConfig.globalContext, ProviderUtil.getFileProviderName(DownloadFileActivity.this), file);//android 7.0以上
            intent.setDataAndType(fileUri, fileType);
        } else {
            intent.setDataAndType(Uri.fromFile(file), fileType);
        }
        return intent;
    }

    @Override
    public void onStart() {
        super.onStart();
//        if (HttpUtils.checkDownloading(url)) {
//            downloadFile();
//        }
        btnDownload.setOnClickListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    void initViews() {
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(R.string.atom_ui_btn_download_file);

        tvFileSize.setText(getText(R.string.atom_ui_tip_filesize) + ": " + fileSize);
        final File file = new File(FileUtils.savePath + fileName);
         File lFile = null;
        if(!TextUtils.isEmpty(localFile)){
            lFile = new File(localFile);
        }

        if (file.exists()) {
            //文件存在校验md5是否一致
            DispatchHelper.Async("getFileMd5", true, () -> {

                String md5 = FileUtils.fileToMD5(file);
                if (!TextUtils.isEmpty(md5) && !TextUtils.isEmpty(fileMd5Path) && md5.equals(fileMd5Path.toLowerCase())) {
                    runOnUiThread(()->{
                        tvfile_path.setText(getText(R.string.atom_ui_tip_filepath) + ": " + file.getAbsolutePath());
                        tvfile_path.setVisibility(View.VISIBLE);
                        btnDownload.setText(R.string.atom_ui_tip_open_other_app);
                        btnDownload.setBackgroundResource(R.drawable.atom_ui_common_button_blue_selector);
                        btnDownload.setId(R.id.atom_ui_open_file);
                        showShareTitleView(file);
                    });

                } else {
                    file.delete();
                }
            });
        }else if(lFile!=null&&lFile.exists()){
            File finalLFile = lFile;
            DispatchHelper.Async("getFileMd5", true, ()->{
                String md5 = FileUtils.fileToMD5(finalLFile);
                if (!TextUtils.isEmpty(md5) && !TextUtils.isEmpty(fileMd5Path) && md5.equals(fileMd5Path.toLowerCase())) {
                    runOnUiThread(()->{
                        tvfile_path.setText(getText(R.string.atom_ui_tip_filepath) + ": " + finalLFile.getAbsolutePath());
                        tvfile_path.setVisibility(View.VISIBLE);
                        btnDownload.setText(R.string.atom_ui_tip_open_other_app);
                        btnDownload.setBackgroundResource(R.drawable.atom_ui_common_button_blue_selector);
                        btnDownload.setId(R.id.atom_ui_open_local_file);
                        showShareTitleView(finalLFile);
                    });

                } else {
                    finalLFile.delete();
                }
            });
        }
    }

    private void showShareTitleView(final File file) {
        if (!file.exists()) {
            return;
        }
        setActionBarRightSpecial(R.string.atom_ui_new_share);
        setActionBarRightIconSpecialClick(v -> externalShare(file));
    }

    private void externalShare(File file) {
        Intent share_intent = new Intent();
        share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
        share_intent.setType("*/*");  //设置分享内容的类型
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            share_intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(this, ProviderUtil.getFileProviderName(DownloadFileActivity.this), file);//android 7.0以上
        } else {
            uri = Uri.fromFile(file);
        }
        share_intent.putExtra(Intent.EXTRA_STREAM, uri);
        //创建分享的Dialog
        share_intent = Intent.createChooser(share_intent, "分享");
        startActivity(share_intent);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.download) {
            downloadFile();
        } else if (v.getId() == R.id.atom_ui_open_file) {
            Intent intent = getFileIntent(FileUtils.savePath + fileName);
            try {
                startActivity(intent);
            } catch (Exception e) {
                LogUtil.e(TAG, "ERROR", e);
                Toast.makeText(this, R.string.atom_ui_tip_file_open_failed, Toast.LENGTH_LONG).show();
            }
        }else if(v.getId() ==R.id.atom_ui_open_local_file){
            Intent intent = getFileIntent(localFile);
            try {
                startActivity(intent);
            } catch (Exception e) {
                LogUtil.e(TAG, "ERROR", e);
                Toast.makeText(this, R.string.atom_ui_tip_file_open_failed, Toast.LENGTH_LONG).show();
            }
        }
    }
}
