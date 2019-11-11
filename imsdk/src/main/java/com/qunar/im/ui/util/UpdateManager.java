package com.qunar.im.ui.util;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.jsonbean.GetUpdateInfoResult;
import com.qunar.im.base.jsonbean.UpdateResponse;
import com.qunar.im.base.protocol.HttpRequestCallback;
import com.qunar.im.base.protocol.HttpUrlConnectionHandler;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.util.FileUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.Utils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.core.utils.GlobalConfigManager;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.R;
import com.qunar.im.ui.imagepicker.util.ProviderUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

/**
 * 应用程序更新工具包
 */
public class UpdateManager {
    private static final String TAG="UpdateManager";

    private  String APK_NAME;
    private  String APK_PACKAGE;

    private  String API_URL;

    private static final String STARTALK_URL = "https://im.qunar.com/package/newapi";

    private static final int DOWN_NOSDCARD = 0;
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private static final int DOWN_ERROR = 3;

    private static final int DIALOG_TYPE_LATEST = 0;
    private static final int DIALOG_TYPE_FAIL   = 1;
    
	private static UpdateManager updateManager;
	
	private Context mContext;
	//通知对话框
	private Dialog noticeDialog;
	//下载对话框
	private Dialog downloadDialog;
	//'已经是最新' 或者 '无法获取最新版本' 的对话框
	private Dialog latestOrFailDialog;
    //进度条
    private ProgressBar mProgress;
    //显示下载数值
    private TextView mProgressText;
    //查询动画
    private ProgressDialog mProDialog;
    //进度值
    private int progress;
    //下载线程
    private Thread downLoadThread;
    //终止标记
    private boolean interceptFlag;
	//提示语
	private String updateMsg = "";
    //提示标题
	private String updateTitle = "";
	//返回的安装包url
	private String apkUrl = "";
	//下载包保存路径
    private String savePath = "";
	//apk保存完整路径
	private String apkFilePath = "";
	//临时下载文件路径
	private String tmpFilePath = "";
	//下载文件大小
	private String apkFileSize;
	//已下载文件大小
	private String tmpFileSize;
	
	private String curVersionName = "";
	private int curVersionCode;
	private UpdateResponse mUpdate;

    private Handler uiHandler;
    
    private Handler mHandler = new Handler(){
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				mProgressText.setText(tmpFileSize + "/" + apkFileSize);
				break;
			case DOWN_OVER:
				downloadDialog.dismiss();
				installApk();
				break;
			case DOWN_ERROR:
                mProgressText.setText("下载失败，请检查网络连接");
				break;
			case DOWN_NOSDCARD:
				downloadDialog.dismiss();
				Utils.showToast(mContext, "无法下载安装文件，请检查SD卡是否挂载");
				break;
			}
    	}
    };
    
	public static UpdateManager getUpdateManager() {
		if(updateManager == null){
			updateManager = new UpdateManager();
		}
		updateManager.interceptFlag = false;
		return updateManager;
	}

    public UpdateManager()
    {
        APK_PACKAGE = CommonConfig.globalContext.getPackageName();
        APK_NAME = GlobalConfigManager.getAppName();
        String user_id = CurrentPreference.getInstance().getUserid();
        if(TextUtils.isEmpty(user_id)){
            user_id = "test";
        }
        if(GlobalConfigManager.isStartalkPlat()) {
            API_URL = STARTALK_URL + "/nck/client/get_version.qunar?clientname="
                    + GlobalConfigManager.getAppName().toLowerCase()+ "_android&u=" + user_id + "&ver=";
        } else {
            API_URL = QtalkNavicationService.getInstance().getHttpUrl() + "/nck/client/get_version.qunar?clientname="
                    + GlobalConfigManager.getAppName().toLowerCase()+ "_android&u=" + user_id + "&ver=";
        }
    }

    /**
     * 检查App更新
     * @param context
     * @param isShowMsg 是否显示提示消息
     */
    public void checkAppUpdate(Context context, final boolean isShowMsg){
        checkAppUpdate(context, isShowMsg, false);
    }
    /**
     * 检查App更新
     * @param context
     * @param isShowMsg 是否显示提示消息
     */
    public void checkAppUpdate(Context context, final boolean isShowMsg, final boolean isForce){
        getCurrentVersion();
        checkAppUpdate(context, isShowMsg, isForce, 0);
    }

    /**
     * 检查App更新
     * @param context
     * @param isShowMsg
     * @param isForce
     * @param versionCode
     */
    public void checkAppUpdate(Context context, final boolean isShowMsg, final boolean isForce, int versionCode){
        this.mContext = context;
        uiHandler = new Handler(mContext.getMainLooper());
        if(versionCode > 0){
            this.curVersionCode = versionCode;
        }
        if(isShowMsg){
            if(mProDialog == null)
                mProDialog = ProgressDialog.show(mContext, null, "正在检测，请稍后...", true, true);
            else if(mProDialog.isShowing() || (latestOrFailDialog!=null && latestOrFailDialog.isShowing()))
                return;
        }
        String url = API_URL + this.curVersionCode;// + "&cpu=" + Build.CPU_ABI;
        if(isForce) url+="&f=1";
        StringBuilder stringBuilder = new StringBuilder(url);
        Protocol.addBasicParamsOnHead(stringBuilder);
        Logger.i("检查更新 url : " + stringBuilder.toString());
        HttpUrlConnectionHandler.executeGet(stringBuilder.toString(), new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response) {
                UpdateResponse message = null;
                try {
                    String resultString = Protocol.parseStream(response);
                    Logger.i("检查更新 result : " + resultString);
                    GetUpdateInfoResult info = JsonUtils.getGson().fromJson(resultString, GetUpdateInfoResult.class);
                    if(info != null) message = info.data;
                } catch (Exception e) {
                    LogUtil.e(TAG,"ERROR",e);
                    if (mProDialog != null && mProDialog.isShowing()) {
                        mProDialog.dismiss();
                        mProDialog = null;
                    }
                }
                if (message == null)
                    return;
                mUpdate = message;
                if (mProDialog != null && !mProDialog.isShowing()) {
                    return;
                }
                //关闭并释放释放进度条对话框
                if (isShowMsg && mProDialog != null) {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mProDialog.dismiss();
                            mProDialog = null;
                        }
                    });
                }
                //显示检测结果
                if (mUpdate != null) {
                    if (mUpdate.isUpdated && (isForce || mUpdate.forceUpdate
                             || mUpdate.version > CurrentPreference.getInstance().getSkipVersion())) {
                        apkUrl = mUpdate.linkUrl;
                        updateMsg = mUpdate.message;
                        updateTitle = "更新";
                        showNoticeDialog(isForce);
                    } else if (isShowMsg) {
                        showLatestOrFailDialog(DIALOG_TYPE_LATEST);
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (mProDialog != null && !mProDialog.isShowing()) {
                    return;
                }
                //关闭并释放释放进度条对话框
                if (isShowMsg && mProDialog != null) {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mProDialog.dismiss();
                            mProDialog = null;
                        }
                    });
                }
                if (isShowMsg)
                    showLatestOrFailDialog(DIALOG_TYPE_FAIL);
            }
        });
	}

	/**
	 * 显示'已经是最新'或者'无法获取版本信息'对话框
	 */
	private void showLatestOrFailDialog(final int dialogType) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (latestOrFailDialog != null) {
                    //关闭并释放之前的对话框
                    latestOrFailDialog.dismiss();
                    latestOrFailDialog = null;
                }
                Builder builder = new Builder(mContext);
                builder.setTitle(getString(R.string.atom_ui_common_prompt));
                if (dialogType == DIALOG_TYPE_LATEST) {
                    builder.setMessage(getString(R.string.atom_ui_latest_version));
                } else if (dialogType == DIALOG_TYPE_FAIL) {
                    builder.setMessage("无法获取版本更新信息");
                }
                builder.setPositiveButton(getString(R.string.atom_ui_ok), null);
                latestOrFailDialog = builder.create();
                latestOrFailDialog.show();
            }
        });
	}

	private String getString(int id){
        return CommonConfig.globalContext.getString(id);
    }

	/**
	 * 获取当前客户端版本信息
	 */
	private void getCurrentVersion(){
        curVersionName = QunarIMApp.getQunarIMApp().getVersionName();
        curVersionCode = QunarIMApp.getQunarIMApp().getVersion();
    }

	/**
	 * 显示版本更新通知对话框
	 */
	private void showNoticeDialog(final boolean isForce){
	    //防止多次弹窗
	    if(noticeDialog != null && noticeDialog.isShowing()){
	        noticeDialog.dismiss();
        }
		final Builder builder = new Builder(mContext);
	    View view = LayoutInflater.from(mContext).inflate(R.layout.atom_ui_dialog_update,null);
	    TextView content = (TextView) view.findViewById(R.id.atom_ui_update_content);
        TextView un_update = (TextView) view.findViewById(R.id.atom_ui_not_update);
        TextView update = (TextView) view.findViewById(R.id.atom_ui_update);
	    builder.setView(view);
//		builder.setTitle(updateTitle);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(updateMsg);
//		builder.setMessage(spannableStringBuilder);
        content.setText(spannableStringBuilder);
        builder.setCancelable(false);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        noticeDialog.dismiss();
                        showDownloadDialog();
                    }
                });
            }
        });
        String cancelTitle = getString(R.string.atom_ui_common_skip);
        if(mUpdate.forceUpdate){
            cancelTitle=getString(R.string.atom_ui_exit_text);
        }
        else if(isForce)
        {
            cancelTitle = getString(R.string.atom_ui_next_time);
        }
        un_update.setText(cancelTitle);
        un_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUpdate.forceUpdate) {
                    System.exit(0);
                } else {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(mUpdate.version!=CurrentPreference.getInstance().getSkipVersion()) {
                                CurrentPreference.getInstance().setSkipVersion(mUpdate.version);
//                                CurrentPreference.getInstance().saveExtConfig();
                            }
                            noticeDialog.dismiss();
                        }
                    });
                }
            }
        });

        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                noticeDialog = builder.create();
                noticeDialog.setCanceledOnTouchOutside(false);
                noticeDialog.show();
            }
        });
	}

	/**
	 * 显示下载对话框
	 */
	private void showDownloadDialog(){
		Builder builder = new Builder(mContext);
		builder.setTitle("正在下载新版本");

		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.atom_ui_update_progress, null);
		mProgress = v.findViewById(R.id.update_progress);
		mProgressText = v.findViewById(R.id.update_progress_text);

		builder.setView(v);
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				interceptFlag = true;
			}
		});
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
				interceptFlag = true;
			}
		});
		downloadDialog = builder.create();
		downloadDialog.setCanceledOnTouchOutside(false);
		downloadDialog.show();

		downloadApk();
	}

	private Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
            FileOutputStream fos=null;
            InputStream is=null;
			try {
				String apkName = APK_NAME+mUpdate.version+".apk";
				String tmpApk = APK_NAME+mUpdate.version+".tmp";
				//判断是否挂载了SD卡
				String storageState = Environment.getExternalStorageState();
				if(storageState.equals(Environment.MEDIA_MOUNTED)){
					savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/qtalk/update/";
					File file = new File(savePath);
					if(!file.exists()){
						file.mkdirs();
					}
					apkFilePath = savePath + apkName;
					tmpFilePath = savePath + tmpApk;
				}

				//没有挂载SD卡，无法下载文件
				if(TextUtils.isEmpty(apkFilePath)){
					mHandler.sendEmptyMessage(DOWN_NOSDCARD);
					return;
				}

				File ApkFile = new File(apkFilePath);

				//是否已下载更新文件
				if(ApkFile.exists()){
                    String localMD5 = FileUtils.fileToMD5(ApkFile).toLowerCase();
                    if(localMD5.equals(mUpdate.md5.toLowerCase())) {
                        downloadDialog.dismiss();
                        installApk();
                        return;
                    }
                    ApkFile.delete();
				}

				//输出临时下载文件
				File tmpFile = new File(tmpFilePath);
				fos = new FileOutputStream(tmpFile);

				URL url = new URL(apkUrl);
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                if(conn.getResponseCode() == 200) {
//                    int length = conn.getContentLength();
                    long length=mUpdate.fileSize;
                    conn = (HttpURLConnection)url.openConnection();
                    is = conn.getInputStream();

                    //显示文件大小格式：2个小数点显示
                    DecimalFormat df = new DecimalFormat("0.00");
                    //进度条下面显示的总文件大小
                    apkFileSize = df.format((double) length / 1024 / 1024) + "MB";

                    int count = 0;
                    byte buf[] = new byte[1024];

                    do {
                        int numread = is.read(buf);
                        count += numread;
                        //进度条下面显示的当前下载文件大小
                        tmpFileSize = df.format((double) count / 1024 / 1024) + "MB";
                        //当前进度值
                        progress = (int) (((double) count / length) * 100);
                        //更新进度
                        mHandler.sendEmptyMessage(DOWN_UPDATE);
                        if (numread <= 0) {
                            //下载完成 - 将临时下载文件转成APK文件
                            if (tmpFile.renameTo(ApkFile)) {
                                //通知安装
                                mHandler.sendEmptyMessage(DOWN_OVER);
                            }
                            break;
                        }
                        fos.write(buf, 0, numread);
                    } while (!interceptFlag);//点击取消就停止下载
                }
                conn.disconnect();
			} catch (Exception e) {
                LogUtil.e(TAG,"ERROR",e);
                mHandler.sendEmptyMessage(DOWN_ERROR);
			}finally {
                try {
                    if(is!=null)is.close();
                    if(fos!=null)fos.close();
                } catch (IOException e) {
                    LogUtil.e(TAG,"ERROR",e);
                }
            }

		}
	};

	/**
	* 下载apk
	*/
	private void downloadApk(){
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}

	/**
     * 安装apk
    */
    private void installApk(){
        File apkfile = new File(apkFilePath);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= 24) {
            i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(mContext,
                    ProviderUtil.getFileProviderName(mContext), apkfile);
            i.setDataAndType(contentUri, "application/vnd.android.package-archive");
        }else{
            //        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
            i.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        mContext.startActivity(i);
    }
}
