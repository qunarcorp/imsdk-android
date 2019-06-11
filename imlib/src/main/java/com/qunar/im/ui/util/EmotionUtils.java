package com.qunar.im.ui.util;

import android.content.Context;
import android.text.TextUtils;

import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.graphics.ImageUtils;
import com.qunar.im.base.view.faceGridView.EmoticionMap;
import com.qunar.im.base.view.faceGridView.EmoticonEntity;
import com.qunar.im.base.view.faceGridView.EmoticonFileUtils;
import com.qunar.im.common.CommonConfig;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by saber on 15-7-3.
 * modify by lihaibin on 19-06-11
 *  某些方法 涉及到反射 getEmoticionByShortCut、clearEmoticonCache
 */
public class EmotionUtils {
    public static File EMOJICON_DIR;
    public static final String FAVORITE_ID = "favorite";

    private final static Object lockObj = new Object();
    private static EmoticionMap defaultEmotion;
    private static EmoticionMap defaultEmotion1;
    private static Map<String, EmoticionMap> extEmotions = new LinkedHashMap<String, EmoticionMap>();
    private static Map<String, String> pkgId2Name = new LinkedHashMap<>();
    static {
        String userid = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(Constants.Preferences.fullname, "");
        EMOJICON_DIR = new File(CommonConfig.globalContext.getFilesDir(), Constants.SYS.EMOTICON_FAVORITE_DIR + File.separator + userid);
    }
    public static EmoticionMap getDefaultEmotion(Context context) {
        if (defaultEmotion == null) {
            synchronized (lockObj) {
                if (defaultEmotion == null) {
                    defaultEmotion = (new EmoticonFileUtils("emoticons/", "emoticons/DefaultEmoticon.xml")).geteMap(context);
                    pkgId2Name.put(defaultEmotion.packgeId, "经典");
                }
            }
        }
        //defaultMap 默认3*7
        defaultEmotion.showAll = 1;
        return defaultEmotion;
    }

    //内置小托表情
    public static EmoticionMap getDefaultEmotion1(Context context) {
        if (defaultEmotion1 == null) {
            synchronized (lockObj) {
                if (defaultEmotion1 == null) {
                    defaultEmotion1 = (new EmoticonFileUtils("Big_Emoticons/qunar_camel/", "Big_Emoticons/qunar_camel/qunar_camel.xml")).geteMap(context);
                    pkgId2Name.put(defaultEmotion1.packgeId, "去哪儿小驼");
                }
            }
        }
        //defaultMap 默认3*7
//        defaultEmotion1.showAll = 1;
        return defaultEmotion1;
    }

    public static EmoticonEntity getEmoticionByShortCut(String shortCut, String pkgId, boolean checkExt) {
        EmoticonEntity emoticonEntity = null;
        if (TextUtils.isEmpty(pkgId)) {
            if (defaultEmotion != null) {
                emoticonEntity = defaultEmotion.getEntity(shortCut);
                if(emoticonEntity != null)
                   emoticonEntity.showAll = true;
            }
            if (checkExt && emoticonEntity == null && extEmotions.size() > 0) {
                for (String key : extEmotions.keySet()) {
                    emoticonEntity = extEmotions.get(key).getEntity(shortCut);
                    if (emoticonEntity != null) {
                        emoticonEntity.showAll = extEmotions.get(key).showAll == 1;
                        break;
                    }
                }
            }
        }
        else {
            if(defaultEmotion != null&&defaultEmotion.packgeId.equals(pkgId))
            {
                emoticonEntity = defaultEmotion.getEntity(shortCut);
                if(emoticonEntity != null)
                    emoticonEntity.showAll = true;
            }else if(defaultEmotion1 !=null && defaultEmotion1.packgeId.equals(pkgId)){
                emoticonEntity = defaultEmotion1.getEntity(shortCut);
            }
            else if(checkExt&&pkgId2Name.containsKey(pkgId))
            {
                EmoticionMap eMap= extEmotions.get(pkgId2Name.get(pkgId));
                if (eMap != null) {
                    emoticonEntity = eMap.getEntity(shortCut);
                    emoticonEntity.showAll = eMap.showAll == 1;
                }
            }
        }
        return emoticonEntity;
    }

    public static boolean isExistsEmoticon(String shortCut, String pkgId, boolean checkExt) {
        if (TextUtils.isEmpty(pkgId)) {
            if (defaultEmotion != null && defaultEmotion.containKey(shortCut)) {
                return true;
            }
            if (checkExt && extEmotions.size() > 0) {
                for (String key : extEmotions.keySet()) {
                    if (extEmotions.get(key).containKey(shortCut))
                        return true;
                }
            }
            return false;
        } else {
            if (defaultEmotion!=null&&defaultEmotion.packgeId.equals(pkgId)) return defaultEmotion.containKey(shortCut);
            return checkExt && pkgId2Name.containsKey(pkgId) &&
                    extEmotions.containsKey(pkgId2Name.get(pkgId))
                    && extEmotions.get(pkgId2Name.get(pkgId)).containKey(shortCut);
        }
    }


    public static Map<String, EmoticionMap> getExtEmotionsMap(Context context,boolean isForce) {
        if(isForce){
            extEmotions.clear();
        }
        if (extEmotions.size() == 0) {
            //extEmotions.put(YAHOO,new EmoticonFileUtils("emoticons/", "emoticons/yahooEmotions.xml").geteMap(context));
//            File dir = new File(context.getFilesDir(), Constants.SYS.EMOTICON_DIR);
//        extEmotions.clear();
            File dir = EmotionUtils.getExtEmoticonFileDir();
            if (!dir.exists()) {
                dir.mkdirs();
            } else {
                List<Map<String, String>> list = getEmotions(context);
                for (int i = 0; i < list.size(); i++) {
                    String name = list.get(i).get("name");
                    String pkgId = list.get(i).get("pkgid");
                    String path = list.get(i).get("path");
                    String xmlPath = list.get(i).get("xml");
                    pkgId2Name.put(pkgId, name);
                    EmoticionMap map = (new EmoticonFileUtils(path + "/", xmlPath).geteMap());
                    if(map != null){
                        //客户端特殊处理 qq yahoo表情showall默认修改成1
                        if("qq".equals(pkgId) || "yahoo".equals(pkgId)){
                            map.showAll = 1;
                        }
                        extEmotions.put(name, map);
                    }
                }
            }
        }
        return extEmotions;
    }

    /*
   * 将自定义表情从文件中读出放入Map中
   * 暂时将所有的自定义表情放入某一个特定的文件夹,在这个文件夹中读出文件信息,存入EmoticonEntity中做适配
   * */
    public static EmoticionMap getFavoriteMap(Context context) {
//        File dir = EMOJICON_DIR;//new File(context.getFilesDir(), Constants.SYS.EMOTICON_FAVORITE_DIR);

        File dir = EmotionUtils.getFavorEmoticonFileDir();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        });
        EmoticionMap map = new EmoticionMap("0", files.length + 1, 0, 0, "");
        EmoticonEntity entity = new EmoticonEntity();
        entity.id = FAVORITE_ID;
        map.pusEntity(entity.id, entity);
        for (File f : files) {
            EmoticonEntity tmpEntity = new EmoticonEntity();
            tmpEntity.fileFiexd = f.getAbsolutePath();
            tmpEntity.fileOrg = f.getAbsolutePath();
            map.pusEntity(f.getName(), tmpEntity);
        }
        //收藏表情默认 8*2
        map.showAll = 0;
        map.packgeId = FAVORITE_ID;
        return map;
    }

    public static void putEmotions(Context cxt, String name, String pkgId, String dirPath, String xmlPath) {
        String userid = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(Constants.Preferences.fullname, "");
        Object obj = DataUtils.getInstance(cxt).loadObject(cxt, Constants.Preferences.emoticon + userid);
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if (obj != null) {
            list = (List<Map<String, String>>) obj;
        }
        Map<String, String> entry = new HashMap<String, String>();
        entry.put("name", name);
        entry.put("pkgid", pkgId);
        entry.put("path", dirPath);
        entry.put("xml", xmlPath);
        list.add(entry);
        DataUtils.getInstance(cxt).saveObject(cxt, list, Constants.Preferences.emoticon + userid);
    }


    //删除文件夹和文件夹里面的文件
    public static void deleteDir(final String pPath) {
        File dir = new File(pPath);
        deleteDirWihtFile(dir);
    }

    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    public static void removeEmotions(Context cxt, String name, String pkgId){
        String userid = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(Constants.Preferences.fullname, "");
        Object obj = DataUtils.getInstance(cxt).loadObject(cxt, Constants.Preferences.emoticon + userid);
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if (obj != null) {
            list = (List<Map<String, String>>) obj;
        }

        for (int i = 0; i < list.size(); i++) {
            Map<String, String> entry = list.get(i);
            if(entry.get("name").equals(name)){
//                File file = new File(entry.get("path"));
                deleteDir(entry.get("path"));
                list.remove(i);
            }
        }

//
//        Map<String, String> entry = new HashMap<String, String>();
//        entry.put("name", name);
//        entry.put("pkgid", pkgId);
////        entry.put("path", dirPath);
////        entry.put("xml", xmlPath);
//        list.add(entry);
        DataUtils.getInstance(cxt).saveObject(cxt, list, Constants.Preferences.emoticon + userid);
    }

    public static List<Map<String, String>> getEmotions(Context cxt) {
        String userid = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(Constants.Preferences.fullname, "");
        Object obj = DataUtils.getInstance(cxt).loadObject(cxt, Constants.Preferences.emoticon + userid);
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if (obj != null) {
            list = (List<Map<String, String>>) obj;
        }
        return list;
    }


    public static void loadSpecialExtEmot(Context cxt, String name, String pkgId, File emtDir) {
        if (emtDir != null && emtDir.exists()) {
            File[] xmlFile = emtDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    if (pathname.isFile() && pathname.getName().endsWith(".xml")) {
                        return true;
                    }
                    return false;
                }
            });
            if (xmlFile.length > 0) {
                pkgId2Name.put(pkgId, name);
                extEmotions.put(name, (new EmoticonFileUtils(emtDir.getPath() + "/", xmlFile[0].getPath())).geteMap());
                putEmotions(cxt, name, pkgId, emtDir.getPath(), xmlFile[0].getPath());
            } else {
                emtDir.delete();
            }
        }
    }

    /**
     * @param filePaths 存为表情的文件
     **/
    public static String saveImgToFavoriteEmojiconDir(final Context context, final List<String> filePaths) {
//        final File dir = EMOJICON_DIR;//new File(context.getFilesDir(), Constants.SYS.EMOTICON_FAVORITE_DIR);
        File dir = EmotionUtils.getFavorEmoticonFileDir();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        final int length = filePaths.size();
        if (length == 0)
            return "";
        //保存的图片暂时以时间排序,这样,展示就会依收藏的先后顺序
        for (String path : filePaths) {
            File src = new File(path);
            if (src.exists() && src.isFile()) {
                File target = new File(dir, fileToMD5(src));
                ImageUtils.compressFile(src, Constants.Config.MAX_EMOJICON_SIZE, 256,256,target);
            }
        }
        return dir.getAbsolutePath();
    }

    private static String fileToMD5(File file) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file); // Create an FileInputStream instance according to the filepath
            byte[] buffer = new byte[1024]; // The buffer to read the file
            MessageDigest digest = MessageDigest.getInstance("MD5"); // Get a MD5 instance
            int numRead = 0; // Record how many bytes have been read
            while (numRead != -1) {
                numRead = inputStream.read(buffer);
                if (numRead > 0)
                    digest.update(buffer, 0, numRead); // Update the digest
            }
            byte[] md5Bytes = digest.digest(); // Complete the hash computing
            return convertHashToString(md5Bytes); // Call the function to convert to hex digits
        } catch (Exception e) {
            return "";
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close(); // Close the InputStream
                } catch (Exception e) {
                }
            }
        }
    }

    private static String convertHashToString(byte[] hashBytes) {
        String returnVal = "";
        for (int i = 0; i < hashBytes.length; i++) {
            returnVal += Integer.toString((hashBytes[i] & 0xff) + 0x100, 16).substring(1);
        }
        return returnVal.toLowerCase();
    }

    public static File getFavorEmoticonFileDir(){
        String userid = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(Constants.Preferences.fullname, "");
        File dir = new File(CommonConfig.globalContext.getFilesDir(), Constants.SYS.EMOTICON_FAVORITE_DIR + File.separator + userid);
        return dir;
    }
    public static File getExtEmoticonFileDir(){
        String userid = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(Constants.Preferences.fullname, "");
        File dir = new File(CommonConfig.globalContext.getFilesDir(), Constants.SYS.EMOTICON_DIR + File.separator + userid);
        return dir;
    }

    /**
     * 清楚表情缓存
     */
    public static void clearEmoticonCache(){
        defaultEmotion = null;
        defaultEmotion1 = null;
        extEmotions.clear();
        pkgId2Name.clear();
    }

}