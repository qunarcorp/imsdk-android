package com.qunar.im.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.base.jsonbean.AdvertiserBean;
import com.qunar.im.base.protocol.AdvertiserApi;
import com.qunar.im.base.util.BinaryUtil;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.ListUtil;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.graphics.MyDiskCache;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.MyWebView;
import com.qunar.im.ui.view.bigimageview.view.MyGlideUrl;
import com.qunar.im.ui.view.medias.video.VideoView;

import java.io.File;
import java.util.logging.Logger;

/**
 * Created by xinbo.wang on 2016-09-23.
 */
public class AdActivity extends IMBaseActivity implements View.OnClickListener {
    private static final String TAG = AdActivity.class.getSimpleName();
    public static final String KEY_AD_JSON = "adjson";
    MyWebView webView;
    SimpleDraweeView imageView;
    VideoView videoView;
    TextView skip;

    protected String linkUrl="";

    protected AdvertiserBean bean;

    @Override
    public void onCreate(Bundle bundle)
    {
        getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        super.onCreate(bundle);
        setContentView(R.layout.atom_ui_activity_ad);
        injectExtra(getIntent());
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(bean==null|| ListUtil.isEmpty(bean.adlist))
        {
            finish();
        }
        startAd();
    }

    @Override
    public void onStart()
    {
        super.onStart();

    }

    protected void startAd()
    {
        int originType = -1;
        final long interval = bean.carousel ? bean.carouseldelay * 1000 : bean.adsec * 1000;
        if(bean.allowskip){
            getHandler().post(new Runnable() {
                long duration = bean.carousel ? interval * bean.adlist.size() : interval;
                @Override
                public void run() {
                    if(duration==0) {
                        finish();
                        getHandler().removeCallbacks(this);
                        return;
                    }
                    skip.setText(getText(R.string.atom_ui_common_skip)+"("+duration/1000+"S)");
                    duration-=1000;
                    getHandler().postDelayed(this,1000);
                }
            });
        }else{
            skip.setVisibility(View.GONE);
        }
        for(AdvertiserBean.AdContent content:bean.adlist)
        {
            if(originType == -1) originType = content.adtype;
            if(content.adtype!=originType) continue;
            linkUrl = content.linkurl;
            if(content.adtype == 1)
            {
                loadUrl(content.url);

                break;
            }
            else if(content.adtype == 3)
            {
                playVideo(content.url);

                break;
            }
            imageView.setVisibility(View.VISIBLE);
//            FacebookImageUtil.loadWithCache(content.url,imageView);
            if(!TextUtils.isEmpty(content.url)){
                loadImage(this, content.url, imageView);
//                FacebookImageUtil.loadWithCache(content.url,imageView);
            }else{
                loadImage(this, content.url, imageView);
//                FacebookImageUtil.loadWithCache(content.imgurl,imageView);
            }
            if(!bean.carousel)
            {
                break;
            }
            SystemClock.sleep(interval);
        }
    }

    private void loadImage(Context context, String url, ImageView imageView){
        if(TextUtils.isEmpty(url)){
            com.orhanobut.logger.Logger.i("图片崩溃错误1");
            return;
        }
        Glide.with(context)                             //配置上下文
                .load(url)
//                .load(new MyGlideUrl(url))      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                .centerCrop()
                .error(R.drawable.atom_ui_ic_default_image)           //设置错误图片
                .placeholder(R.drawable.atom_ui_ic_default_image)     //设置占位图片
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)//缓存全尺寸
                .dontAnimate()
                .into(imageView);
    }

    protected void playVideo(String url)
    {
        String name = BinaryUtil.MD5(url) + ".cnt";
        File file = new File(CommonConfig.globalContext.getFilesDir(), name);
        if(!file.exists()){
            AdvertiserApi.deleteAdConfig(this);
            finish();
        }
        videoView.setVisibility(View.VISIBLE);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        videoView.setVideoURI(Uri.fromFile(file));
        videoView.requestFocus();
    }

    protected void loadUrl(String url)
    {
        webView.setVisibility(View.VISIBLE);
        webView.clearCache(true);
        webView.clearHistory();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setSavePassword(false);
        // webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        /*** 打开本地缓存提供JS调用 **/
        webView.getSettings().setDomStorageEnabled(true);
        // Set cache size to 8 mb by default. should be more than enough
        webView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        // This next one is crazy. It's the DEFAULT location for your app's cache
        // But it didn't work for me without this line.
        // UPDATE: no hardcoded path. Thanks to Kevin Hawkins
        File cacheDir = MyDiskCache.getTempDir();
        if (cacheDir != null) {
            webView.getSettings().setAppCachePath(cacheDir.getAbsolutePath());
        }
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT <= 15) {
            webView.getSettings().setBuiltInZoomControls(true);
        } else {
            webView.getSettings().setBuiltInZoomControls(false);
        }
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.loadUrl(url);
    }

    protected void injectExtra(Intent intent)
    {
        if(intent!=null)
        {
            Bundle extra = intent.getExtras();
            if(extra.containsKey(KEY_AD_JSON))
            {
                String base64Str = extra.getString(KEY_AD_JSON);
                if(!TextUtils.isEmpty(base64Str)) {
                    String json = new String(Base64.decode(base64Str,Base64.URL_SAFE|Base64.NO_WRAP));

                    try {
                        bean = JsonUtils.getGson().fromJson(json, AdvertiserBean.class);
                    }
                    catch (Exception ex)
                    {
                        LogUtil.e(TAG,"ERROR",ex);
                        finish();
                    }
                }
            }
        }
    }
    protected void initViews()
    {
        webView = (MyWebView) findViewById(R.id.webview);
        imageView = (SimpleDraweeView) findViewById(R.id.imageview);
        videoView = (VideoView) findViewById(R.id.videoView);
        skip = (TextView) findViewById(R.id.skip);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    if (TextUtils.isEmpty(linkUrl)) {
                        return false;
                    }
                    Intent intent = new Intent(AdActivity.this, QunarWebActvity.class);
                    intent.putExtra(QunarWebActvity.IS_HIDE_BAR, false);
                    intent.setData(Uri.parse(linkUrl));
                    startActivity(intent);
                    finish();
                    return true;
                }
                return true;
            }
        });
        imageView.setOnClickListener(this);
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    if (TextUtils.isEmpty(linkUrl)) {
                        return false;
                    }
                    Intent intent = new Intent(AdActivity.this, QunarWebActvity.class);
                    intent.putExtra(QunarWebActvity.IS_HIDE_BAR, false);
                    intent.setData(Uri.parse(linkUrl));
                    startActivity(intent);
                    finish();
                    return true;
                }
                return true;
            }
        });
        skip.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imageview || id == R.id.videoView) {
            if (TextUtils.isEmpty(linkUrl)) return;
            Intent intent = new Intent(this, QunarWebActvity.class);
            intent.putExtra(QunarWebActvity.IS_HIDE_BAR, false);
            intent.setData(Uri.parse(linkUrl));
            startActivity(intent);
            finish();
        } else if (id == R.id.skip) {
            finish();
        }

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.atom_ui_top_in, R.anim.atom_ui_hide_to_bottom);
    }
}
