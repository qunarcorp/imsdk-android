package com.qunar.im.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.MessageUtils;
import com.qunar.im.ui.R;
import com.qunar.im.ui.fragment.WorkWorldBrowersingFragment;
import com.qunar.im.ui.presenter.views.IBrowsingConversationImageView;
import com.qunar.im.ui.util.StatusBarUtil;
import com.qunar.im.ui.view.bigimageview.ImagePreview;
import com.qunar.im.ui.view.bigimageview.bean.ImageInfo;
import com.qunar.im.ui.view.bigimageview.view.listener.OnBigImageClickListener;
import com.qunar.im.ui.view.bigimageview.view.listener.OnBigImageLongClickListener;
import com.qunar.im.ui.view.bigimageview.view.listener.OnBigImagePageChangeListener;
import com.qunar.im.ui.view.bigimageview.view.listener.OnOriginProgressListener;
import com.qunar.im.utils.ConnectionUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Modify by xinbo.wang on 2015/4/24.
 * 本activity  功能更改为中转activity
 */
public class ImageBrowersingActivity extends IMBaseActivity {

    private String mImageUrl;
    private String localPath;
    private String converserId = "";
    private String ofrom;
    private String oto;
    private final static String objPattern = "\\[obj type=\"image\" value=\"([\\S]+)\"([\\w|=|\\s|\\.]+)?\\]";
    private final static Pattern compiledPattern = Pattern.compile(objPattern);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.d("debug", "image oncreate");
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTransparentForImageView(this, null);
        setContentView(R.layout.atom_ui_layout_blank_content);
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey(Constants.BundleKey.CONVERSATION_ID)) {
            initViewPagerBrowsing();
        } else if (bundle.containsKey(Constants.BundleKey.WORK_WORLD_BROWERSING)) {
            initViewWorkWorldBrowsing();
        } else {
            initViews();
        }
    }

    private void initViewWorkWorldBrowsing() {
        WorkWorldBrowersingFragment galleryFragment = new WorkWorldBrowersingFragment();

        galleryFragment.setArguments(getIntent().getExtras());

        getSupportFragmentManager().beginTransaction()

                .replace(R.id.layout_blanck_content, galleryFragment)

                .commit();
    }

    private void initViewPagerBrowsing() {
//        GalleryFragment galleryFragment = new GalleryFragment();
//
//        galleryFragment.setArguments(getIntent().getExtras());
//
//        getSupportFragmentManager().beginTransaction()
//
//                .replace(R.id.layout_blanck_content, galleryFragment)
//
//                .commit();


//        IBrowsingConversationImageView.PreImage cur = new IBrowsingConversationImageView.PreImage();
//        cur.originUrl = mImageUrl;
//        int curPage = urls.indexOf(cur);
//        if(curPage == -1) {
//            IBrowsingConversationImageView.PreImage image = new IBrowsingConversationImageView.PreImage();
//            image.originUrl = mImageUrl;
//            image.smallUrl = FileUtils.toUri(localPath).toString();
//            urls.add(0,image);
//            curPage = urls.size()-1;
//        }


        mImageUrl = getIntent().getExtras().getString(Constants.BundleKey.IMAGE_URL);
//        localPath = getIntent().getExtras().getString(Constants.BundleKey.IMAGE_ON_LOADING);
        converserId = getIntent().getExtras().getString(Constants.BundleKey.CONVERSATION_ID);
        ofrom = getIntent().getExtras().getString(Constants.BundleKey.ORIGIN_FROM);
        oto = getIntent().getExtras().getString(Constants.BundleKey.ORIGIN_TO);


        List<IMMessage> list = null;
        if (!TextUtils.isEmpty(ofrom) && !TextUtils.isEmpty(oto)) {
            list = ConnectionUtil.getInstance().searchImageMsg(ofrom, oto, 50);
        } else {
            list = ConnectionUtil.getInstance().searchImageMsg(converserId, 50);
        }


        int position = -1;
        List<IBrowsingConversationImageView.PreImage> imgs = new LinkedList<>();
        List<ImageInfo> imagesList = new ArrayList<>();
        int index = 0;
        for (int i = list.size()-1; i >=0 ; i--) {

            IMMessage msg = list.get(i);

            Matcher m = compiledPattern.matcher(msg.getBody());
            while (m.find()) {
                String value = m.group(1);
                String ext = null;
                if (m.groupCount() >= 2) {
                    ext = m.group(2);
                }

                MessageUtils.ImageMsgParams params = new MessageUtils.ImageMsgParams();
                ImageInfo info = new ImageInfo();
                if (ext != null && ext.contains("width") && ext.contains("height")) {
                    try {
                        String[] str = ext.trim().split("\\s+");
                        if (str.length > 1) {
                            //处理width = 240.000000　问题
                            params.width = Double.valueOf(str[0].substring(str[0].indexOf("width") + 6)).intValue();
                            params.height = Double.valueOf(str[1].substring(str[1].indexOf("height") + 7)).intValue();

                        }
                    } catch (Exception e) {

                    }
                }

                params.sourceUrl = value;
                MessageUtils.getDownloadFile(params, QunarIMApp.getContext(), true);
                IBrowsingConversationImageView.PreImage image = new IBrowsingConversationImageView.PreImage();
                image.originUrl = params.sourceUrl;
                image.smallUrl = params.smallUrl;
                image.width = params.width;
                image.height = params.height;

                info.setOriginUrl(params.sourceUrl);
                info.setThumbnailUrl(params.smallUrl);
                info.setWidth(params.width);
                info.setHeight(params.height);

                //判断是不是自己发的本地图片
                String extenInfo = msg.getExt();
                if (!TextUtils.isEmpty(extenInfo)) {
                    //判断本地图片是否存在
                    List<Map<String, String>> tempList = ChatTextHelper.getObjList(extenInfo);
                    if (tempList != null && tempList.size() == 1) {
                        String v = tempList.get(0).get("value");
                        if (v.startsWith("file://")) {
                            String localPath = v.substring(7);
                            if (new File(localPath).exists()) {//本地文件存在取ext 不存在取网络url
                                image.localPath = v;
                                info.setLocalPath(v);
                                if (!TextUtils.isEmpty(info.getLocalPath())) {
                                    info.setReturnLocal(true);
                                }
                            }
                        }
                    }
                }
                imgs.add(image);
                if (info.getOriginUrl().equals(mImageUrl)) {
                    position = index;
                }

                index++;

                imagesList.add(info);
            }
        }

        startPreView(position,imagesList);

        finish();


    }


    public void startPreView(int position, List<ImageInfo> imageInfoList) {
        ImagePreview.getInstance()
                // 上下文，必须是activity，不需要担心内存泄漏，本框架已经处理好
                .setContext(this)
                // 从第几张图片开始，索引从0开始哦~
                .setIndex(position)

                //=================================================================================================
                // 有三种设置数据集合的方式，根据自己的需求进行三选一：
                // 1：第一步生成的imageInfo List
                .setImageInfoList(imageInfoList)

                // 2：直接传url List
                //.setImageList(List<String> imageList)

                // 3：只有一张图片的情况，可以直接传入这张图片的url
                //.setImage(String image)
                //=================================================================================================

                // 加载策略，默认为手动模式
                .setLoadStrategy(ImagePreview.LoadStrategy.Default)

                // 保存的文件夹名称，会在SD卡根目录进行文件夹的新建。
                // (你也可设置嵌套模式，比如："BigImageView/Download"，会在SD卡根目录新建BigImageView文件夹，并在BigImageView文件夹中新建Download文件夹)
                .setFolderName("BigImageView/Download")

                // 缩放动画时长，单位ms
                .setZoomTransitionDuration(300)

                // 是否启用点击图片关闭。默认启用
                .setEnableClickClose(true)
                // 是否启用上拉/下拉关闭。默认不启用
                .setEnableDragClose(true)

                // 是否显示关闭页面按钮，在页面左下角。默认不显示
                .setShowCloseButton(false)
                // 设置关闭按钮图片资源，可不填，默认为库中自带：R.drawable.ic_action_close
                .setCloseIconResId(R.drawable.ic_action_close)

                // 是否显示下载按钮，在页面右下角。默认显示
                .setShowDownButton(false)
                // 设置下载按钮图片资源，可不填，默认为库中自带：R.drawable.icon_download_new
                .setDownIconResId(R.drawable.icon_download_new)

                // 设置是否显示顶部的指示器（1/9）默认显示
                .setShowIndicator(true)

                // 设置失败时的占位图，默认为库中自带R.drawable.load_failed，设置为 0 时不显示
                .setErrorPlaceHolder(R.drawable.load_failed)

                // 点击回调
                .setBigImageClickListener(new OnBigImageClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        // ...
                    }
                })
                // 长按回调
                .setBigImageLongClickListener(new OnBigImageLongClickListener() {
                    @Override
                    public boolean onLongClick(View view, int position, Context context) {
                        return false;
                    }

//                    @Override
//                    public boolean onLongClick(View view, int position) {
//                        // ...
//                        return false;
//                    }
                })
                // 页面切换回调
                .setBigImagePageChangeListener(new OnBigImagePageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                })

                //=================================================================================================
                // 设置查看原图时的百分比样式：库中带有一个样式：ImagePreview.PROGRESS_THEME_CIRCLE_TEXT，使用如下：
                .setProgressLayoutId(ImagePreview.PROGRESS_THEME_CIRCLE_TEXT, new OnOriginProgressListener() {
                    @Override
                    public void progress(View parentView, int progress) {

                        // 需要找到进度控件并设置百分比，回调中的parentView即传入的布局的根View，可通过parentView找到控件：
                        ProgressBar progressBar = parentView.findViewById(R.id.sh_progress_view);
                        TextView textView = parentView.findViewById(R.id.sh_progress_text);
                        progressBar.setProgress(progress);
                        String progressText = progress + "%";
                        textView.setText(progressText);
                    }

                    @Override
                    public void finish(View parentView) {
                    }
                })

                // 使用自定义百分比样式，传入自己的布局，并设置回调，再根据parentView找到进度控件进行百分比的设置：
                //.setProgressLayoutId(R.layout.image_progress_layout_theme_1, new OnOriginProgressListener() {
                //    @Override public void progress(View parentView, int progress) {
                //        Log.d(TAG, "progress: " + progress);
                //
                //        ProgressBar progressBar = parentView.findViewById(R.id.progress_horizontal);
                //        progressBar.setProgress(progress);
                //    }
                //
                //    @Override public void finish(View parentView) {
                //        Log.d(TAG, "finish: ");
                //    }
                //})
                //=================================================================================================

                // 开启预览
                .start();
    }

    private void initViews() {
        // Create fragment and define some of it transitions
//        ImageBroswingFragment sharedElementFragment1 = ImageBroswingFragment.newInstance(getIntent().getExtras());
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.layout_blanck_content, sharedElementFragment1)
//                .commit();


        ImagePreview.getInstance()
                // 上下文，必须是activity，不需要担心内存泄漏，本框架已经处理好
                .setContext(this)
                // 从第几张图片开始，索引从0开始哦~
                .setIndex(0)

                //=================================================================================================
                // 有三种设置数据集合的方式，根据自己的需求进行三选一：
                // 1：第一步生成的imageInfo List
                .setImage(getIntent().getExtras().getString(Constants.BundleKey.IMAGE_URL))

                // 2：直接传url List
                //.setImageList(List<String> imageList)

                // 3：只有一张图片的情况，可以直接传入这张图片的url
                //.setImage(String image)
                //=================================================================================================

                // 加载策略，默认为手动模式
                .setLoadStrategy(ImagePreview.LoadStrategy.AlwaysOrigin)

                // 保存的文件夹名称，会在SD卡根目录进行文件夹的新建。
                // (你也可设置嵌套模式，比如："BigImageView/Download"，会在SD卡根目录新建BigImageView文件夹，并在BigImageView文件夹中新建Download文件夹)
                .setFolderName("BigImageView/Download")

                // 缩放动画时长，单位ms
                .setZoomTransitionDuration(300)

                // 是否启用点击图片关闭。默认启用
                .setEnableClickClose(true)
                // 是否启用上拉/下拉关闭。默认不启用
                .setEnableDragClose(true)

                // 是否显示关闭页面按钮，在页面左下角。默认不显示
                .setShowCloseButton(false)
                // 设置关闭按钮图片资源，可不填，默认为库中自带：R.drawable.ic_action_close
                .setCloseIconResId(R.drawable.ic_action_close)

                // 是否显示下载按钮，在页面右下角。默认显示
                .setShowDownButton(false)
                // 设置下载按钮图片资源，可不填，默认为库中自带：R.drawable.icon_download_new
                .setDownIconResId(R.drawable.icon_download_new)

                // 设置是否显示顶部的指示器（1/9）默认显示
                .setShowIndicator(true)

                // 设置失败时的占位图，默认为库中自带R.drawable.load_failed，设置为 0 时不显示
                .setErrorPlaceHolder(R.drawable.load_failed)

                // 点击回调
                .setBigImageClickListener(new OnBigImageClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        // ...
                    }
                })
                // 长按回调
                .setBigImageLongClickListener(new OnBigImageLongClickListener() {
                    @Override
                    public boolean onLongClick(View view, int position, Context context) {
                        return false;
                    }

//                    @Override
//                    public boolean onLongClick(View view, int position) {
//                        // ...
//                        return false;
//                    }
                })
                // 页面切换回调
                .setBigImagePageChangeListener(new OnBigImagePageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                })

                //=================================================================================================
                // 设置查看原图时的百分比样式：库中带有一个样式：ImagePreview.PROGRESS_THEME_CIRCLE_TEXT，使用如下：
                .setProgressLayoutId(ImagePreview.PROGRESS_THEME_CIRCLE_TEXT, new OnOriginProgressListener() {
                    @Override
                    public void progress(View parentView, int progress) {

                        // 需要找到进度控件并设置百分比，回调中的parentView即传入的布局的根View，可通过parentView找到控件：
                        ProgressBar progressBar = parentView.findViewById(R.id.sh_progress_view);
                        TextView textView = parentView.findViewById(R.id.sh_progress_text);
                        progressBar.setProgress(progress);
                        String progressText = progress + "%";
                        textView.setText(progressText);
                    }

                    @Override
                    public void finish(View parentView) {
                    }
                })

                // 使用自定义百分比样式，传入自己的布局，并设置回调，再根据parentView找到进度控件进行百分比的设置：
                //.setProgressLayoutId(R.layout.image_progress_layout_theme_1, new OnOriginProgressListener() {
                //    @Override public void progress(View parentView, int progress) {
                //        Log.d(TAG, "progress: " + progress);
                //
                //        ProgressBar progressBar = parentView.findViewById(R.id.progress_horizontal);
                //        progressBar.setProgress(progress);
                //    }
                //
                //    @Override public void finish(View parentView) {
                //        Log.d(TAG, "finish: ");
                //    }
                //})
                //=================================================================================================

                // 开启预览
                .start();

    }
}