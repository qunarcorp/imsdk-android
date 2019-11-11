package com.qunar.im.ui.view.baseView.processor;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.facebook.drawee.interfaces.DraweeController;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.jsonbean.EncryptMsg;
import com.qunar.im.base.jsonbean.VideoMessageResult;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.Utils;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.ui.R;
import com.qunar.im.ui.util.videoPlayUtil.VideoPlayUtil;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.VideoImageView;
import com.qunar.im.ui.view.bigimageview.view.MyGlideUrl;
import com.qunar.im.ui.view.bubbleLayout.BubbleLayout;
import com.qunar.im.utils.QtalkStringUtils;

import java.io.File;


/**
 * Created by xingchao.song on 8/18/2015.
 */
public class VideoMessageProcessor extends DefaultMessageProcessor {

    View.OnClickListener videoClickListener;

    @Override
    public void processChatView(ViewGroup parent, IMessageItem item) {
        final IMMessage message = item.getMessage();
        final Context context = item.getContext();
        if (videoClickListener == null) {
            videoClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    VideoMessageResult result = (VideoMessageResult) v.getTag(R.integer.atom_ui_videotag);
                    if (result == null || TextUtils.isEmpty(result.FileUrl)) {
                        return;
                    }
                    String url = QtalkStringUtils.addFilePathDomain(result.FileUrl, false);
                    String thumb = QtalkStringUtils.addFilePathDomain(result.ThumbUrl, false);
                    String fileSize = result.FileSize;
                    String localPath = result.LocalVideoOutPath;

                    boolean onlyDownLoad = false;
                    if(result.newVideo){
                        onlyDownLoad = false;
                    }else{
                        onlyDownLoad = true;
                    }
                    if(!TextUtils.isEmpty(localPath)&&new File(localPath).exists()){
                        VideoPlayUtil.conAndWwOpen((FragmentActivity) context, localPath, result.FileName, thumb, url, onlyDownLoad, fileSize);
                    }else{
                        VideoPlayUtil.conAndWwOpen((FragmentActivity) context, url, result.FileName, thumb, url, onlyDownLoad, fileSize);
                    }


//                    if (result.newVideo) {
//
//                        VideoPlayUtil.conAndWwOpen((FragmentActivity) context, url, result.FileName, thumb, url, false);
//                    } else {
//                        VideoPlayUtil.conAndWwOpen((FragmentActivity) context, url, result.FileName, thumb, url, true);
////                        Intent intent = new Intent();
////                        intent.setClass(context, VideoPlayerActivity.class);
////                        intent.setData(Uri.parse(url));
////                        intent.putExtra("FileSize", result.FileSize);
////                        intent.putExtra(Constants.BundleKey.FILE_NAME, result.FileName);
////                        context.startActivity(intent);
//                    }

//
                }
            };
        }
//        VideoImageView videoIcon = ViewPool.getView(VideoImageView.class, context);
        VideoImageView videoIcon = new VideoImageView(context);

        videoIcon.setOnClickListener(videoClickListener);
        //videoIcon.setBackgroundResource(R.drawable.atom_ui_bg_video);
        //设置video的第一帧当图像
        VideoMessageResult result = null;
        if (message.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeEncrypt_VALUE) {
            EncryptMsg encryptMsg = ChatTextHelper.getEncryptMessageBody(message);
            if (encryptMsg != null) {
                result = JsonUtils.getGson().fromJson(encryptMsg.Content, VideoMessageResult.class);
            }
        } else {
            try {
                result = JsonUtils.getGson().fromJson(message.getExt(), VideoMessageResult.class);
                if (result == null) {
                    result = JsonUtils.getGson().fromJson(message.getBody(), VideoMessageResult.class);
                }
            } catch (Exception e) {
                result = JsonUtils.getGson().fromJson(message.getBody(), VideoMessageResult.class);
            }
        }
        if (result == null) return;
        Uri firstFrameUri;

        if (TextUtils.isEmpty(result.ThumbUrl)) {
            firstFrameUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.drawable.atom_ui_ic_video_play);
        } else {
            firstFrameUri = Uri.parse(QtalkStringUtils.addFilePathDomain(result.ThumbUrl, true));
        }
//        if(result.newVideo){
        videoIcon.setVideoInfo("", result.Duration);
//        }else {
//            videoIcon.setVideoInfo("", result.Duration);
//        }
        int h = 0;
        int w = 0;
        try {
            h = Integer.parseInt(result.Height);
            w = Integer.parseInt(result.Width);
        } catch (Exception e) {
            LogUtil.e(TAG, "ERROR", e);
        }


//        if (w == 0) {
//            w = Utils.dipToPixels(context, 144);
//        }
//        if (h == 0) {
//            h = w * 16 / 9;
//        }
        int defaultSize = Utils.dipToPixels(QunarIMApp.getContext(), 144);
        int maxWidth = (int) (Utils.getScreenWidth(QunarIMApp.getContext()) * 0.45);
        int heightP = h / (gcd(h, w));
        int widthP = w / (gcd(h, w));
        int maxHeight = maxWidth * heightP / widthP;

        int ratio = 0;
        int newH=0;
        int newW=0;
        if (w > maxWidth) {
            ratio = w/maxWidth;
            newW = maxWidth;
            newH = h/ratio;
            if(newH>maxHeight){
                int i = newH/maxHeight;
                newH = maxHeight;
                newW = newW/i;
            }
            h=newH;
            w=newW;
        }else{
            if(h>maxHeight){
                ratio = h/maxHeight;
                h=maxHeight;
                w=w/ratio;
            }else{
                h=h;
                w=w;
            }
        }


//        if (h * w > maxHeight * maxWidth) {
//            int k = (int) Math.ceil(Math.sqrt((h * w) / (maxHeight * maxWidth)));
//            if (k == 0) k = 2;
//            w = w / k;
//            h = h / k;
//
//
////            int k = (int) Math.ceil(Math.sqrt((h * w) / (maxHeight * maxWidth)));
////            if (k == 0) k = 2;
////            w = w / k;
////            h = h / k;
//        } else {
//            int k = (int) Math.ceil(Math.sqrt((defaultSize * defaultSize) / (h * w)));
//            if (k == 0) k = 1;
//            w = k * w;
//            h = k * h;
//        }
//        videoIcon.setMinimumHeight(h);
//        videoIcon.setMinimumWidth(w);
//        float ration = w / h;
//        videoIcon.setAspectRatio(ration);
        videoIcon.setScaleType(ImageView.ScaleType.FIT_XY);
        result.ThumbUrl = QtalkStringUtils.addFilePathDomain(result.ThumbUrl, true);
        if (result.ThumbUrl.startsWith("http")) {
            Glide.with(context).load(new MyGlideUrl(result.ThumbUrl)).error(R.drawable.atom_ui_sharemore_picture).into(videoIcon);
        } else {
            Glide.with(context).load(result.ThumbUrl).error(R.drawable.atom_ui_sharemore_picture).into(videoIcon);
        }

        DraweeController controller;
//        if(result.ThumbUrl.startsWith("/storage")){
//            controller = Fresco.newDraweeControllerBuilder()
//                    .setUri(result.ThumbUrl)
//                    .setAutoPlayAnimations(false)
//                    .setOldController(videoIcon.getController())
//                    .build();
//        }else{
//            controller = Fresco.newDraweeControllerBuilder()
//                    .setUri(firstFrameUri)
//                    .setAutoPlayAnimations(false)
//                    .setOldController(videoIcon.getController())
//                    .build();
//        }

//        videoIcon.setController(controller);
        videoIcon.setLayoutParams(new LinearLayout.LayoutParams(w, h));
        parent.addView(videoIcon);
        videoIcon.setTag(R.integer.atom_ui_videotag, result);
    }


    @Override
    public void processBubbleView(BubbleLayout bubbleLayout, IMessageItem item) {
//        bubbleLayout.setBubbleColor(ContextCompat.getColor(item.getContext(), R.color.atom_ui_white));
        bubbleLayout.noBubbleStyle();
    }

    /*** 求最大公约数 ***/
    public int gcd(int a, int b) {
        if ((a % b) != 0)
            return gcd(b, a % b);
        else
            return b;
    }
}
