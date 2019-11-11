package com.qunar.im.ui.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.target.Target;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.qunar.im.base.callbacks.BasicCallback;
import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.common.ICommentView;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.jsonbean.GetVCardData;
import com.qunar.im.base.jsonbean.GetVCardResult;
import com.qunar.im.base.module.ChatRoom;
import com.qunar.im.base.module.DepartmentItem;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.module.UserVCard;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.protocol.VCardAPI;
import com.qunar.im.base.util.BusinessUtils;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.FileUtils;
import com.qunar.im.base.util.InternDatas;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.ListUtil;
import com.qunar.im.base.util.glide.GlideCircleTransform;
import com.qunar.im.base.util.graphics.BitmapHelper;
import com.qunar.im.base.util.graphics.ImageUtils;
import com.qunar.im.base.util.graphics.MyDiskCache;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.view.bigimageview.view.MyGlideUrl;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.QtalkStringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import de.greenrobot.event.EventBus;

import static com.qunar.im.base.common.QunarIMApp.getContext;

/**
 * Created by xingchao.song on 9/7/2015.
 */
public final class ProfileUtils {

    private static int defaultRes;
    private static int workworldDefault;

    public static int getDefaultRes() {
        return defaultRes;
    }

    public static void setDefaultRes(int defaultRes) {
        ProfileUtils.defaultRes = defaultRes;
    }

    public static void setWorkworldDefault(int defaultRes) {
        ProfileUtils.workworldDefault = defaultRes;
    }




    public static void displayGravatarByFullname(final String fullName, final SimpleDraweeView headView) {
    }

    public static String getGravatarUrl(String jid)
    {
        if(TextUtils.isEmpty(jid)) return jid;
        String imageUrl = InternDatas.JidToUrl.get(jid);
        if(TextUtils.isEmpty(imageUrl))
        {
            UserVCard vCard = getLocalVCard(jid);
            if(!TextUtils.isEmpty(vCard.gravantarUrl))
            {
                imageUrl = QtalkStringUtils.getGravatar(vCard.gravantarUrl,true);
                InternDatas.JidToUrl.put(jid,imageUrl);
            }
        }
        return imageUrl;
    }


    public static void displayEmojiconByImageSrc(final Activity context, final String imageSrc, final ImageView headView, final int w, final int h){

        if(context == null){
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if(context.isDestroyed()){
                return;
            }
        }
        if(TextUtils.isEmpty(imageSrc) || headView == null){
            return;
        }
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //glide
                Glide.with(context)
                        //配置上下文
                        .load(imageSrc)
//                        .load(new MyGlideUrl(imageSrc))      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                        .asBitmap()
//                        .error(defaultRes)
                        .centerCrop()
                        .thumbnail(0.1f)
                        .transform(new CenterCrop(context))
                        .placeholder(defaultRes)
                        .override(w > 0 ? w + 5 : Target.SIZE_ORIGINAL, h > 0 ? h + 5 : Target.SIZE_ORIGINAL)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)//缓存全尺寸
                        .dontAnimate()
                        .into(headView);
            }
        });



    }

    public static void displayGravatarByImageSrc(final Activity context, final String imageSrc, final ImageView headView, final int w, final int h){


        if(context == null){
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if(context.isDestroyed()){
                return;
            }
        }
        if(TextUtils.isEmpty(imageSrc) || headView == null){
            return;
        }
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //glide
                Glide.with(context)
//                        .load(imageSrc)//配置上下文
                        .load(new MyGlideUrl(imageSrc))      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                        .asBitmap()
//                        .error(defaultRes)
                        .centerCrop()
                        .thumbnail(0.1f)
                        .transform(new CenterCrop(context)
                                ,new GlideCircleTransform(context))
                        .placeholder(defaultRes)
                        .override(w > 0 ? w + 5 : Target.SIZE_ORIGINAL, h > 0 ? h + 5 : Target.SIZE_ORIGINAL)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)//缓存全尺寸
                        .dontAnimate()
                        .into(headView);
            }
        });



    }

    public static void displayMedalSmallByImageSrc(final Activity context, final String imageSrc, final ImageView headView, final int w, final int h){


        if(context == null){
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if(context.isDestroyed()){
                return;
            }
        }
        if(TextUtils.isEmpty(imageSrc) || headView == null){
            return;
        }
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //glide
                Glide.with(context)
//                        .load(imageSrc)//配置上下文
                        .load(new MyGlideUrl(imageSrc))      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                        .asBitmap()
//                        .error(defaultRes)
                        .centerCrop()
                        .thumbnail(0.1f)
                        .transform(new CenterCrop(context)
                                ,new GlideCircleTransform(context))
                        .placeholder(defaultRes)
                        .override(w > 0 ? w + 5 : Target.SIZE_ORIGINAL, h > 0 ? h + 5 : Target.SIZE_ORIGINAL)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)//缓存全尺寸
                        .dontAnimate()
                        .into(headView);
            }
        });



    }

    public static void displaySquareByImageSrc(final Activity context, final String imageSrc, final ImageView headView, final int w, final int h){


        if(context == null){
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if(context.isDestroyed()){
                return;
            }
        }
        if(TextUtils.isEmpty(imageSrc) || headView == null){
            return;
        }
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //glide
                Glide.with(context)
//                        .load(imageSrc)//配置上下文
                        .load(new MyGlideUrl(imageSrc))      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                        .asBitmap()
//                        .error(defaultRes)
                        .centerCrop()
                        .thumbnail(0.1f)
                        .transform(new CenterCrop(context)
                                )
                        .placeholder(workworldDefault)
                        .override(w > 0 ? w + 5 : Target.SIZE_ORIGINAL, h > 0 ? h + 5 : Target.SIZE_ORIGINAL)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)//缓存全尺寸
                        .dontAnimate()
                        .into(headView);
            }
        });



    }


    public static void displayLinkImgByImageSrc(final Activity context, final String imageSrc, final Drawable error, final ImageView headView, final int w, final int h){


        if(context == null){
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if(context.isDestroyed()){
                return;
            }
        }
        if(TextUtils.isEmpty(imageSrc) || headView == null){
            return;
        }
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //glide
                Glide.with(context)
//                        .load(imageSrc)//配置上下文
                        .load(new MyGlideUrl(imageSrc))      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                        .asBitmap()
//                        .error(defaultRes)
                        .centerCrop()
                        .thumbnail(0.1f)
                        .transform(new CenterCrop(context)
                        )
                        .placeholder(error)
                        .error(error)
                        .override(w > 0 ? w + 5 : Target.SIZE_ORIGINAL, h > 0 ? h + 5 : Target.SIZE_ORIGINAL)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)//缓存全尺寸
                        .dontAnimate()
                        .into(headView);
            }
        });



    }


    public static void displayGravatarByImageSrc(String jid,String imageSrc,final SimpleDraweeView headView){
        if (TextUtils.isEmpty(imageSrc)) {
            headView.setTag(jid);
            FacebookImageUtil.loadFromResource(CommonConfig.DEFAULT_GRAVATAR, headView);
//            loadVCard4mNet(jid, headView, null);
        } else {
            headView.setTag(jid);
            FacebookImageUtil.loadWithCache(imageSrc, headView, ImageRequest.CacheChoice.SMALL,
                    CurrentPreference.getInstance().isSupportGifGravantar());
        }
    }

    public static void displayGravatarByUserId(String jid, final SimpleDraweeView headView) {
        ConnectionUtil.getInstance().getUserCard(jid, new IMLogicManager.NickCallBack() {
            @Override
            public void onNickCallBack(Nick nick) {
                if (nick != null && !TextUtils.isEmpty(nick.getHeaderSrc())) {
                    displayGravatarByImageSrc((Activity) headView.getContext(), nick.getHeaderSrc(), headView, 0, 0);
                }
            }
        },false,false);

    }

    public static void setCommentUrl(ICommentView commentView, String url, String userId) {
        if (commentView != null && !TextUtils.isEmpty(url) && !TextUtils.isEmpty(userId)) {
            commentView.setCommentUrl(url, userId);
        }
    }


    public static List<GetVCardData> genarateGetData(UserVCard vCard) {
        GetVCardData vCardData = new GetVCardData();
        vCardData.domain = QtalkStringUtils.parseDomain(vCard.id);
        vCardData.users = new ArrayList<>();
        GetVCardData.UserVCardInfo userVCardInfo = new GetVCardData.UserVCardInfo();
        vCardData.users.add(userVCardInfo);
        int version = vCard.gravantarVersion;

        userVCardInfo.version = String.valueOf(version);
        userVCardInfo.user = QtalkStringUtils.parseLocalpart(vCard.id);
        List<GetVCardData> list = new ArrayList<GetVCardData>(1);
        list.add(vCardData);
        return list;
    }

    public static void loadVCard4mNet(final SimpleDraweeView headView,String jid, final ICommentView view, final boolean showDefault)
    {
        loadVCard4mNet(headView, jid, view, showDefault, null);
    }

    public static void loadVCard4mNet(final SimpleDraweeView headView,String jid, final ICommentView view, final boolean showDefault,
                                      final BasicCallback<UserVCard> cardCallback)
    {
        if(jid==null) {
            cardCallback.onError();
            return;
        }
        if(!jid.contains("@")) jid = getJid(jid);
        final UserVCard vCard = ProfileUtils.getLocalVCard(jid);
        if (view != null)
            setCommentUrl(view, vCard.commentUrl, QtalkStringUtils.parseLocalpart(jid));
        List<GetVCardData> datas = genarateGetData(vCard);
        final String finalJid = jid;
        if(headView!=null)headView.setTag(vCard.id);
        VCardAPI.getVCardInfo(datas, new ProtocolCallback.UnitCallback<GetVCardResult>() {
            @Override
            public void onCompleted(GetVCardResult vCardResult) {
                if (vCardResult != null && !ListUtil.isEmpty(vCardResult.data)) {
                    final GetVCardResult.VCardGroup group = vCardResult.data.get(0);
                    if (ListUtil.isEmpty(group.users)) return;
                    final GetVCardResult.VCardInfoN info = group.users.get(0);
                    UserVCard card = tranformUserToVCard(info, group.domain);
                    if(cardCallback!=null)
                    {
                        cardCallback.onSuccess(card);
                    }
                    Object tag = headView==null?null:headView.getTag();
                    if (view != null)
                        setCommentUrl(view, info.commenturl, QtalkStringUtils.parseLocalpart(finalJid));
                    if (!TextUtils.isEmpty(info.imageurl)&&tag!=null&&
                            tag.equals(vCard.id)) {
                        if (!TextUtils.isEmpty(vCard.gravantarUrl)) {
                            FacebookImageUtil.evictCache(QtalkStringUtils.getGravatar(vCard.gravantarUrl, true));
                        }
                        final String imageString = QtalkStringUtils.getGravatar(info.imageurl, true);
                        FacebookImageUtil.loadWithCache(imageString,
                                headView, false, CurrentPreference.getInstance().isSupportGifGravantar(),
                                ImageRequest.CacheChoice.SMALL,
                                new FacebookImageUtil.ImageLoadCallback() {
                                    @Override
                                    public void onSuccess() {
                                        InternDatas.JidToUrl.put(finalJid, imageString);
                                        EventBus.getDefault().post(
                                                new EventBusEvent.GravtarGot(imageString, finalJid));
                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });

                    }
                }
            }

            @Override
            public void onFailure(String errMsg) {
                Object tag = headView==null?null:headView.getTag();
                if (showDefault&&tag!=null&&
                        tag.equals(vCard.id)) {
                    FacebookImageUtil.loadFromResource(CommonConfig.DEFAULT_GRAVATAR, headView);
                }
                if(cardCallback!=null)
                {
                    cardCallback.onError();
                }
            }
        });
    }

    public static UserVCard tranformUserToVCard(GetVCardResult.VCardInfoN user,
                                                String domain) {
        if (user == null) return null;
        UserVCard vCard = new UserVCard();
        vCard.id = TextUtils.isEmpty(user.loginName)?user.username+"@"+domain:
                user.loginName+"@"+domain;
        vCard.email = user.email;
        vCard.telphone = user.mobile;
        vCard.gender = TextUtils.isEmpty(vCard.toString())?"-1":
                String.valueOf(user.gender);
        if (!TextUtils.isEmpty(user.nickname)) {
            vCard.nickname = user.nickname;
        } else if (!TextUtils.isEmpty(user.webname)) {
            vCard.nickname = user.webname;
        } else {
            vCard.nickname = user.loginName;
        }
        vCard.gravantarVersion = TextUtils.isEmpty(user.V)?0:
                Integer.parseInt(user.V);
        vCard.commentUrl = user.commenturl;
        vCard.gravantarUrl = user.imageurl;
        vCard.type = user.type;
        if(user.extentInfo!=null)
        {
            vCard.extension = JsonUtils.getGson().toJson(user.extentInfo);
        }
        return vCard;
    }


    public static void loadNickName(final String uidOrJid, final TextView textView, final boolean bforce) {
        if (textView != null) textView.setTag(uidOrJid);
        ConnectionUtil.getInstance().getUserCard(uidOrJid, new IMLogicManager.NickCallBack() {
            @Override
            public void onNickCallBack(final Nick nick) {
                CommonConfig.mainhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (nick != null && !TextUtils.isEmpty(nick.getName())) {
                            textView.setText(nick.getName());
                        } else {
                            textView.setText(uidOrJid);
                        }
                    }
                });

            }
        },false,false);
    }

    public static String getJid(final String uidOrJid)
    {
        if(uidOrJid==null) return "";
        if(uidOrJid.contains("@")&&!uidOrJid.contains("@conference"))
        {
            return uidOrJid;
        }
        final String key = QtalkStringUtils.parseResource(uidOrJid);
        String jid = InternDatas.getJid(key);
        if(TextUtils.isEmpty(jid))
        {
            if(uidOrJid.contains("/")) {
                return  QtalkStringUtils.userId2Jid(key);
            }
            else {
                jid = QtalkStringUtils.userId2Jid(key);
            }
        }
        return jid;
    }
    public static void loadNickName(final String uidOrJid, boolean bforce, final LoadNickNameCallback callback) {
        final String jid = getJid(uidOrJid);
        if(TextUtils.isEmpty(jid)) {
            callback.finish(QtalkStringUtils.parseResource(uidOrJid));
            return;
        }
        String name = null;
        if (CommonConfig.isQtalk &&
                BusinessUtils.checkChiness(uidOrJid)) {
            callback.finish(uidOrJid);
            return;
        }

        if (CommonConfig.isQtalk) {
            Object val = InternDatas.getData(Constants.SYS.CONTACTS_MAP);
            if (val != null) {
                Map<String, DepartmentItem>
                        map = (Map<String, DepartmentItem>) val;
                DepartmentItem item = map.get(QtalkStringUtils.userId2Jid(jid));
                if (item != null) {
                    name = item.fullName;
                }
            }
        } else {
            if (!jid.contains("@")) {
                callback.finish(jid);
                return;
            } else {
                name = InternDatas.getName(jid);
            }
        }
        if (!TextUtils.isEmpty(name)) {
            callback.finish(name);
            return;
        }

        UserVCard vCard = getLocalVCard(jid);


        if (!TextUtils.isEmpty(vCard.nickname)) {
            callback.finish(vCard.nickname);
            InternDatas.saveName(jid, vCard.nickname);
            if (!TextUtils.isEmpty(vCard.gravantarUrl)) {
                InternDatas.JidToUrl.put(jid, QtalkStringUtils.getGravatar(vCard.gravantarUrl, true));
            }
            return;
        }

        if (!bforce) {
            callback.finish(QtalkStringUtils.parseLocalpart(jid));
            return;
        }
        List<GetVCardData> datas = genarateGetData(vCard);
        VCardAPI.getVCardInfo(datas, new ProtocolCallback.UnitCallback<GetVCardResult>() {
            @Override
            public void onCompleted(final GetVCardResult profile4mUCenter) {
                if (profile4mUCenter != null && !ListUtil.isEmpty(profile4mUCenter.data)) {
                    GetVCardResult.VCardGroup group = profile4mUCenter.data.get(0);
                    if (!ListUtil.isEmpty(group.users)) {
                        GetVCardResult.VCardInfoN profile = group.users.get(0);
                        final UserVCard vCard = tranformUserToVCard(profile, group.domain);
                        InternDatas.saveName(jid, vCard.nickname);
                        InternDatas.JidToUrl.put(jid,
                                QtalkStringUtils.getGravatar(vCard.gravantarUrl, true));
                        QunarIMApp.mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.finish(vCard.nickname);
                            }
                        });
                        return;
                    }
                }
                QunarIMApp.mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.finish(QtalkStringUtils.parseLocalpart(jid));
                    }
                });
            }

            @Override
            public void onFailure(String errMsg) {
                QunarIMApp.mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.finish(QtalkStringUtils.parseLocalpart(jid));
                    }
                });
            }
        });
    }


    public static void updateGVer(String gravantarUrl,String v,String commenturl, String jid) {
        final UserVCard vCard = getLocalVCard(jid);
        if (!TextUtils.isEmpty(v))
            vCard.gravantarVersion = vCard.gravantarVersion+1;
        if (!TextUtils.isEmpty(gravantarUrl))
            vCard.gravantarUrl = gravantarUrl;
        if (!TextUtils.isEmpty(commenturl))
            vCard.commentUrl = commenturl;
    }

    public static UserVCard getLocalVCard(String jid) {
        String id =  QtalkStringUtils.parseBareJid(jid);
        Object obj = InternDatas.cache.get(jid+"vcard");
        UserVCard vCard = null;
        if(obj instanceof UserVCard) vCard = (UserVCard) obj;
        if(vCard == null) {
            vCard = new UserVCard();
            vCard.id = id;
            vCard.gravantarVersion = -1;
        }
        return vCard;
    }

    public static Bitmap getGroupBitmap(String jid)
    {
        if (jid == null) return null;
        final String imageString = InternDatas.JidToUrl.get(jid);
        File file = null;
        if(!TextUtils.isEmpty(imageString))
        {
            String url = QtalkStringUtils.addFilePathDomain(imageString, true);
            file = MyDiskCache.getFile(url);
            if(!file.exists()) return null;
        }
        else {
            final File groupGravatar = new File(FileUtils.getExternalFilesDir(getContext()),
                    "gravatar");
            file = new File(groupGravatar, jid);
            if (!file.exists()) {
                ChatRoom chatRoom = new ChatRoom();
                chatRoom.setJid(jid);
                if (TextUtils.isEmpty(chatRoom.getPicUrl())) {
                    return null;
                }
                String url = QtalkStringUtils.addFilePathDomain(chatRoom.getPicUrl(), true);
                file = MyDiskCache.getFile(url);
                if (!file.exists()) return null;
                InternDatas.JidToUrl.put(jid,url);
            }
        }
        return BitmapHelper.decodeFile(file.getPath());
    }

    public static void setGroupPicture(final SimpleDraweeView mImageView, final int defaultPic, final Context context, final String jid) {
        FacebookImageUtil.loadFromResource(defaultPic, mImageView);
        if(TextUtils.isEmpty(jid))return;
        final String imageString = InternDatas.JidToUrl.get(jid);
        if(!TextUtils.isEmpty(imageString))
        {
            mImageView.setTag(null);
            FacebookImageUtil.loadWithCache(imageString,mImageView);
        }
        else {
            mImageView.setTag(jid);
            BackgroundExecutor.execute(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    }


    public interface LoadNickNameCallback {
        void finish(String name);
    }


    public static String getNickByKey(String k)
    {
        String key = QtalkStringUtils.userId2Jid(k);
        if(CommonConfig.isQtalk)
        {
            Object val = InternDatas.getData(Constants.SYS.CONTACTS_MAP);
            if(val!=null) {
                Map<String, DepartmentItem>
                        map = (Map<String, DepartmentItem>)val;
                DepartmentItem item = map.get(key);
                if(item!=null)
                {
                    return item.fullName;
                }
            }
            return QtalkStringUtils.parseLocalpart(key);
        }
        String nick = InternDatas.getName(key);
        if(TextUtils.isEmpty(nick))
        {
            UserVCard userVCard = getLocalVCard(key);
            if(!TextUtils.isEmpty(userVCard.nickname))
            {
                return userVCard.nickname;
            }
        }
        return QtalkStringUtils.parseLocalpart(key);
    }


}
