package com.qunar.rn_service.util;


import com.qunar.im.base.util.FileUtils;

import java.io.File;


/**
 * Created by wangyu.wang on 16/5/9.
 */
public class MD5Helper {

    /**
     *
     * @param filename 路径+文件名
     * @param md5      需要校验的MD5值
     * @return         是否校验成功,文件不存在时默认返回false
     */
    public static boolean checkMD5ByFilename(String filename, String md5){

        boolean result = false;
        try {
           String md5Str =  getMD5ByFilename(filename);
           if(md5Str.equalsIgnoreCase(md5)){
               result = true;
           }
        }catch (Exception e) {
            //
        } finally {

        }

        return result;
    }

    /**
     *
     * @param filename 路径+文件名
     * @return         文件的MD5值,当文件不存在时返回空字符串
     */
    public static String getMD5ByFilename(String filename) {
        File file = new File(filename);
        return FileUtils.fileToMD5(file);
    }

    public static void main(String args[]) {
        String filepath = "/Users/qitmac000005/code/ops/qchat_camelhelp/SRC/app/src/androidTest/java/com/qunar/im/camelhelp/utils/a.txt";
        String md5 = "6f5902ac237024bdd0c176cb93063dc4";
        System.out.println(checkMD5ByFilename(filepath, md5));
    }
}
