package com.qunar.im.base.util;

import android.text.TextUtils;

import com.qunar.im.base.common.CommonDownloader;
import com.qunar.im.base.jsonbean.DownloadImageResult;
import com.qunar.im.base.jsonbean.EncryptMsg;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.structs.MessageType;
import com.qunar.im.base.structs.TransitSoundJSON;
import com.qunar.im.base.transit.DownloadRequest;
import com.qunar.im.base.transit.IDownloadRequestComplete;
import com.qunar.im.base.util.graphics.MyDiskCache;
import com.qunar.im.base.view.faceGridView.EmoticonEntity;
import com.qunar.im.common.R;
import com.qunar.im.core.utils.GlobalConfigManager;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.utils.QtalkStringUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jiang.cheng on 2015/2/5.
 */

/**
 * 线程不安全，不可多线程调用
 */
public class ChatTextHelper {
    public final static String TAG = "ChatTextHelper";

    public interface DownloadVoiceCallback {
        void onComplete(boolean isSuccess);
    }

    private final static Map<Integer, String> defaultMsg = new HashMap<Integer, String>();

    static {
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeFile_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_file));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeLocalShare_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_location));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeTopic_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_newmsg));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeActionRichText_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_rich));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeRichText_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_rich));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeNotice_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_notification));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeSystem_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_system));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeBurnAfterRead_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_burn));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeSmallVideo_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_videomsg));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeRobotAnswer_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_robotmsg));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeVoice_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_voice));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeShock_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_shake));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeRedPack_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_redpkg));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeRedPackInfo_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_redpkgnoc));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeActivity_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_activity));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeAA_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_aapay));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeAAInfo_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_aapaynoc));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeNote_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_productdtl));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeShareLocation_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_sharelocation));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeProduct_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_productlink));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeTransChatToCustomerService_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_sestransfer));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeTransChatToCustomer_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_sestransfer));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeConsult_VALUE, "[来生意了]");
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeCommonTrdInfo_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_linkcard));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeRevoke_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_recall));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeConsultRevoke_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_recall));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.WebRTC_MsgType_VideoCall_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_videocall));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.WebRTC_MsgType_Video_Group_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_videogroup));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.WebRTC_MsgType_Video_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_ui_tip_client_too_low));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.WebRTC_MsgType_Audio_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_ui_tip_client_too_low));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeRobotQuestionList_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_questionlist));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeRobotTurnToUser_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_turntocs));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.WebRTC_MsgType_AudioCall_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_audiocall));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeGrabMenuVcard_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_grabmsg));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeGrabMenuResult_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_grabmsg));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeImageNew_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_sticker));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeMeetingRemind_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_meetinginvite));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeEncrypt_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_encryptmsg));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypeSourceCode_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_sourcecode));
        defaultMsg.put(ProtoMessageOuterClass.MessageType.MessageTypePhoto_VALUE, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_photo));
    }

    static public String showContentType(String strText, int msgType) {
        if (strText == null)
            return "";
        if (msgType == ProtoMessageOuterClass.MessageType.MessageTypeGroupNotify_VALUE)
            return strText;
        String result = defaultMsg.get(msgType);

        if ((msgType == ProtoMessageOuterClass.MessageType.MessageTypeText_VALUE || msgType == ProtoMessageOuterClass.MessageType.MessageTypeGroupAt_VALUE) && result == null)
            result = replaceSpecialChar(strText);
        if (MessageType.AUTO_REPLY_MESSAGE == msgType)
            result = strText;
        if (result == null && !TextUtils.isEmpty(strText))
            result = strText;
        if (result == null)
            result = "[不支持该类型消息，请升级到最新版]";
        return result;
    }

    static public String showDraftContent(String draft) {
        boolean isMyEmoji = false;
        boolean keyStart = false;
        StringBuilder builder = new StringBuilder(10);
        StringBuilder pkgBuilder = new StringBuilder(20);
        StringBuilder results = new StringBuilder(draft.length());
        for (char c : draft.toCharArray()) {
            if (isMyEmoji) {
                if (c == 1) {
                    keyStart = true;
                    continue;
                } else if (c == 255 && builder.length() >= 2) {
                    EmoticonEntity entity = getEmojiEntityByInvoke(builder.toString(),pkgBuilder.toString(),true);
                    if (entity != null) {
                        results.append("[");
                        results.append(entity.tip);
                        results.append("]");
                    }
                    builder.setLength(0);
                    pkgBuilder.setLength(0);
                    isMyEmoji = false;
                    keyStart = false;
                }
                if (isMyEmoji && keyStart)
                    builder.append(c);
                else if (isMyEmoji)
                    pkgBuilder.append(c);
                if (builder.length() >= 10 ||
                        pkgBuilder.length() >= 20) {
                    builder.setLength(0);
                    pkgBuilder.setLength(0);
                    isMyEmoji = false;
                    keyStart = false;
                }
            } else if (c == 0) {
                isMyEmoji = true;
            } else {
                results.append(c);
            }
        }
        return results.toString();
    }

    static public TransitSoundJSON turnText2SoundObj(final IMMessage message, boolean isSend, final DownloadVoiceCallback callback) {
        TransitSoundJSON json = null;
        //加密消息 先解密
        if (message.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeEncrypt_VALUE) {
            EncryptMsg encryptMsg = getEncryptMessageBody(message);
            if (encryptMsg != null) {
                json = JsonUtils.getGson().fromJson(encryptMsg.Content, TransitSoundJSON.class);
            }
        } else {
            try {
                json = JsonUtils.getGson().fromJson(message.getExt(), TransitSoundJSON.class);
                if (json == null) {
                    json = JsonUtils.getGson().fromJson(message.getBody(), TransitSoundJSON.class);
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "error", e);
                json = JsonUtils.getGson().fromJson(message.getBody(), TransitSoundJSON.class);
            }
        }
        if (json != null) {
            File file = MyDiskCache.getVoiceFile(json.FileName);
            json.FileName = file.getAbsolutePath();
            if (file.exists()) {
                if (!isSend) {
                    callback.onComplete(true);
                }
                return json;
            }

            final DownloadRequest request = new DownloadRequest();
            request.url = QtalkStringUtils.addFilePathDomain(json.HttpUrl, true);
            request.savePath = file.getAbsolutePath();
            request.requestComplete = new IDownloadRequestComplete() {
                @Override
                public void onRequestComplete(DownloadImageResult result) {
                    if (result != null && result.isDownloadComplete()) {
                        callback.onComplete(true);
                    } else {
                        callback.onComplete(false);
                    }
                }
            };
            CommonDownloader.getInsatnce().setDownloadRequest(request);
        }
        return json;
    }

    /**
     * 加密消息的 真实body体
     *
     * @param message
     * @return
     */
    public static EncryptMsg getEncryptMessageBody(IMMessage message) {
        EncryptMsg encryptMsg = null;
        String encryptUid = message.getDirection() == IMMessage.DIRECTION_SEND ? message.getToID() : message.getConversationID();
        String password;
        if (DataCenter.decryptUsers.containsKey(encryptUid)) {
            password = DataCenter.decryptUsers.get(encryptUid);
        } else password = DataCenter.encryptUsers.get(encryptUid);
        if (!TextUtils.isEmpty(password)) {//存在密码解密
            try {
                if (!TextUtils.isEmpty(message.getExt()))
                    encryptMsg = JsonUtils.getGson().fromJson(AESTools.decodeFromBase64(password, message.getExt()), EncryptMsg.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return encryptMsg;
    }




    public static String generateQueryString4image(int w, int h, boolean start) {

//        double maxHPixel = Utils.getScreenHeight(QunarIMApp.getContext()) * 0.3;
//        double maxWPixel = Utils.getScreenWidth(QunarIMApp.getContext()) * 0.3;
//        int rate = ImageUtils.computeSampleSize(w, h, -1, (int) (maxWPixel * maxHPixel));
//
//        if (rate <= 1) {
//            double dw = w;
//            double dh = h;
//            rate = (int) Math.max(Math.ceil(dw / maxWPixel), Math.ceil(dh / maxHPixel));
//        }
//        int rw = w / rate;
//        int rh = h / rate;

        StringBuilder stringBuilder = new StringBuilder();
        if (start) {
            stringBuilder.append("&");
        } else {
            stringBuilder.append("?");
        }

        stringBuilder.append("w=");
        stringBuilder.append(w);
        stringBuilder.append("&h=");
        stringBuilder.append(h);
        return stringBuilder.toString();
    }

    static public String textToHTML(String strText) {

        boolean isMyEmoji = false;
        char[] chars = strText.toCharArray();
        StringBuilder keyBuilder = new StringBuilder(128);
        StringBuilder pkgBuilder = new StringBuilder(128);
        StringBuilder valueBuilder = new StringBuilder();
        boolean keyStart = false;
        int idx = 0;
        for (char c : chars) {
            valueBuilder.append(c);
            if (isMyEmoji) {
                if (c == 1) {
                    keyStart = true;
                    continue;
                } else if (c == 255 && keyBuilder.length() >= 2) {
                    String key = keyBuilder.toString();
                    String pkgId = pkgBuilder.toString();
                    try{
                        Class<?> threadClazz = Class.forName("com.qunar.im.ui.util.EmotionUtils");
                        Method method = threadClazz.getMethod("isExistsEmoticon", String.class,String.class,boolean.class);
                        Object o = method.invoke(null,key,pkgId,true);
                        if(o != null){
                            boolean result = (boolean)o;
                            if (result) {
                                valueBuilder.delete(idx, valueBuilder.length());
                                valueBuilder.append("[obj type=\"emoticon\" value=\"[")
                                        .append(key)
                                        .append("]\" width=")
                                        .append(pkgId)
                                        .append(" height=0 ]");
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    isMyEmoji = false;
                    keyStart = false;
                    keyBuilder.setLength(0);
                    pkgBuilder.setLength(0);
                }
                if (isMyEmoji && keyStart) keyBuilder.append(c);
                else if (isMyEmoji) pkgBuilder.append(c);
                if (keyBuilder.length() >= 128 ||
                        pkgBuilder.length() >= 128) {
                    isMyEmoji = false;
                    keyStart = false;
                    keyBuilder.setLength(0);
                    pkgBuilder.setLength(0);
                }
            } else if (c == 0) {
                isMyEmoji = true;
                idx = valueBuilder.length() - 1;
            }
        }
        keyBuilder.setLength(0);
        Matcher matcher = compliedUrlPattern.matcher(valueBuilder);
//        int startIndex = 0;
        StringBuilder sb = new StringBuilder();
        int startIndex = 0;
        while (matcher.find()) {

//            String path = valueBuilder.toString();
//            if (!path.contains("[obj type=")) {
            String url = matcher.group();
//            if (url.lastIndexOf("?") != -1) {
//                url = url.substring(0, url.lastIndexOf("?"));
//            }


            sb.append(valueBuilder.substring(startIndex, matcher.start()));

//            if (url.endsWith(".png") || url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".gif") || url.endsWith(".tiff")) {
////                    valueBuilder.replace(matcher.start(), matcher.end(), "[obj type=\"image\" value=\"" + url + "\"] 图片地址:"+
////                            "[obj type=\"url\" value=\"" + url + "\"]");
//
//                sb.append("[obj type=\"image\" value=\"" + url + "\"] 图片地址:" +
//                        "[obj type=\"url\" value=\"" + url + "\"]");
//            } else {

                sb.append("[obj type=\"url\" value=\"" + url + "\"]");

//            }
            startIndex = matcher.end() ;
//                    {
////                    String url = matcher.group(0);
////                    valueBuilder.replace(matcher.start(), matcher.end(), "[obj type=\"url\" value=\"" + url + "\"]");
//                }
//            }
        }
        sb.append(valueBuilder.substring(startIndex));
        return sb.toString();
    }

    public static String textToImgHtml(String strText, int width, int height) {
        if (strText == null)
            return "";
        return String.format("[obj type=\"image\" value=\"%s\" width=%d height=%d]", strText, width, height);
    }


    public static String textToUrl(String url) {
        return "[obj type=\"url\" value=\"" + url + "\"]";
    }

    private final static String objPattern = "\\[obj type=\"([\\w]+)\" value=\"([\\S]+)\"([\\w|=|\\s|\\.]+)?\\]";
    //    private final static String objPattern = "\\[obj type=\"(.*?)\" value=\"(.*?)\"(.*?)\\]";
    private final static Pattern compiledPattern = Pattern.compile(objPattern);

    /*public static final String GOOD_IRI_CHAR =
            "a-zA-Z0-9\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF";

    public static final Pattern IP_ADDRESS
            = Pattern.compile(
            "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                    + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                    + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                    + "|[1-9][0-9]|[0-9]))");*/

    /**
     * RFC 1035 Section 2.3.4 limits the labels to a maximum 63 octets.
     */
    /*private static final String IRI
            = "[" + GOOD_IRI_CHAR + "]([" + GOOD_IRI_CHAR + "\\-]{0,61}[" + GOOD_IRI_CHAR + "]){0,1}";

    private static final String GOOD_GTLD_CHAR =
            "a-zA-Z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF";
    private static final String GTLD = "[" + GOOD_GTLD_CHAR + "]{2,63}";
    private static final String HOST_NAME = "(" + IRI + "\\.)+" + GTLD;

    public static final Pattern DOMAIN_NAME
            = Pattern.compile("(" + HOST_NAME + "|" + IP_ADDRESS + ")");

    //private final static String httpUrlPattern = "((http[s]{0,1}|ftp)://[a-zA-Z0-9\\\\.\\\\-]+\\\\.([a-zA-Z0-9]{1,4})(:\\\\d+)?([^ ^\\\"^\\\\]]*)?)|(www\\\\.[a-zA-Z0-9\\\\.\\\\-]+\\\\.([a-zA-Z]{2,4})(:\\\\d+)?([^ ^\\\"^\\\\[^\\\\]]*)?)";
    private final static Pattern compliedUrlPattern =  Pattern.compile(
            "((?:(http|https|Http|Https|rtsp|Rtsp):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)"
                    + "\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_"
                    + "\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?"
                    + "(?:" + DOMAIN_NAME + ")"
                    + "(?:\\:\\d{1,5})?)" // plus option port number
                    + "(\\/(?:(?:[" + GOOD_IRI_CHAR + "\\;\\/\\?\\:\\@\\&\\=\\#\\~"  // plus option query params
                    + "\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?"
                    + "(?:\\b|$)");*/
    private final static String httpUrlPattern = "((http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z0-9]{1,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+,?:_/={}\\[\\]\\(\\)`~|]*)?)|(www\\.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*,+?:_/={}\\[\\]\\(\\)`~|]*)?)";
    private final static Pattern compliedUrlPattern = Pattern.compile(httpUrlPattern);
    //Pattern.compile("(((ftp|https?)://|www.)[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z0-9]{1,4})([^ ^\"^\\[^\\]^\\r^\\n]*))");

    /***
     * 将一个形如[obj type="xx" value="xxx"]的字符串转化为一List<Map>
     * 将List中的所有项转化为Map键值对
     * 形如[obj type="emoticon" value="[/chi]"]将转化为
     * type:emotion value:[/chi]
     * 的Map,如果有其他字段将放入extra属性,如果有[obj]和text混合,则Text的type为"text"
     *
     * @param srcObj 原始字符串
     * @return List<Map> 包含该串的所有信息的List
     */
    public static List<Map<String, String>> getObjList(String srcObj) {
        List<Map<String, String>> result = new ArrayList();
        if (srcObj == null) {
            srcObj = "";
        }
        if (srcObj.length() < 21) {
            Map<String, String> textMap = new HashMap<String, String>();
            textMap.put("type", "text");
            textMap.put("value", srcObj);
            result.add(textMap);
            return result;
        }

        Matcher m = compiledPattern.matcher(srcObj);
        int start = 0;
        int end = 0;
        while (m.find()) {
            String type = m.group(1);
            String value = m.group(2);
            String ext = null;
            if (m.groupCount() >= 3) {
                ext = m.group(3);
            }
            end = m.start();
            if (end > start) {
                Map<String, String> textMap = new HashMap<String, String>();
                textMap.put("type", "text");
                textMap.put("value", srcObj.substring(start, end));
                result.add(textMap);
            }
            start = m.end();

            Map<String, String> objMap = new HashMap<String, String>();
            objMap.put("type", type);
            objMap.put("value", value);
            if (ext != null) {
                objMap.put("extra", ext);
            }
            result.add(objMap);
        }
        if (start == end) {
            Map<String, String> textMap = new HashMap<String, String>();
            textMap.put("type", "text");
            textMap.put("value", srcObj);
            result.add(textMap);
        } else if (start > end && start < srcObj.length() - 1) {
            Map<String, String> textMap = new HashMap<String, String>();
            textMap.put("type", "text");
            textMap.put("value", srcObj.substring(start));
            result.add(textMap);
        }
        return result;
    }

    private static String replaceSpecialChar(String srcObj) {
        if (srcObj == null) {
            return "";
        }

        if (srcObj.length() < 21) {
            return srcObj;
        }

        Matcher m = compiledPattern.matcher(srcObj);
        while (m.find()) {
            String oldStr = m.group(0);
            String type = m.group(1);

            if (type.equals("image")) {
                srcObj = srcObj.replace(oldStr, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_photo));
            } else if (type.equals("emoticon")) {
                String shortcut = m.group(2);
                EmoticonEntity emotionEntry = null;
                if (shortcut.length() > 3) {
                    String pkgId = "";
                    if (m.groupCount() == 4) {
                        String ext = m.group(3).trim();
                        pkgId = ext.substring(6, ext.indexOf(" "));
                    }
                    shortcut = shortcut.substring(1, shortcut.length() - 1);
                    emotionEntry = getEmojiEntityByInvoke(shortcut,pkgId,true);
                }
                if (emotionEntry == null) {
                    srcObj = srcObj.replace(oldStr, GlobalConfigManager.getGlobalContext().getString(R.string.atom_common_text_sticker));
                } else {
                    srcObj = srcObj.replace(oldStr, "[" + emotionEntry.tip + "]");
                }
            } else if (type.equals("url")) {
                srcObj = srcObj.replace(oldStr, m.group(2));
            }
        }
        return srcObj;
    }

    private static EmoticonEntity getEmojiEntityByInvoke(String shortcut,String pkgId,boolean checkExt){
        EmoticonEntity entity = null;
        try{
            Class<?> threadClazz = Class.forName("com.qunar.im.ui.util.EmotionUtils");
            Method method = threadClazz.getMethod("getEmoticionByShortCut", String.class,String.class,boolean.class);
            Object o = method.invoke(null,shortcut,pkgId,checkExt);
            if(o != null){
                entity = (EmoticonEntity)o;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return entity;
    }
}
