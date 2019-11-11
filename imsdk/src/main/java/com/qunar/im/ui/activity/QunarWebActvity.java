
package com.qunar.im.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.base.jsonbean.ExtendMessageEntity;
import com.qunar.im.base.jsonbean.QVTResponseResult;
import com.qunar.im.base.module.ImageItem;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.util.BinaryUtil;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.ListUtil;
import com.qunar.im.base.util.Utils;
import com.qunar.im.base.util.graphics.MyDiskCache;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.core.utils.GlobalConfigManager;
import com.qunar.im.other.QtalkSDK;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.R;
import com.qunar.im.ui.broadcastreceivers.ShareReceiver;
import com.qunar.im.ui.imagepicker.ImagePicker;
import com.qunar.im.ui.util.NavConfigUtils;
import com.qunar.im.ui.util.ShareUtil;
import com.qunar.im.ui.util.VacationAdUtil;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.bigimageview.tool.utility.image.DownloadPictureUtil;
import com.qunar.im.ui.view.dialog.BottomDialog;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.QtalkStringUtils;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

import static com.qunar.im.ui.activity.ShareWorkWorldRouteActivity.WORKWORLDSHARE;

/**
 *
 */
public class QunarWebActvity extends IMBaseActivity implements BottomDialog.OnItemSelectedListener {


    public static int REQUEST_CODE_ENABLE_LOCATION = 100;
    public static int REQUEST_CODE_ACCESS_LOCATION_PERMISSION = 101;


    private GeolocationPermissions.Callback mGeolocationPermissionsCallback;
    private String mOrigin;

    private boolean mShowRequestPermissionRationale = false;

    private static final String VIDEO_DEFAULT_UA = "Mozilla/5.0 (BB10; Touch) AppleWebKit/537.1+ (KHTML, like Gecko) Version/10.0.0.1337 Mobile Safari/537.1+";


    public final static String IS_HIDE_BAR = "ishidebar";
    public final static String UA = "useragent";
    protected String DOMAIN = ".qunar.com";//
    protected WebView mWebView;
    protected ProgressBar mProgressBar;
    protected ProgressBar pb_central;
    protected RelativeLayout root_container;

    protected String mUrl;
    protected URL selfUrl;
    protected long mDownloadedFileID = -1;
    protected String from = "";

    protected String USER_AGENT = "qunartalk-android";
    protected VacationAdUtil adUtil;
    protected String inputUA;
    protected boolean isHideBar;
    protected boolean isVideoAudioCall;
    private String videoCaller;
    private String videoCallee;
    private List<String> shareImgs = new ArrayList<>();
    private String shareTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!TextUtils.isEmpty(QtalkNavicationService.getInstance().getDomainhost())){
            DOMAIN = QtalkNavicationService.getInstance().getDomainhost();

        }

        Uri uri = getIntent().getData();
        from = getIntent().getStringExtra(Constants.BundleKey.WEB_FROM);
        mUrl = uri.toString();
        inputUA = getIntent().getStringExtra(UA);
        isHideBar = getIntent().getBooleanExtra(IS_HIDE_BAR, false);
        videoCaller = getIntent().getStringExtra(Constants.BundleKey.VIDEO_AUODIO_CALLER);
        videoCallee = getIntent().getStringExtra(Constants.BundleKey.VIDEO_AUODIO_CALLEE);
        isVideoAudioCall = getIntent().getBooleanExtra(Constants.BundleKey.IS_VIDEO_AUDIO_CALL, false);
        if (uri.getScheme() == null && !mUrl.startsWith("http://") &&
                !mUrl.startsWith("https://")
                && !mUrl.startsWith("file://")) {
            mUrl = "http://" + mUrl;
        }
        try {
            selfUrl = new URL(mUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
//        finish();

        if ((Constants.BundleValue.HONGBAO.equals(from)) &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        }
        setContentView(R.layout.atom_ui_activity_qunar_web_actvity);
        bindViews();

        initView();
        if (isHideBar && mNewActionBar != null && mNewActionBar.getVisibility() == View.VISIBLE) {
            mNewActionBar.setVisibility(View.GONE);
        }
        initWebView();
        loadUrl();
        EventBus.getDefault().register(this);
        adUtil = new VacationAdUtil(this);
    }

    private boolean hasAccessLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
        }
    }

    private boolean isEnabledLocationFunction() {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    private void doJudgeLocationServiceEnabled() {
        //是否开启定位
        if (isEnabledLocationFunction()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("温馨提示");
            builder.setMessage(String.format("网站%s，正在请求使用您当前的位置，是否许可？", mOrigin));
            builder.setPositiveButton("许可", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mGeolocationPermissionsCallback.invoke(mOrigin, true, true);
                }
            });
            builder.setNegativeButton("不许可", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mGeolocationPermissionsCallback.invoke(mOrigin, false, false);
                }
            });
            builder.create().show();
        } else {
            //请求开启定位功能
            requestEnableLocationFunction(mOrigin, mGeolocationPermissionsCallback);
        }
    }

    /**
     * 请求开启定位服务
     */
    private void requestEnableLocationFunction(final String origin, final GeolocationPermissions.Callback geolocationPermissionsCallback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("温馨提示");
        builder.setMessage(String.format("网站%s，正在请求使用您当前的位置，是否前往开启定位服务？", origin));
        builder.setPositiveButton("前往开启", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, REQUEST_CODE_ENABLE_LOCATION);
            }
        });
        builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                geolocationPermissionsCallback.invoke(origin, false, false);
            }
        });
        builder.create().show();
    }

    private void doRequestAppSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("温馨提示");
        builder.setMessage(String.format("您禁止了应用获取当前位置的权限，是否前往开启？", mOrigin));
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent mIntent = new Intent();
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= 9) {
                    mIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    mIntent.setData(Uri.fromParts("package", getPackageName(), null));
                } else if (Build.VERSION.SDK_INT <= 8) {
                    mIntent.setAction(Intent.ACTION_VIEW);
                    mIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
                    mIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
                }
                QunarWebActvity.this.startActivity(mIntent);
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mGeolocationPermissionsCallback.invoke(mOrigin, false, false);
            }
        });
        builder.create().show();
    }

    private void requestAccessLocationPermission() {
        // 是否要显示问什么要获取权限的解释界面
        /**
         * 什么情况下 shouldShowRequestPermissionRationale会返回true？
         * - 首次请求权限，但是用户禁止了，但是没有勾选“禁止后不再询问”，这样，之后的请求都会返回true
         * 什么情况下，shouldShowRequestPermissionRationale会返回false？
         * - 首次请求权限或者请求权限时，用户勾选了“禁止后不再询问”，之后的请求都会返回false
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                //请求过定位权限，但是被用户拒绝了（但是没有勾选“禁止后不再询问”）
                // 显示解释权限用途的界面，然后再继续请求权限
                mShowRequestPermissionRationale = true;
            } else {
                mShowRequestPermissionRationale = false;
            }
        } else {
            mShowRequestPermissionRationale = false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("温馨提示");
        builder.setMessage(String.format("网站%s，正在请求使用您当前的位置，是否许可应用获取当前位置权限？", mOrigin));
        builder.setPositiveButton(" 是 ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_ACCESS_LOCATION_PERMISSION);
                } else {
                    //额，版本低，正常情况下，安装默认许可，然鹅，国产ROM各种魔改，有阔轮提前实现了单独授权
                    doRequestAppSetting();
                }
            }
        });
        builder.setNegativeButton(" 否 ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mGeolocationPermissionsCallback.invoke(mOrigin, false, false);
            }
        });
        builder.create().show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void initWebView() {
        mProgressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 8);
        mProgressBar.setLayoutParams(layoutParams);
        mWebView.addView(mProgressBar);
        mWebView.setWebChromeClient(new WebChromeClient() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                runOnUiThread(() -> request.grant(request.getResources()));
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                mOrigin = origin;
                mGeolocationPermissionsCallback = callback;
                //是否拥有定位权限
                if (hasAccessLocationPermission()) {
                    doJudgeLocationServiceEnabled();
                } else {
                    //请求定位
                    requestAccessLocationPermission();
                }
            }

            //            添加弹窗 alert功能
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
//                AlertDialog.Builder b = new AlertDialog.Builder(QunarWebActvity.this);
                if(isFinishing()){
                    return true;
                }
                commonDialog.setTitle(getString(R.string.atom_ui_tip_dialog_prompt));
                commonDialog.setMessage(message);
                commonDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                        dialog.dismiss();
                    }
                });
                commonDialog.setCancelable(false);
                commonDialog.show();
                return true;
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    if (mProgressBar.getVisibility() == View.GONE)
                        mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
                if (newProgress > 75) {
                    if (from != null && from.equals(Constants.BundleValue.UC_LOGIN)) {
                        CommonConfig.loginViewHasShown = true;
                        CookieManager cookieManager = CookieManager.getInstance();
                        String cookieStr = cookieManager.getCookie(DOMAIN);
                        if (!TextUtils.isEmpty(cookieStr)) {
                            QVTResponseResult qvtResponseResult = new QVTResponseResult();
                            qvtResponseResult.data = new QVTResponseResult.QVT();
                            String[] cookies = cookieStr.split(";");
                            for (String str : cookies) {
                                str = str.trim();
                                if (str.startsWith("_q")) {
                                    String[] strings = str.split("=");
                                    if (strings.length > 1 && !strings[1].equals("null"))
                                        qvtResponseResult.data.qcookie = strings[1];

                                } else if (str.startsWith("_t")) {
                                    String[] strings = str.split("=");
                                    if (strings.length > 1 && !strings[1].equals("null"))
                                        qvtResponseResult.data.tcookie = strings[1];
                                } else if (str.startsWith("_v")) {
                                    String[] strings = str.split("=");
                                    if (strings.length > 1 && !strings[1].equals("null"))
                                        qvtResponseResult.data.vcookie = strings[1];
                                }
                            }
                            qvtResponseResult.ret = true;
                            if (!TextUtils.isEmpty(qvtResponseResult.data.qcookie)
                                    && !TextUtils.isEmpty(qvtResponseResult.data.vcookie)
                                    && !TextUtils.isEmpty(qvtResponseResult.data.tcookie)) {
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra(Constants.BundleKey.WEB_LOGIN_RESULT, JsonUtils.getGson().toJson(qvtResponseResult));
                                setResult(RESULT_OK, resultIntent);
                                CommonConfig.loginViewHasShown = false;
                                QunarWebActvity.this.finish();
                            }
                        }
                    }
                    pb_central.setVisibility(View.GONE);
                } else {
                    pb_central.setVisibility(View.VISIBLE);
                    try {
                        pb_central.setProgress(newProgress);

                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (adUtil.mUploadMessage != null) return true;
                adUtil.mUploadMessages = filePathCallback;
//                adUtil.selectImage();
                adUtil.selectPic();
                return true;
            }

            @JavascriptInterface
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                if (adUtil.mUploadMessage != null) return;
                adUtil.mUploadMessage = uploadMsg;
//                adUtil.selectImage();
                adUtil.selectPic();
            }

            @JavascriptInterface
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooser(uploadMsg, acceptType);
            }
        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onLoadResource(WebView view, String url) {
                if ((url.indexOf(".jpg") > 0 || url.indexOf(".png") > 0)) {
                    shareImgs.add(url);
                }
                super.onLoadResource(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (Constants.BundleValue.HONGBAO.equals(from)) {
                    finish();
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (TextUtils.isEmpty(url)) return false;
                Uri data = Uri.parse(url);
                //代收管理网页关闭
                if ("qim://close".equals(url) || "qim://publicNav/resetpwdSuccessed".equals(url)) {
                    finish();
                    return true;
                }

                if (url.startsWith("tel:")) {//电话号码跳转
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(data);
                    startActivity(intent);
                    return true;
                } else if (url.startsWith("qchat://open_singlechat")) {//qchat发起和某人的会话
                    Map<String, String> params = Protocol.splitParams(data);
                    if (params != null) {
                        String jid = params.get("userid");//发起会话的jid
                        Intent intent = new Intent(QunarWebActvity.this, PbChatActivity.class);
                        intent.putExtra(PbChatActivity.KEY_JID, jid);
                        intent.putExtra(PbChatActivity.KEY_CHAT_TYPE, String.valueOf(ConversitionType.MSG_TYPE_CHAT));
                        intent.putExtra(PbChatActivity.KEY_IS_CHATROOM, false);
                        startActivity(intent);
                        //更新个人配置
                        String value = JsonUtils.getGson().toJson(params);
                        ConnectionUtil.getInstance().setConversationParams(jid, value);
                        if ("true".equals(params.get("closewnd"))) {
                            finish();
                            return true;
                        }
                    }
                    return true;
                }

                String scheme = data.getScheme();
                if (scheme != null && scheme.equals("qchataphone")) {
                    if (data.getPath().contains("close")) {
                        onBackPressed();
                        return true;
                    }
                }
                if (scheme != null && (scheme.equals("qunartalk")
                        || scheme.equals("qunarchat") ||
                        scheme.equals("qunarlvtu"))) {//qunartalk://redpackage?content=xxx
                    String host = data.getHost().toLowerCase();
                    if (host.equals("closeredpackage")) {
                        setResult(Activity.RESULT_CANCELED);
                        finish();
                        return true;
                    } else if ("redpackge".equals(host)) {

                        String content = data.getQueryParameter("content");
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(Constants.BundleValue.HONGBAO, content);
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                        return true;
                    } else if ("redpackage".equals(host)) {
                        String method = data.getQueryParameter("method");
                        String rid = data.getQueryParameter("rid");
                        if (!TextUtils.isEmpty(method) && !TextUtils.isEmpty(rid)) {
                            Intent selUser = new Intent(QunarWebActvity.this, SearchUserActivity.class);
                            selUser.putExtra(Constants.BundleKey.IS_TRANS, true);
                            selUser.putExtra(Constants.BundleKey.TRANS_MSG, (Serializable) rid);
                            startActivity(selUser);
                            return true;
                        }
                    }
                }
                if(scheme != null && !scheme.startsWith("http")){//其余scheme处理
                    try{
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(data);
                        startActivity(intent);
                        return true;
                    }catch (Exception e){
                        return false;
                    }
                }
                //音视频挂断以及拒绝
                if(data != null && "hangup".equals(data.getQueryParameter("status"))){
                    finish();
                    return true;
                }
                return false;
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                shareImgs.clear();
                pb_central.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (mNewActionBar != null) {
                    shareTitle = view.getTitle();
                    if (!TextUtils.isEmpty(shareTitle)) {
                        setActionBarTitle(shareTitle);
                    }
                }
                if (!mWebView.getSettings().getLoadsImagesAutomatically()) {
                    mWebView.getSettings().setLoadsImagesAutomatically(true);
                }
                super.onPageFinished(view, url);
            }

            @SuppressLint("NewApi")
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError
                    error) {
                //super.onReceivedSslError(view, handler, error);
                // this will ignore the Ssl error and will go forward to your site
                handler.proceed();
                //error.getCertificate();
            }
        });
        mWebView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));
                final DownloadManager mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                BroadcastReceiver onComplete = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // Prevents the occasional unintentional call. I needed this.
                        if (mDownloadedFileID == -1)
                            return;
                        Toast.makeText(getApplicationContext(), getString(R.string.atom_ui_tip_download_success), //To notify the Client that the file is being downloaded
                                Toast.LENGTH_LONG).show();
                        QunarWebActvity.this.finish();
                    }
                };
                // Registers function to listen to the completion of the download.
                QunarWebActvity.this.registerReceiver(onComplete, new
                        IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                request.allowScanningByMediaScanner();
                String fileName = "defaultname";
                String[] contents = contentDisposition.split("filename=");
                if (contents.length > 1) {
                    fileName = contents[contents.length - 1];
                    fileName = fileName.replaceAll("\"", "");
                } else {
                    String[] names = url.split("/");
                    if (names.length > 0) {
                        fileName = names[names.length - 1];
                    }
                }
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

                mDownloadedFileID = mDownloadManager.enqueue(request);
                Toast.makeText(getApplicationContext(), getString(R.string.atom_ui_tip_start_download), //To notify the Client that the file is being downloaded
                        Toast.LENGTH_SHORT).show();

            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.setWebContentsDebuggingEnabled(CommonConfig.isDebug);
        }
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setSavePassword(false);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }

        }

        // webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        /*** 打开本地缓存提供JS调用 **/
        mWebView.getSettings().setDomStorageEnabled(true);
        // Set cache size to 8 mb by default. should be more than enough
        mWebView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        // This next one is crazy. It's the DEFAULT location for your app's cache
        // But it didn't work for me without this line.
        // UPDATE: no hardcoded path. Thanks to Kevin Hawkins
        File cacheDir = MyDiskCache.getTempDir();
        if (cacheDir != null) {
            mWebView.getSettings().setAppCachePath(cacheDir.getAbsolutePath());
        }
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT <= 15) {
            mWebView.getSettings().setBuiltInZoomControls(true);
        } else {
            mWebView.getSettings().setBuiltInZoomControls(false);
        }
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setGeolocationEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.getSettings().setLoadsImagesAutomatically(true);
        } else {
            mWebView.getSettings().setLoadsImagesAutomatically(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
        synCookie();
        if (Constants.BundleValue.HONGBAO.equals(from)) {
            mWebView.setBackgroundColor(0);
        }
        if (TextUtils.isEmpty(inputUA)) {
            if (Constants.BundleValue.ORDER_MANAGE.equals(from)) {
                USER_AGENT = "qchataphone-tts";
            } else {
                if (CommonConfig.isQtalk) {
                    USER_AGENT += "-" + GlobalConfigManager.getAppName() + "-" + GlobalConfigManager.getAppVersion();
                } else {
                    //qchat就是 qunarchat-设备-应用类型
                    USER_AGENT = "qunarchat-android-" + GlobalConfigManager.getAppName() + "-" + GlobalConfigManager.getAppVersion();
                }
            }
        } else {
            USER_AGENT = inputUA;
        }
        WebSettings webSettings = mWebView.getSettings();
        if(isVideoAudioCall){
            mWebView.getSettings().setUserAgentString(VIDEO_DEFAULT_UA);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
            }
        }else {
            mWebView.getSettings().setUserAgentString(webSettings.getUserAgentString() + ";" + USER_AGENT);
        }
    }

    @Override
    protected void onResume() {
        if (from != null && from.equals(Constants.BundleValue.UC_LOGIN)) {
            CommonConfig.loginViewHasShown = true;
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (from != null && from.equals(Constants.BundleValue.UC_LOGIN)) {
            CommonConfig.loginViewHasShown = false;
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void bindViews() {
        mWebView = (WebView) findViewById(R.id.webview);
        pb_central = (ProgressBar) findViewById(R.id.pb_central);
        root_container = (RelativeLayout) findViewById(R.id.root_container);
    }

    int clickCounts;

    public void initView() {
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(GlobalConfigManager.getAppName());
        if (Constants.BundleValue.HONGBAO.equals(from)) {
            mNewActionBar.setVisibility(View.GONE);
        }
        setActionBarRightIcon(R.string.atom_ui_new_share);
        setActionBarRightIconClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomDialog bottomDialog = new BottomDialog(QunarWebActvity.this, false);
                bottomDialog.setOnItemSelectedListener(QunarWebActvity.this);
                bottomDialog.inflateMenu(R.menu.atom_ui_menu_share);
                bottomDialog.show();
            }
        });
        //qchat登录页显示反馈按钮
        if(!CommonConfig.isQtalk && mUrl.contains("user.qunar.com/mobile/login.jsp")){
            setActionBarRightSpecial(R.string.atom_ui_new_feedback);
            setActionBarRightIconSpecialClick(v -> {
                Intent intent = new Intent(QunarWebActvity.this, BugreportActivity.class);
                startActivity(intent);
            });
        }
        setActionBarLeftClick(view -> finish());
        setActionBarTitleClick(v -> {
            clickCounts++;
            if (clickCounts > 10) {
                Intent intent1 = new Intent(QunarWebActvity.this, LoginActivity.class);
                startActivity(intent1);
                finish();
            }
        });
    }

    public void synCookie() {

        //下面的变量都是暂时处理 日后逻辑清晰重新构成
        Boolean isHistory = mUrl.contains("main_controller");
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (isHistory) {
            cookieManager.removeAllCookie();
        }
        if (from != null && from.equals(Constants.BundleValue.UC_LOGIN)) {
            cookieManager.removeAllCookie();
        }
        if (!TextUtils.isEmpty(mUrl) && mUrl.contains(DOMAIN)) {
            String qvtStr = CurrentPreference.getInstance().getQvt();
            boolean qvt = false;
            if (!TextUtils.isEmpty(qvtStr) && !isHistory) {
                QVTResponseResult qvtResponseResult = JsonUtils.getGson().fromJson(qvtStr, QVTResponseResult.class);
                if (qvtResponseResult.ret) {
                    cookieManager.setCookie(DOMAIN, "_v=" + qvtResponseResult.data.vcookie + ";");
                    cookieManager.setCookie(DOMAIN, "_t=" + qvtResponseResult.data.tcookie + ";");
                    cookieManager.setCookie(DOMAIN, "_q=" + qvtResponseResult.data.qcookie + ";");
                    cookieManager.setCookie(DOMAIN, "q_d=" + QtalkNavicationService.getInstance().getXmppdomain() + ";");
                    qvt = true;
                }
            }
//            if (!isHistory) {
//                if (!qvt) {
//                    cookieManager.setCookie(DOMAIN, "_v=;");
//                    cookieManager.setCookie(DOMAIN, "_t=;");
//                    cookieManager.setCookie(DOMAIN, "_q=;");
//                    cookieManager.setCookie(DOMAIN, "q_d=;");
//                }
//            }
        }

        if (mUrl.contains("/package/plts/dashboard")) {
            cookieManager.setCookie(DOMAIN, "q_u=" + CurrentPreference.getInstance().getUserid() + ";");
            cookieManager.setCookie(DOMAIN, "q_nm=" + CurrentPreference.getInstance().getUserid() + ";");
            cookieManager.setCookie(DOMAIN, "q_d=" + QtalkNavicationService.getInstance().getXmppdomain() + ";");
        }

        if(isVideoAudioCall){
            cookieManager.setCookie(DOMAIN, "_caller=" + QtalkStringUtils.parseId(videoCaller) + ";");
            cookieManager.setCookie(DOMAIN, "_callee=" + QtalkStringUtils.parseId(videoCallee) + ";");
            cookieManager.setCookie(DOMAIN, "_calltime=" + System.currentTimeMillis() + ";");
            cookieManager.setCookie(DOMAIN, "u=" + CurrentPreference.getInstance().getUserid() + ";");
        }else {
            cookieManager.setCookie(DOMAIN, "_caller=;");
            cookieManager.setCookie(DOMAIN, "_callee=;");
            cookieManager.setCookie(DOMAIN, "_calltime=;");
            cookieManager.setCookie(DOMAIN, "u=;");
        }


        //默认种qckey
        cookieManager.setCookie(DOMAIN, "q_ckey=" + Protocol.getCKEY() + ";");

        if (QtalkSDK.getInstance().isLoginStatus()) {
            cookieManager.setCookie(DOMAIN, "_u=" + CurrentPreference.getInstance().getUserid() + ";");
            cookieManager.setCookie(DOMAIN, "q_u=" + CurrentPreference.getInstance().getUserid() + ";");
            cookieManager.setCookie(DOMAIN, "_k=" + CurrentPreference.getInstance().getVerifyKey() + ";");
            cookieManager.setCookie(DOMAIN, "q_d=" + QtalkNavicationService.getInstance().getXmppdomain() + ";");
        } else {
            cookieManager.setCookie(DOMAIN, "_u=;");
            cookieManager.setCookie(DOMAIN, "_k=;");
            cookieManager.setCookie(DOMAIN, "q_d=;");
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.flush();
        } else {
            CookieSyncManager.getInstance().sync();
        }

        Logger.i("QunarWebActvity getCookie:"+ cookieManager.getCookie(DOMAIN));
    }

    private Intent getDefaultIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, mWebView.getTitle() + "\n" + mUrl);
        intent.setType("text/plain");
        return intent;
    }

    @JavascriptInterface
    public void sharePic(final String url){
        runOnUiThread(() -> {
            if(TextUtils.isEmpty(url)){
                toast("操作失败！");
                return;
            }
            DownloadPictureUtil.downloadPicture(QunarWebActvity.this, url, str -> {
                File file = new File(str);

                if (file != null && file.exists()) {
//                            ImageBrowsUtil.externalShare(file,CommonConfig.globalContext);
                    ShareUtil.shareImage(CommonConfig.globalContext,file,"分享图片");
                }
            }, false);
        });
    }

    @JavascriptInterface
    public String getCurrentNavUrl(){
        return NavConfigUtils.getCurrentNavUrl();
    }

    @JavascriptInterface
    public String getUserInfoFromClient() {
        Map<String, String> param = new HashMap<String, String>();
        String json = JsonUtils.getGson().toJson(param);
        return json;
    }

    @SuppressLint("JavascriptInterface")
    protected void loadUrl() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mWebView.addJavascriptInterface(this, "ClientApi");
            mWebView.loadUrl(mUrl);
        } else {
            mWebView.loadUrl(mUrl);
        }
    }

    public void onEventMainThread(EventBusEvent.SendTransMsg sendTransMsg) {
        final String transJid = sendTransMsg.transId;
        final String rid = (String) sendTransMsg.msg;
        if (!TextUtils.isEmpty(transJid) && !TextUtils.isEmpty(rid)) {
            String sign = BinaryUtil.MD5(rid + Constants.SIGN_SALT).toLowerCase();
            if (transJid.contains("@conference")) {
                mWebView.loadUrl("javascript:relay_red_env('" + sign + "','" + USER_AGENT + "','" +
                        QtalkStringUtils.parseBareJid(transJid) + "',null)");
            } else {
                mWebView.loadUrl("javascript:relay_red_env('" + sign + "','" + USER_AGENT + "',null,'" +
                        QtalkStringUtils.parseBareJid(transJid) + "')");
            }
            //finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == VacationAdUtil.SELECT_PHOTO_REQUEST) {
            if (data == null) {
                adUtil.cancelUpload();
                return;
            }
            List<String> photos = data.getStringArrayListExtra(PictureSelectorActivity.KEY_SELECTED_PIC);
            if (photos != null && photos.size() > 0) {
                Uri uri = Uri.fromFile(new File(photos.get(0)));
                adUtil.reqChoose(uri);
            }
        }else if(requestCode == VacationAdUtil.ACTIVITY_SELECT_PHOTO){
            if (data == null) {
                adUtil.cancelUpload();
                return;
            }
            List<String> photos = new ArrayList<>();
            //新版图片选择器
            ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
            if (images.size() > 0) {
                for (ImageItem image : images) {
                    photos.add(image.path);
                }
            }
            //可以选择多张图片
            if (photos != null && photos.size() > 0) {
                List<Uri> list = new ArrayList<>();
                for (int i = 0; i < photos.size(); i++) {
                    Uri uri = Uri.fromFile(new File(photos.get(i)));
//                    adUtil.reqChoose(uri);
                   list.add(uri);
                }
                adUtil.reqChooseList(list);

            }
        }else{
            if (mLocationWebChromeClientListener != null) {
                if (mLocationWebChromeClientListener.onReturnFromLocationSetting(requestCode)) {
                    return;
                }
            }
        }


    }

    @Override
    public void onBackPressed() {
        if (mWebView.getUrl() == null || mWebView.getUrl().contains("home.do") ||
                Constants.BundleValue.HONGBAO.equals(from)) {
            this.finish();
        }
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onItemSelected(int id) {
        if (id == R.id.menu_copy) {
            Utils.dropIntoClipboard(mWebView.getUrl(), CommonConfig.globalContext);
        } else if (id == R.id.menu_refresh) {
            mWebView.reload();
        } else if (id == R.id.menu_transfer) {
            startActivity(Intent.createChooser(getDefaultIntent(), getString(R.string.atom_ui_title_shared)));
        } else if (id == R.id.menu_web_share) {
            ExtendMessageEntity entity = new ExtendMessageEntity();
            entity.title = TextUtils.isEmpty(shareTitle) ? mWebView.getTitle() : shareTitle;
            entity.linkurl = mWebView.getUrl();
            if(selfUrl!=null){
                entity.img = selfUrl.getProtocol() + "://" + selfUrl.getHost() + "/favicon.ico";// 保证从域名根路径搜索
            }else {
              entity.img =  ListUtil.isEmpty(shareImgs) ? "" : shareImgs.get(shareImgs.size()/2);
            }
            entity.desc = "点击查看全文";
            String jsonStr = JsonUtils.getGson().toJson(entity);
            Intent intent = new Intent();
            intent.setClass(this, SearchUserActivity.class);
//            intent.putExtra(Constants.BundleKey.IS_TRANS, true);
            intent.putExtra(Constants.BundleKey.IS_FROM_SHARE, true);
            intent.putExtra(Constants.BundleKey.IS_TRANS, true);
            intent.putExtra(ShareReceiver.SHARE_EXTRA_KEY, jsonStr);
            startActivity(intent);
        }else if(id == R.id.menu_workworld_share){

            ExtendMessageEntity entity = new ExtendMessageEntity();
            entity.title = TextUtils.isEmpty(shareTitle) ? mWebView.getTitle() : shareTitle;
            entity.linkurl = mWebView.getUrl();
            if(selfUrl!=null){
                entity.img = selfUrl.getProtocol() + "://" + selfUrl.getHost() + "/favicon.ico";// 保证从域名根路径搜索
            }else {
                entity.img =  ListUtil.isEmpty(shareImgs) ? "" : shareImgs.get(shareImgs.size()/2);
            }
            entity.desc = "点击查看全文";

            Intent workWorldIntent = new Intent(this,WorkWorldReleaseCircleActivity.class);

            workWorldIntent.putExtra(WORKWORLDSHARE,true);
            workWorldIntent.putExtra(Constants.BundleKey.IS_TRANS, true);
            workWorldIntent.putExtra(Constants.BundleKey.IS_FROM_SHARE, true);
            workWorldIntent.putExtra(ShareReceiver.SHARE_EXTRA_KEY, JsonUtils.getGson().toJson(entity));
            startActivity(workWorldIntent);
        }
        return true;
    }


    interface LocationWebChromeClientListener {

        /**
         * 用户从开启定位页面回来了
         */
        boolean onReturnFromLocationSetting(int requestCode);

        /**
         * 请求权限结果
         */
        void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
    }

    private LocationWebChromeClientListener mLocationWebChromeClientListener = new LocationWebChromeClientListener() {
        @Override
        public boolean onReturnFromLocationSetting(int requestCode) {
            if (requestCode == REQUEST_CODE_ENABLE_LOCATION) {
                if (mGeolocationPermissionsCallback != null) {
                    if (isEnabledLocationFunction()) {
                        mGeolocationPermissionsCallback.invoke(mOrigin, true, true);
                    } else {
                        //显然，从设置界面回来还没有开启定位服务，肯定是要拒绝定位了
                        Toast.makeText(QunarWebActvity.this, "您拒绝了定位请求", Toast.LENGTH_SHORT).show();
                        mGeolocationPermissionsCallback.invoke(mOrigin, false, false);
                    }
                }
                return true;
            }
            return false;
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            boolean pass = true;
            for (Integer result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    pass = false;
                    break;
                }
            }
            if (pass) {
                onAccessLocationPermissionGranted();
            } else {
                onAccessLocationPermissionRejected();
            }
        }

        public void onAccessLocationPermissionGranted() {
            doJudgeLocationServiceEnabled();
        }

        public void onAccessLocationPermissionRejected() {
            if (mShowRequestPermissionRationale) {
                Toast.makeText(QunarWebActvity.this, "您拒绝了定位请求", Toast.LENGTH_SHORT).show();
                mGeolocationPermissionsCallback.invoke(mOrigin, false, false);
            } else {
                doRequestAppSetting();
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mLocationWebChromeClientListener != null) {
            mLocationWebChromeClientListener.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//    }
}
