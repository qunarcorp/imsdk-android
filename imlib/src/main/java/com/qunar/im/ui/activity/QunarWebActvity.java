package com.qunar.im.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
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
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.other.QtalkSDK;
import com.qunar.im.base.jsonbean.ExtendMessageEntity;
import com.qunar.im.base.jsonbean.QVTResponseResult;
import com.qunar.im.base.presenter.messageHandler.ConversitionType;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.util.BinaryUtil;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.Utils;
import com.qunar.im.base.util.graphics.MyDiskCache;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.core.utils.GlobalConfigManager;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.R;
import com.qunar.im.ui.broadcastreceivers.ShareReceiver;
import com.qunar.im.ui.imagepicker.ImagePicker;
import com.qunar.im.ui.imagepicker.bean.ImageItem;
import com.qunar.im.ui.util.VacationAdUtil;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.dialog.BottomDialog;
import com.qunar.im.utils.QtalkStringUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 *
 */
public class QunarWebActvity extends IMBaseActivity implements BottomDialog.OnItemSelectedListener {
    public final static String IS_HIDE_BAR = "ishidebar";
    public final static String UA = "useragent";
    protected final String DOMAIN = "qunar.com";//
    protected WebView mWebView;
    protected ProgressBar mProgressBar;
    protected ProgressBar pb_central;
    protected RelativeLayout root_container;

    protected String mUrl;
    protected long mDownloadedFileID = -1;
    protected String from = "";

    protected String USER_AGENT = "qunartalk-android";
    protected VacationAdUtil adUtil;
    protected String inputUA;
    protected boolean isHideBar;
    private String shareImg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = getIntent().getData();
        from = getIntent().getStringExtra(Constants.BundleKey.WEB_FROM);
        mUrl = uri.toString();
        inputUA = getIntent().getStringExtra(UA);
        isHideBar = getIntent().getBooleanExtra(IS_HIDE_BAR, false);
        if (uri.getScheme() == null && !mUrl.startsWith("http://") &&
                !mUrl.startsWith("https://")
                && !mUrl.startsWith("file://")) {
            mUrl = "http://" + mUrl;
        }
//        finish();

        if ((Constants.BundleValue.HONGBAO.equals(from)) &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            Window window = getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            root_container.setBackgroundColor(getResources().getColor(R.color.atom_ui_trans_dark_gray));
        }
        setContentView(R.layout.atom_ui_activity_qunar_web_actvity);
        bindViews();

        initView();
        if (isHideBar && mNewActionBar != null && mNewActionBar.getVisibility() == View.VISIBLE) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////                mNewActionBar.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
////                        getResources().getDimensionPixelSize(R.dimen.atom_ui_action_bar_padding)));
//                mNewActionBar.setVisibility(View.INVISIBLE);
//            } else {
                mNewActionBar.setVisibility(View.GONE);
//            }
        }
        initWebView();
        loadUrl();
        EventBus.getDefault().register(this);
        adUtil = new VacationAdUtil(this);
    }


    protected void initWebView() {
        mProgressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 8);
        mProgressBar.setLayoutParams(layoutParams);
        mWebView.addView(mProgressBar);
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onPermissionRequest(PermissionRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.deny();
                }
            }

            //            添加弹窗 alert功能
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
//                AlertDialog.Builder b = new AlertDialog.Builder(QunarWebActvity.this);
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
                if (TextUtils.isEmpty(shareImg) && (url.indexOf(".jpg") > 0 || url.indexOf(".png") > 0)) {
                    shareImg = url;
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
                if ("qim://close".equals(url)) {
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
                return false;
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                shareImg = "";
                pb_central.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (mNewActionBar != null) {
                    String titile = view.getTitle();
                    if (!TextUtils.isEmpty(titile)) {
                        setActionBarTitle(titile);
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
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.getSettings().setLoadsImagesAutomatically(true);
        } else {
            mWebView.getSettings().setLoadsImagesAutomatically(false);
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
                    USER_AGENT += "-" + GlobalConfigManager.getAppName();
                } else {
                    //qchat就是 qunarchat-设备-应用类型
                    USER_AGENT = "qunarchat-android-" + GlobalConfigManager.getAppName();
                }
            }
        } else {
            USER_AGENT = inputUA;
        }
        WebSettings webSettings = mWebView.getSettings();
        mWebView.getSettings().setUserAgentString(webSettings.getUserAgentString() + ";" + USER_AGENT);
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
        if(CommonConfig.isQtalk){
            setActionBarTitle("QTalk");
        }else{
            setActionBarTitle("QChat");
        }
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
            setActionBarRightIconSpecialClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(QunarWebActvity.this, BugreportActivity.class);
                    startActivity(intent);
                }
            });
        }
        setActionBarLeftClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setActionBarTitleClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCounts++;
                if (clickCounts > 10) {
                    Intent intent1 = new Intent(QunarWebActvity.this, LoginActivity.class);
                    startActivity(intent1);
                    finish();
                }
            }
        });
    }

    public void synCookie() {

//         //* wiki ceshi start **
//
//        String wikiUrl = QtalkNavicationService.getInstance().getWikiurl();
//        if(!TextUtils.isEmpty(wikiUrl)){
//            Uri uri = Uri.parse(wikiUrl);
//            DOMAIN = uri.getHost();
//        }
//        //* wiki ceshi end ***

        //下面的变量都是暂时处理 日后逻辑清晰重新构成
        String domain = QtalkNavicationService.getInstance().getXmppdomain();
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
                    cookieManager.setCookie(DOMAIN, "_v=" + qvtResponseResult.data.vcookie + "; domain=" + DOMAIN);
                    cookieManager.setCookie(DOMAIN, "_t=" + qvtResponseResult.data.tcookie + "; domain=" + DOMAIN);
                    cookieManager.setCookie(DOMAIN, "_q=" + qvtResponseResult.data.qcookie + "; domain=" + DOMAIN);
                    cookieManager.setCookie(DOMAIN, "q_d=" + QtalkNavicationService.getInstance().getXmppdomain() + "; domain=" + DOMAIN);
                    qvt = true;
                }
            }
            if (!isHistory) {
                if (!qvt) {
                    cookieManager.setCookie(DOMAIN, "_v=null; domain=" + DOMAIN);
                    cookieManager.setCookie(DOMAIN, "_t=null; domain=" + DOMAIN);
                    cookieManager.setCookie(DOMAIN, "_q=null; domain=" + DOMAIN);
                    cookieManager.setCookie(DOMAIN, "q_d=null; domain=" + DOMAIN);
                }
            }
        }

        if (mUrl.contains("/package/plts/dashboard")) {
            cookieManager.setCookie(DOMAIN, "q_u=" + CurrentPreference.getInstance().getUserid() + "; domain=" + DOMAIN);
            cookieManager.setCookie(DOMAIN, "q_nm=" + CurrentPreference.getInstance().getUserid() + "; domain=" + DOMAIN);
            cookieManager.setCookie(DOMAIN, "q_ckey=" + Protocol.getCKEY() + "; domain=" + DOMAIN);
            cookieManager.setCookie(DOMAIN, "q_d=" + QtalkNavicationService.getInstance().getXmppdomain() + "; domain=" + DOMAIN);
        }else if(mUrl.contains("/main_controller.php")){
            cookieManager.setCookie(DOMAIN, "q_ckey=" + Protocol.getCKEY() + "; domain=" + DOMAIN);
        }else if (mUrl.contains("mainSite/")) {
            cookieManager.setCookie(DOMAIN, "q_ckey=" + Protocol.getCKEY() +"; domain=." + DOMAIN );
        }


        //qtalk wiki
        if(!TextUtils.isEmpty(QtalkNavicationService.getInstance().getWikiurl())){
            cookieManager.setCookie(DOMAIN, "q_ckey=" + Protocol.getCKEY() + "; domain=" + DOMAIN);
        }

        if (QtalkSDK.getInstance().isLoginStatus()) {
//                mUrl = mUrl + "?user=" + CurrentPreference.getInstance().getUserId() + "&key=" + CommonConfig.verifyKey;
            cookieManager.setCookie(DOMAIN, "_u=" + CurrentPreference.getInstance().getUserid() + "; domain=" + DOMAIN);
            cookieManager.setCookie(DOMAIN, "_k=" + CurrentPreference.getInstance().getVerifyKey() + "; domain=" + DOMAIN);
            cookieManager.setCookie(DOMAIN, "q_d=" + QtalkNavicationService.getInstance().getXmppdomain() + "; domain=" + DOMAIN);
        } else {
            cookieManager.setCookie(DOMAIN, "_u=null; domain=" + DOMAIN);
            cookieManager.setCookie(DOMAIN, "_k=null; domain=" + DOMAIN);
            cookieManager.setCookie(DOMAIN, "q_d=null; domain=" + DOMAIN);
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.flush();
        } else {
            CookieSyncManager.createInstance(this.getApplicationContext());
            CookieSyncManager.getInstance().sync();
        }

//        CookieSyncManager.getInstance().sync();
        Logger.i("QunarWebActvity getCookie:"+ cookieManager.getCookie(DOMAIN));
    }

    private Intent getDefaultIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, mWebView.getTitle() + "\n" + mUrl);
        intent.setType("text/plain");
        return intent;
    }

    @JavascriptInterface
    public String getUserInfoFromClient() {
        Map<String, String> param = new HashMap<String, String>();
        String json = JsonUtils.getGson().toJson(param);
        return json;
    }

    @SuppressLint("JavascriptInterface")
    protected void loadUrl() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Map<String, String> header = new HashMap<String, String>();
//            header.put("x-title-bar", String.valueOf(getResources().
//                    getDimensionPixelSize(R.dimen.atom_ui_action_bar_padding)));
            mWebView.addJavascriptInterface(this, "ClientApi");
            mWebView.loadUrl(mUrl, header);
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
            if (photos != null && photos.size() > 0) {
                Uri uri = Uri.fromFile(new File(photos.get(0)));
                adUtil.reqChoose(uri);
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
            entity.title = mWebView.getTitle();
            entity.linkurl = mWebView.getUrl();
            entity.img = shareImg;
            entity.desc = "点击查看全文";
            String jsonStr = JsonUtils.getGson().toJson(entity);
            Intent intent = new Intent();
            intent.setClass(this, SearchUserActivity.class);
//            intent.putExtra(Constants.BundleKey.IS_TRANS, true);
            intent.putExtra(Constants.BundleKey.IS_FROM_SHARE, true);
            intent.putExtra(Constants.BundleKey.IS_TRANS, true);
            intent.putExtra(ShareReceiver.SHARE_EXTRA_KEY, jsonStr);
            startActivity(intent);
        }
        return true;
    }
}
