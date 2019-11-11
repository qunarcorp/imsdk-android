package com.qunar.im.ui.view.baseView.processor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcelable;
import android.support.rastermill.FrameSequence;
import android.support.rastermill.FrameSequenceDrawable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.gson.reflect.TypeToken;
import com.qunar.im.base.util.ListUtil;
import com.qunar.im.ui.util.FacebookImageUtil;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.jsonbean.EncryptMsg;
import com.qunar.im.base.module.AtData;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.ui.util.EmotionUtils;
import com.qunar.im.base.util.Constants;
import com.qunar.im.ui.util.EmotionUtils;
import com.qunar.im.base.util.FileUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.MemoryCache;
import com.qunar.im.base.util.MessageUtils;
import com.qunar.im.base.util.Utils;
import com.qunar.im.base.util.glide.GlideRoundTransform;
import com.qunar.im.base.util.graphics.ImageUtils;
import com.qunar.im.base.view.faceGridView.EmoticonEntity;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.QunarWebActvity;
import com.qunar.im.ui.view.LinkMovementClickMethod;
import com.qunar.im.ui.view.LoadingImgView;
import com.qunar.im.ui.view.baseView.AnimatedGifDrawable;
import com.qunar.im.ui.view.baseView.AnimatedImageSpan;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.ViewPool;
import com.qunar.im.ui.view.bigimageview.ImageBrowsUtil;
import com.qunar.im.ui.view.bigimageview.view.MyGlideUrl;
import com.qunar.im.ui.view.bubbleLayout.BubbleLayout;
import com.qunar.im.ui.view.emojiconTextView.EmojiconTextView;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.QtalkStringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaokai on 15-8-14.
 * <p/>
 * 处理文本和图像
 */
public class TextMessageProcessor extends DefaultMessageProcessor {
    private int maxWidth;
    private int minWidth;
    private int textPadding;
    private int iconSize;
    private int defaultSize;

    public TextMessageProcessor() {
        maxWidth = (int) (Utils.getScreenWidth(QunarIMApp.getContext()) * 0.4);
        minWidth = (int) (maxWidth * 0.5);
        defaultSize = Utils.dipToPixels(QunarIMApp.getContext(), 96);
        iconSize = Utils.dpToPx(QunarIMApp.getContext(), 32);
        textPadding = Utils.dpToPx(QunarIMApp.getContext(), 8);
    }

    public static class GifListener implements AnimatedImageSpan.UpdateListener {
        WeakReference<TextView> target;

        public GifListener(WeakReference<TextView> weakReference) {
            target = weakReference;
        }

        @Override
        public void update() {
            TextView tv = target.get();
            if (tv.getTag(R.string.atom_ui_title_add_emotion) != null)
                tv.invalidate();
        }
    }

    @Override
    public void processBubbleView(BubbleLayout bubbleLayout, IMessageItem item) {
        //单个图片或者表情 不展示气泡
        List<Map<String, String>> list = ChatTextHelper.getObjList(item.getMessage().getBody());
        if(!ListUtil.isEmpty(list) && list.size() == 1
                && ("image".equals(list.get(0).get("type")) || "emoticon".equals(list.get(0).get("type")))){
            bubbleLayout.noBubbleStyle();
        }else if(item.getMessage().getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeImageNew_VALUE
                || item.getMessage().getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypePhoto_VALUE){
            bubbleLayout.noBubbleStyle();
        }else {
            bubbleLayout.resetBubble(item.getMessage().getDirection()==IMMessage.DIRECTION_RECV);
        }
    }

    @Override
    public void processChatView(ViewGroup parent, IMessageItem item) {
        final IMMessage message = item.getMessage();
        final Context context = item.getContext();
        if (message != null) {
            String realBody = null;
            if (message.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeEncrypt_VALUE) {//如果是加密消息 替换成真实的body体
                EncryptMsg encryptMsg = ChatTextHelper.getEncryptMessageBody(message);
                if (encryptMsg != null) realBody = encryptMsg.Content;
                if (TextUtils.isEmpty(realBody))
                    realBody = "【加密消息】";
            } else if(message.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeGroupAt_VALUE &&
                    message.getDirection() == IMMessage.DIRECTION_RECV){//@消息 展示人员备注
                realBody = handleAtMessage(message);
            } else if(message.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypePhoto_VALUE &&
                    message.getDirection() == IMMessage.DIRECTION_SEND){//处理自己本地发送的图片
                realBody = message.getBody();
                String ext = message.getExt();
                if(!TextUtils.isEmpty(ext)){
                    //判断本地图片是否存在
                    List<Map<String, String>> tempList = ChatTextHelper.getObjList(ext);
                    if(tempList != null && tempList.size() == 1){
                        String value = tempList.get(0).get("value");
                        if(value.startsWith("file://")){
                            String localPath = value.substring(7);
                            if(new File(localPath).exists()){//本地文件存在取ext 不存在取网络url
                                realBody = ext;
                            }
                        }
                    }
                }
            }else{
                realBody = message.getBody();
            }
            List<Map<String, String>> list = ChatTextHelper.getObjList(realBody);
            setTextOrImageView(list, parent, context, message);
        }
    }

    /**
     * 处理@消息 备注展示
     * @param message
     * @return
     */
    private String handleAtMessage(IMMessage message){
        String body = message.getBody();
        String ext = message.getExt();
        List<AtData.DataBean> list = null;
        if(!TextUtils.isEmpty(ext)){
            try{
                List<AtData> personList = JsonUtils.getGson().fromJson(ext, new TypeToken<List<AtData>>(){}.getType());
                for(AtData atPerson : personList){
                    if(atPerson.getType() == 10001){
                        list = atPerson.getData();
                        break;
                    }
                }
                if(list != null && !list.isEmpty()){
                    for (AtData.DataBean person : list){
                        String markupName = ConnectionUtil.getInstance().getMarkupNameById(person.getJid());
                        if(!TextUtils.isEmpty(markupName))
                            body = body.replaceAll("@" + person.getText(),"@" + markupName);
                    }
                }
            }catch (Exception e){

            }
        }
        return body;
    }

    protected void setTextOrImageView(List<Map<String, String>> list, ViewGroup parent, final Context context, IMMessage message) {
        //在每次有非textView生成需要加入parent的时候务必将newTextView置为true,主要解决图文混排的问题
        boolean newTextView = true;
        EmojiconTextView textView = null;
        SpannableStringBuilder sb = new SpannableStringBuilder();

        for (Map<String, String> map : list) {
            switch (map.get("type")) {
                case "image":
                    final String source = map.get("value");
                    final String extra = map.get("extra");
                    if (textView != null && sb.length() > 0) {
                        newTextView = true;
                        textView.setText(sb);
                        parent.addView(textView);
                        textView = null;
                        sb.clear();
                    }
                    int width = 0;
                    int height = 0;
                    if (extra != null && extra.contains("width") && extra.contains("height")) {
                        try{
                            String[] str = extra.trim().split("\\s+");
                            if (str.length > 1) {
                                //处理width = 240.000000　问题
                                width = Double.valueOf(str[0].substring(str[0].indexOf("width") + 6)).intValue();
                                height = Double.valueOf(str[1].substring(str[1].indexOf("height") + 7)).intValue();

                            }
                        }catch (Exception e){

                        }
                    }
                    final MessageUtils.ImageMsgParams params = new MessageUtils.ImageMsgParams();
                    params.sourceUrl = source;
                    params.height = height;
                    params.width = width;


                    MessageUtils.getDownloadFile(params, context, false);

//                    MessageUtils.initImageUrl(params,context,false);
                    //fresco
//                    final SimpleDraweeView draweeView = getSimpleDraweeView(context,
//                            params.origin ? params.sourceUrl : params.smallUrl, params.smallUrl,
//                            params.width, params.height, message.getDirection());
                    //glide
                    final LoadingImgView loadingImgView = getLoadingImgView(context, params.width, params.height,
                             params.thumbUrl, message.getDirection());

                    loadingImgView.setPer(0);
                    final ImageBrowsUtil.ImageBrowseOpenItem openItem = new ImageBrowsUtil.ImageBrowseOpenItem();
//                    final Intent intent = new Intent(context, ImageBrowersingActivity.class);
                    openItem.setLocalPath(params.savedFilePath.getPath());
                    openItem.setmImageUrl(params.sourceUrl);
                    openItem.setType(ImageBrowsUtil.converser);
//                    intent.putExtra(Constants.BundleKey.IMAGE_ON_LOADING, params.savedFilePath.getPath());
//                    intent.putExtra(Constants.BundleKey.IMAGE_URL, params.sourceUrl);
                    //加密的图片消息 特殊处理 点击只显示一张
                    if (message.getMsgType() != ProtoMessageOuterClass.MessageType.MessageTypeEncrypt_VALUE) {
//                        intent.putExtra(Constants.BundleKey.CONVERSATION_ID, message.getConversationID());
                        openItem.setConverserId(message.getConversationID());
                    }
                    if(!TextUtils.isEmpty(message.getoFromId())&&!TextUtils.isEmpty(message.getoToId())){
                        openItem.setOfrom(message.getoFromId());
                        openItem.setOto(message.getoToId());
//                        intent.putExtra(Constants.BundleKey.ORIGIN_FROM,message.getoFromId());
//                        intent.putExtra(Constants.BundleKey.ORIGIN_TO,message.getoToId());
                    }
                    if (list.size() == 1) {
                        parent.setTag(R.id.imageview, params.savedFilePath.getPath());
                    } else {
                        parent.setTag(R.id.imageview, null);
                    }
                    loadingImgView/*draweeView*/.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ImageBrowsUtil.openImageBrowse(openItem,context);
//                            int location[] = new int[2];
//                            v.getLocationOnScreen(location);
//                            intent.putExtra("left", location[0]);
//                            intent.putExtra("top", location[1]);
//                            intent.putExtra("height", params.height);
//                            intent.putExtra("width", params.width);
//                            context.startActivity(intent);
//                            ((Activity)context).overridePendingTransition(0, 0);
                        }
                    });
//                    draweeView
//

                    if(MessageStatus.isProcession(message.getMessageState())){
                        loadingImgView.setPer(message.getProgress());
                    } else {
                        loadingImgView.finish();
                    }
                    parent.addView(loadingImgView);
//                    parent.addView(draweeView);
                    break;
                case "emoticon":
                    if (newTextView) {
                        newTextView = false;
                        textView = ViewPool.getView(EmojiconTextView.class, context);
                        textView.setTag(R.string.atom_ui_title_add_emotion, null);
                    }
                    String value = map.get("value");

                    if (TextUtils.isEmpty(value)) {
                        break;
                    }
                    String ext = map.get("extra");
                    String pkgId = "";
                    if (ext != null && ext.contains("width")) {
                        String[] str = ext.trim().split("\\s+");
                        if (str.length > 1) {
                            //处理width = 240.000000　问题
                            pkgId = str[0].substring(str[0].indexOf("width") + 6);
                        }
                    }
                    String shortcut = value.substring(1, value.length() - 1);
                    EmoticonEntity emotionEntry = EmotionUtils.getEmoticionByShortCut(shortcut, pkgId, true);
                    if (emotionEntry != null) {
                        String path = emotionEntry.fileOrg;
                        if (!TextUtils.isEmpty(path)) {
                            Parcelable cached = MemoryCache.getMemoryCache(path);
                            if (cached == null) {
                                InputStream is = null;
                                int imgSize = emotionEntry.showAll?iconSize:defaultSize;
                                try {
                                    if (path.startsWith("emoticons")||path.startsWith("Big_Emoticons")) {
                                        is = context.getAssets().open(path);
                                    } else {
                                        is = new FileInputStream(path);
                                    }
                                    ImageUtils.ImageType type = ImageUtils.adjustImageType(
                                            FileUtils.toByteArray(new File(path), 4));
                                    if (emotionEntry.fileFiexd.endsWith(".gif")) {
                                        type = ImageUtils.ImageType.GIF;
                                    }

                                    if (type == ImageUtils.ImageType.GIF) {
                                        cached = new AnimatedGifDrawable(is, imgSize);
                                    } else {
                                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                                        Matrix matrix = new Matrix();
                                        matrix.postScale(imgSize / bitmap.getWidth(),
                                                imgSize / bitmap.getHeight());
                                        cached = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                                                bitmap.getHeight(), matrix, true);
                                        if (cached != bitmap) {
                                            bitmap.recycle();
                                        }
                                    }
                                    MemoryCache.addObjToMemoryCache(path, cached);
                                } catch (IOException e) {
                                    LogUtil.e(TAG, "ERROR", e);
                                } finally {
                                    if (is != null) {
                                        try {
                                            is.close();
                                        } catch (IOException e) {
                                            LogUtil.e(TAG, "ERROR", e);
                                        }
                                    }
                                }
                            }
                            if (cached != null) {
                                DynamicDrawableSpan span;
                                if (cached instanceof AnimatedGifDrawable) {
                                    WeakReference<TextView> weakReference = new WeakReference<TextView>(textView);
                                    span = new AnimatedImageSpan((Drawable) cached, weakReference);
                                    if (textView.getTag(R.string.atom_ui_title_add_emotion) == null) {
                                        ((AnimatedImageSpan) span).setListener(new GifListener(weakReference));
                                        textView.setTag(R.string.atom_ui_title_add_emotion, 1);
                                    }
                                } else {
                                    span = new ImageSpan(context, (Bitmap) cached);
                                }
                                SpannableString spannableString = new SpannableString(shortcut);
                                spannableString.setSpan(span, 0, spannableString.length(),
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                sb.append(spannableString);
                            }
                        } else if (path != null) {
                            //牛驼表情和其他非默认表情
                            //每当新创建ImageView的时候都要将当前的TextView放入parent
                            if (textView != null && sb.length() > 0) {
                                newTextView = true;
                                textView.setText(sb);
                                parent.addView(textView);
                                textView = null;
                                sb.clear();
                            }
                            if (path.startsWith("Big_Emoticons/")) {//内置大图逻辑
                                String p = "file:///android_asset/" + path;
                                LoadingImgView bigEmoticons = getLoadingImgView(context, 256, 256, p, message.getDirection());
                                bigEmoticons.finish();
                                parent.addView(bigEmoticons);
                            } else {
                                SimpleDraweeView emojiView = getSimpleDraweeView(new File(path), context);
                                //牛驼表情的特殊处理,240 * 240px
                                emojiView.setLayoutParams(new LinearLayout.LayoutParams(256, 256));
                                emojiView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                parent.addView(emojiView);
                            }
                        } else {
                            sb.append(value);
                        }
                    } else {
                        SimpleDraweeView emojiView;
                        if (TextUtils.isEmpty(pkgId)) {
                            emojiView = getSimpleDraweeView(context,
                                    QtalkStringUtils.addFilePathDomain("/file/v2/emo/d/oe/"
                                            + shortcut
                                            + "/org", true), null, 128, 128, -1);
                        } else {
                            emojiView = getSimpleDraweeView(context,
                                    QtalkStringUtils.addFilePathDomain("/file/v2/emo/d/e/"
                                            + pkgId
                                            + "/"
                                            + shortcut + "/org", true), null, 128, 128, -1);
                        }
                        emojiView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        parent.addView(emojiView);
                    }
                    break;
                case "url":
                    if (newTextView) {
                        newTextView = false;
                        textView = ViewPool.getView(EmojiconTextView.class, context);
                    }
                    String url = map.get("value");
                    URLSpan span = new URLSpan(url) {
                        @Override
                        public void onClick(View widget) {

                            View v = (View) widget.getParent();
                            if (v.getTag(R.string.atom_ui_voice_hold_to_talk) != null) {
                                v.setTag(R.string.atom_ui_voice_hold_to_talk, null);
                                return;
                            }
                            if (widget instanceof EmojiconTextView) {
                                String url = getURL();
                                Intent intent = new Intent(context, QunarWebActvity.class);
                                intent.setData(Uri.parse(url));
                                context.startActivity(intent);
                            }
                        }
                    };
                    SpannableString spannableString = new SpannableString(url);
                    ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.parseColor("#48A3FF"));
                    spannableString.setSpan(span, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(redSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    sb.append(spannableString);
                    break;
                case "text":
                    String v = map.get("value");
                    if (TextUtils.isEmpty(v.trim())) {
                        break;
                    }
                    if (newTextView) {
                        newTextView = false;
                        textView = ViewPool.getView(EmojiconTextView.class, context);
                    }
                    if (v.length() > 1024) {
                        sb.append(v);
                    } else {
                        SpannableString textSpannable = new SpannableString(v);
                        Linkify.addLinks(textSpannable, Linkify.WEB_URLS |
                                Linkify.EMAIL_ADDRESSES | Linkify.PHONE_NUMBERS);
                        sb.append(textSpannable);
                        textView.setMovementMethod(LinkMovementClickMethod.getInstance());//长按事件与Spannable点击冲突
                    }
                    break;
            }
        }
        if (textView != null && sb.length() > 0) {
            textView.setText(sb);
            textView.setPadding(textPadding,textPadding,textPadding,textPadding);
            textView.setLineSpacing(2.0f,1.2f);
//            textView.setTextColor(message.getDirection() == IMMessage.DIRECTION_RECV ? Color.parseColor("#333333") : Color.parseColor("#555555"));
            parent.addView(textView);
        }

    }

    public SimpleDraweeView getSimpleDraweeView(File file, Context context) {
        SimpleDraweeView simpleDraweeView = ViewPool.getView(SimpleDraweeView.class, context);
//        simpleDraweeView.setMinimumHeight(300);
//        simpleDraweeView.setMinimumWidth(300);
        simpleDraweeView.setAspectRatio(1.0f);//宽高缩放比
        FacebookImageUtil.loadLocalImage(file, simpleDraweeView, 0, 0, true, FacebookImageUtil.EMPTY_CALLBACK);
        return simpleDraweeView;
    }

    public SimpleDraweeView getSimpleDraweeView(Context context, final String url, String smallUrl, int w, int h, int d) {
        //&w=128&h=128
        SimpleDraweeView simpleDraweeView = ViewPool.getView(SimpleDraweeView.class, context);

        float ration = w / h;
        simpleDraweeView.setAspectRatio(ration);
        simpleDraweeView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        PipelineDraweeControllerBuilder requestBuilder = Fresco.newDraweeControllerBuilder();

        if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(smallUrl) && !url.equals(smallUrl)) {
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(smallUrl))
                    .setProgressiveRenderingEnabled(true)
                    .setResizeOptions(new ResizeOptions(w, h/*Math.max(tempW, minWidth), (Math.max(tempH, minWidth))*/))
                    .build();
            requestBuilder.setLowResImageRequest(request);
        } else {
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                    .setProgressiveRenderingEnabled(true)
                    .setResizeOptions(new ResizeOptions(w, h/*Math.max(tempW, minWidth), (Math.max(tempH, minWidth))*/))
                    .build();
            requestBuilder.setImageRequest(/*ImageRequest.fromUri(url)*/request);
        }

        DraweeController controller = requestBuilder
                .setTapToRetryEnabled(true)
                .setOldController(simpleDraweeView.getController())
                .setAutoPlayAnimations(true)
//                .setControllerListener(listener)
                .build();
        simpleDraweeView.setController(controller);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(w, h/*Math.max(tempW, minWidth), (Math.max(tempH, minWidth))*/);
        if (d == 0) {
            lp.leftMargin = Utils.dpToPx(context, 6);
        } else if (d == 1) {
            lp.rightMargin = Utils.dpToPx(context, 6);
        }

//        lp.leftMargin
        simpleDraweeView.setLayoutParams(lp);
        return simpleDraweeView;
    }

    public LoadingImgView getLoadingImgView(final Context context, int w, int h, final String url, final int d) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(w, h);

        LoadingImgView mLoadingImgView = ViewPool.getView(LoadingImgView.class, context);
        mLoadingImgView.setLayoutParams(lp);

        if (Utils.isGifUrl(url)) {
            Glide.with(context)
                    .load(url.startsWith("http")?new MyGlideUrl(url):url)
                    .asGif()
                    .toBytes()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)//缓存全尺寸
                    .dontAnimate()
                    .into(new ViewTarget<LoadingImgView, byte[]>(mLoadingImgView) {
                        @Override
                        public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                            WeakReference<Parcelable> cached = new WeakReference<>(MemoryCache.getMemoryCache(url));
                            if(cached.get() == null){
                                FrameSequence fs = FrameSequence.decodeByteArray(resource);
                                if(fs != null){
                                    FrameSequenceDrawable drawable = new FrameSequenceDrawable(fs);
                                    drawable.setByteCount(resource.length);
                                    view.setImageDrawable(drawable);
                                    MemoryCache.addObjToMemoryCache(url,drawable);
                                }
                            }else {
                                if(cached.get() instanceof FrameSequenceDrawable){
                                    FrameSequenceDrawable fsd = (FrameSequenceDrawable)cached.get();
                                    view.setImageDrawable(fsd);
                                }

                            }

                        }

                    });
        } else {
            //glide 3+
            Glide.with(context)                             //配置上下文
                    .load(url.startsWith("http")?new MyGlideUrl(url):url)      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .centerCrop()
                    .error(R.drawable.atom_ui_ic_default_image)           //设置错误图片
                    .placeholder(R.drawable.atom_ui_ic_default_image)     //设置占位图片
//                    .thumbnail(Glide.with(context).load(smallUrl))
                    .transform(new CenterCrop(context), new GlideRoundTransform(context))
                    .override(w, h)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)//缓存全尺寸
                    .dontAnimate()
                    .into(mLoadingImgView);
        }
        return mLoadingImgView;
    }

}
