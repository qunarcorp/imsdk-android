package com.qunar.rn_service.util;

import com.qunar.im.base.util.LogUtil;

import java.io.File;
import java.io.FileOutputStream;

import io.sigpipe.jbsdiff.Patch;

/**
 * Created by wangyu.wang on 16/9/5.
 */
public class PatchHelper {

    public static final String TAG = "PatchHelper";

    /**
     *
     * @param source file path+name
     * @param patch file patch+name
     * @param dest file patch+name
     * @return is patch success
     */
    public static boolean patchByPath(String source, String patch, String dest){

        boolean is_ok = false;

        File destFile = null;
        FileOutputStream destOut = null;

        try {
            byte[] sourceBytes = FileHelper.toByteArray(source);
            byte[] patchBytes = FileHelper.toByteArray(patch);

            destFile = new File(dest);
            destOut = new FileOutputStream(destFile);

            Patch.patch(sourceBytes, patchBytes, destOut);

            is_ok = true;
        } catch (Exception e) {
            LogUtil.e(TAG, e.toString());
        } finally {
            FileHelper.close(destOut);
        }

        return is_ok;
    }

    /**
     *
     * @param source file path+name
     * @param patch file patch+name
     * @param dest file patch+name
     * @return is patch success
     */
    public static boolean patchByBytes(byte[] source, byte[] patch, String dest){

        boolean is_ok = false;

        File destFile = null;
        FileOutputStream destOut = null;

        try {

            destFile = new File(dest);
            destOut = new FileOutputStream(destFile);

            Patch.patch(source, patch, destOut);

            is_ok = true;
        } catch (Exception e) {
            LogUtil.e(TAG, e.toString());
        } finally {
            FileHelper.close(destOut);
        }

        return is_ok;
    }
}
