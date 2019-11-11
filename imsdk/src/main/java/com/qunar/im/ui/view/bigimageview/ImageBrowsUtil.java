package com.qunar.im.ui.view.bigimageview;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.zxing.Result;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.module.ImageItemWorkWorldItem;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.base.util.MessageUtils;
import com.qunar.im.ui.R;
import com.qunar.im.ui.presenter.views.IBrowsingConversationImageView;
import com.qunar.im.ui.util.QRRouter;
import com.qunar.im.ui.util.ShareUtil;
import com.qunar.im.ui.util.easyphoto.easyphotos.EasyPhotos;
import com.qunar.im.ui.view.CommonDialog;
import com.qunar.im.ui.view.bigimageview.bean.ImageInfo;
import com.qunar.im.ui.view.bigimageview.tool.utility.image.DownloadPictureUtil;
import com.qunar.im.ui.view.bigimageview.view.listener.OnBigImageClickListener;
import com.qunar.im.ui.view.bigimageview.view.listener.OnBigImageLongClickListener;
import com.qunar.im.ui.view.bigimageview.view.listener.OnBigImagePageChangeListener;
import com.qunar.im.ui.view.bigimageview.view.listener.OnOriginProgressListener;
import com.qunar.im.ui.view.zxing.decode.DecodeBitmap;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.QtalkStringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageBrowsUtil {


    private final static String objPattern = "\\[obj type=\"image\" value=\"([\\S]+)\"([\\w|=|\\s|\\.]+)?\\]";
    private final static Pattern compiledPattern = Pattern.compile(objPattern);
    public static final int converser = 1;
    public static final int workworld = 2;
//    private static Context mContext;

    private static String loadUrl;

    protected static CommonDialog.Builder commonDialog;


    public static void openImageBrowse(ImageBrowseOpenItem item, Context context) {
//        mContext = context;

        openCon(item, context);


    }

    public static void openImageWorkWorld(int position, List<ImageItemWorkWorldItem> list,Context context){
        List<ImageInfo> imageInfoList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            MessageUtils.ImageMsgParams params = new MessageUtils.ImageMsgParams();
            ImageInfo info = new ImageInfo();
            params.sourceUrl = list.get(i).getData();
            MessageUtils.getDownloadFile(params, QunarIMApp.getContext(), true);
            info.setOriginUrl(params.sourceUrl);
            info.setThumbnailUrl(params.smallUrl);
            info.setWidth(params.width);
            info.setHeight(params.height);
            imageInfoList.add(info);
        }

        startPreView(position,imageInfoList,context);

    }


    public static void openImageSingle(final String url, Context context, boolean showBigButton) {
        loadUrl = url;
        ImagePreview.getInstance()
                // 上下文，必须是activity，不需要担心内存泄漏，本框架已经处理好
                .setContext(context)
                // 从第几张图片开始，索引从0开始哦~
                .setIndex(0)

                //=================================================================================================
                // 有三种设置数据集合的方式，根据自己的需求进行三选一：
                // 1：第一步生成的imageInfo List
//                .setImageInfoList(imageInfoList)

                // 2：直接传url List
                //.setImageList(List<String> imageList)

                // 3：只有一张图片的情况，可以直接传入这张图片的url
                .setImage(url)
                //=================================================================================================

                // 加载策略，默认为手动模式
                .setLoadStrategy(showBigButton ? ImagePreview.LoadStrategy.Default : ImagePreview.LoadStrategy.AlwaysOrigin)

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
                    public boolean onLongClick(View view, int position, final Context context) {
                        // ...

                        return false;
                    }
                })
                // 页面切换回调
                .setBigImagePageChangeListener(new OnBigImagePageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
//                        loadUrl = imageInfoList.get(position).getOriginUrl();
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


    private static void openCon(ImageBrowseOpenItem item, Context context) {
        String mImageUrl = QtalkStringUtils.findRealUrl(item.getmImageUrl());
//        localPath = getIntent().getExtras().getString(Constants.BundleKey.IMAGE_ON_LOADING);
        String converserId = item.getConverserId();
        String ofrom = item.getOfrom();
        String oto = item.getOto();


        List<IMMessage> list = null;
        if (!TextUtils.isEmpty(ofrom) && !TextUtils.isEmpty(oto)) {
            list = ConnectionUtil.getInstance().searchImageMsg(ofrom, oto, 50);
        } else {
            list = ConnectionUtil.getInstance().searchImageMsg(converserId, 50);
        }


        int position = -1;
//        List<IBrowsingConversationImageView.PreImage> imgs = new LinkedList<>();
        List<ImageInfo> imagesList = new ArrayList<>();
        int index = 0;
        for (int i = list.size() - 1; i >= 0; i--) {

            IMMessage msg = list.get(i);

//            String realBody ="";
//            if(msg.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypePhoto_VALUE &&
//                    msg.getDirection() == IMMessage.DIRECTION_SEND){
//                realBody = msg.getBody();
//                String ext = msg.getExt();
//                if(!TextUtils.isEmpty(ext)){
//                    //判断本地图片是否存在
//                    List<Map<String, String>> tempList = ChatTextHelper.getObjList(ext);
//                    if(tempList != null && tempList.size() == 1){
//                        String value = tempList.get(0).get("value");
//                        if(value.startsWith("file://")){
//                            String localPath = value.substring(7);
//                            if(new File(localPath).exists()){//本地文件存在取ext 不存在取网络url
//                                realBody = ext;
//                            }
//                        }
//                    }
//                }
//            }else{
//                realBody = msg.getBody();
//            }


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
//                IBrowsingConversationImageView.PreImage image = new IBrowsingConversationImageView.PreImage();
//                image.originUrl = params.sourceUrl;
//                image.smallUrl = params.smallUrl;
//                image.width = params.width;
//                image.height = params.height;

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
//                                image.localPath = v;
                                info.setLocalPath(v);
                                if (!TextUtils.isEmpty(info.getLocalPath())) {
                                    info.setReturnLocal(true);
                                }
                            }
                        }
                    }
                }
//                imgs.add(image);
                if (QtalkStringUtils.findRealUrl(info.originUrl).toLowerCase().equals(mImageUrl.toLowerCase()) || mImageUrl.toLowerCase().equals(info.localPath.toLowerCase())) {
                    position = index;
                }

                index++;

                imagesList.add(info);
            }
        }
        //有可能是搜索情况下未找到本地数据库中图片 则添加进imagelist里面
        if(position==-1){
            MessageUtils.ImageMsgParams params = new MessageUtils.ImageMsgParams();
            params.sourceUrl = item.mImageUrl;
            MessageUtils.getDownloadFile(params, QunarIMApp.getContext(), true);
//                IBrowsingConversationImageView.PreImage image = new IBrowsingConversationImageView.PreImage();
//                image.originUrl = params.sourceUrl;
//                image.smallUrl = params.smallUrl;
//                image.width = params.width;
//                image.height = params.height;
            ImageInfo info = new ImageInfo();
            info.setOriginUrl(params.sourceUrl);
            info.setThumbnailUrl(params.smallUrl);
            info.setWidth(params.width);
            info.setHeight(params.height);
//            imagesList.add(info);
            imagesList.add(0,info);
            position=0;
        }
//            startPreView(position, imagesList, context);

        startPreView(position, imagesList, context);
    }


    public static void startPreView(int position, final List<ImageInfo> imageInfoList, Context context) {
        if (position == -1) {
            position = imageInfoList.size() - 1;
        }
        ImagePreview.getInstance()
                // 上下文，必须是activity，不需要担心内存泄漏，本框架已经处理好
                .setContext(context)
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
                    public boolean onLongClick(final View view, int position, final Context context) {

                        loadUrl = imageInfoList.get(position).getOriginUrl();
                        commonDialog = new CommonDialog.Builder(view.getContext());
                        // ...
                        commonDialog.setItems(view.getContext().getResources().getStringArray(R.array.atom_ui_image_menu)).setOnItemClickListener(new CommonDialog.Builder.OnItemClickListener() {
                            @Override
                            public void OnItemClickListener(Dialog dialog, int postion) {
                                switch (postion) {
                                    case 0:
                                        DownloadPictureUtil.downloadPicture(view.getContext(), loadUrl, new DownloadPictureUtil.PicCallBack() {
                                            @Override
                                            public void onDownLoadSuccess(String str) {
                                                EasyPhotos.notifyMedia(view.getContext(), str);
                                            }
                                        }, true);

                                        break;
                                    case 1:

                                        DownloadPictureUtil.downloadPicture(view.getContext(), loadUrl, new DownloadPictureUtil.PicCallBack() {
                                            @Override
                                            public void onDownLoadSuccess(String str) {
                                                File imageFile = new File(str);
                                                if (imageFile == null || !imageFile.exists()) {
                                                    return;
                                                }
                                                EasyPhotos.notifyMedia(view.getContext(), imageFile.getAbsoluteFile());
                                                Result result = DecodeBitmap.scanningImage(imageFile.getPath());
                                                if (result == null) {

                                                } else {
                                                    QRRouter.handleQRCode(result.toString(), view.getContext());
//                mContext.finish();
                                                }
                                            }
                                        }, false);
                                        break;
                                    case 2:

                                        DownloadPictureUtil.downloadPicture(view.getContext(), loadUrl, new DownloadPictureUtil.PicCallBack() {
                                            @Override
                                            public void onDownLoadSuccess(String str) {

                                                File file = new File(str);

                                                if (file != null && file.exists()) {
                                                    EasyPhotos.notifyMedia(view.getContext(), file.getAbsoluteFile());
                                                    ShareUtil.shareImage(view.getContext(),file,"分享图片");
//                                                    ShareUtil.imageExternalShare(file,view.getContext());
                                                }
                                            }
                                        }, false);

//                                                File file = savePicture(context);

                                        break;
                                }
                            }

                        }).create().show();

                        return false;
                    }
                })
                // 页面切换回调
                .setBigImagePageChangeListener(new OnBigImagePageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        loadUrl = imageInfoList.get(position).getOriginUrl();
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


    public static class ImageBrowseOpenItem {
        private String localPath;
        private String mImageUrl;
        private String converserId;
        private String ofrom;
        private String oto;
        private int type;

        public String getLocalPath() {
            return localPath;
        }

        public void setLocalPath(String localPath) {
            this.localPath = localPath;
        }

        public String getmImageUrl() {
            return mImageUrl;
        }

        public void setmImageUrl(String mImageUrl) {
            this.mImageUrl = mImageUrl;
        }

        public String getConverserId() {
            return converserId;
        }

        public void setConverserId(String converserId) {
            this.converserId = converserId;
        }

        public String getOfrom() {
            return ofrom;
        }

        public void setOfrom(String ofrom) {
            this.ofrom = ofrom;
        }

        public String getOto() {
            return oto;
        }

        public void setOto(String oto) {
            this.oto = oto;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }


    private static File savePicture(Context context) {


//        File imageFile = null;
//        try {
//            imageFile = Glide.with(mContext)
////                    .load(new MyGlideUrl(loadUrl))
//                    .load(loadUrl)
//                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                    .get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//        if(imageFile!=null&&imageFile.exists())
//        {
//            byte[] bytes = FileUtils.toByteArray(imageFile, 4);//new byte[4];
//            ImageUtils.ImageType type = ImageUtils.adjustImageType(bytes);
//            if(type == ImageUtils.ImageType.GIF)
//            {
//                ImageUtils.saveToGallery(mContext,imageFile,true);
//            }
//            else {
//                ImageUtils.saveToGallery(mContext,imageFile,false);
//            }
//        }
//
//        return imageFile;
        return null;
    }




//    private static void externalShare(File file,Context context) {
//        Intent share_intent = new Intent();
//        share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
//        share_intent.setType("image/*");  //设置分享内容的类型
//        Uri uri;
//        if (Build.VERSION.SDK_INT >= 24) {
//            share_intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
////            uri = FileProvider.getUriForFile(mContext, ProviderUtil.getFileProviderName(mContext), file);//android 7.0以上
//            uri = getImageContentUri(context, file);
//        } else {
////            uri = Uri.fromFile(file);
//            uri = getImageContentUri(context, file);
//        }
//        share_intent.putExtra(Intent.EXTRA_STREAM, uri);
//        //创建分享的Dialog
//        share_intent = Intent.createChooser(share_intent, "分享");
//        context.startActivity(share_intent);
//    }
//
//
//    /**
//     * Gets the content:// URI from the given corresponding path to a file
//     *
//     * @param context
//     * @param imageFile
//     * @return content Uri
//     */
//    public static Uri getImageContentUri(Context context, java.io.File imageFile) {
//        String filePath = imageFile.getAbsolutePath();
//        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
//                new String[]{filePath}, null);
//        if (cursor != null && cursor.moveToFirst()) {
//            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
//            Uri baseUri = Uri.parse("content://media/external/images/media");
//            return Uri.withAppendedPath(baseUri, "" + id);
//        } else {
//            if (imageFile.exists()) {
//                ContentValues values = new ContentValues();
//                values.put(MediaStore.Images.Media.DATA, filePath);
//                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//            } else {
//                return null;
//            }
//        }
//    }
}
