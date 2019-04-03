package com.qunar.rn_service.util;

import com.qunar.im.base.util.LogUtil;
import com.qunar.im.common.CommonConfig;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wangyu.wang on 2016/11/30.
 */
public class QTalkServicePatchDownloadHelper {

    public static final String TAG = "QTalkPatchDownloadHelper";
    private static final String TEMP_SUBFIX = ".tmp";

    public static boolean downloadFullPackageAndCheck(String fullpackageUrl,  // 资源下载路径 md5文件
                                                      final String fullpackageMd5, // zip 包md5
                                                      final String bundleName, // zip包中的bundle文件名
                                                      final String zipName, // 存储zip包文件名 rn-qtalk-search.ios.jsbundle.tar.gz
                                                      final String cachaPath, // 本地缓存路径  全路径
                                                      final String desetAssetName // bundle 存储文件名 index.ios.jsbundle_v8
    ) {
        // TODO download fullpackage -> check md5 -> unzip -> rename bundlename
        String downloadUrl = fullpackageUrl;
        boolean is_ok = false;

        if (downloadUrl.startsWith("https://") || downloadUrl.startsWith("http://")) {
            try {

                InputStream response = DownloadHelper.downloadByUrl(downloadUrl);

                final String path = cachaPath;
                final String filename = zipName;
                final String finalBundleFile = desetAssetName;

                if (QTalkServicePatchDownloadHelper.saveResponse2file(response, path, filename) &&
//                        QTalkServicePatchDownloadHelper.checkBundleMD5(path, filename, fullpackageMd5) &&
                        QTalkServicePatchDownloadHelper.unzipBundle(path, filename) &&
                        QTalkServicePatchDownloadHelper.renameFile(path, bundleName, finalBundleFile)) {

                    is_ok = true;
                } else {
                    // check md5 or save or unzip or rename error
                }

            } catch (Exception e) {
                // download error
                LogUtil.e(TAG, e.toString());
            }

        }

        return is_ok;
    }

    /**
     * RN外部应用使用
     *
     * @return
     */
    public static boolean downloadPatchAndSave(String patchUrl,//下载路径
                                               String cachePath,//存储路径
                                               String innerBundleName //存储文件名
    ) {
        boolean is_ok = false;
        if (patchUrl.startsWith("https://") || patchUrl.startsWith("http://")) {
            try {
                InputStream inputStream = DownloadHelper.downloadByUrl(patchUrl);

                is_ok = QTalkServicePatchDownloadHelper.saveResponse2file(inputStream, cachePath, innerBundleName);
            } catch (Exception e) {
                is_ok=false;
            }
        }

        return is_ok;
    }


    public static boolean downloadPatchAndCheck(String patchUrl,
                                                final String patchMd5,
                                                final String fullMd5,
                                                final String cachePath, // 本地缓存路径  全路径
                                                final String deseAssetName, // bundle 存储文件名 index.ios.jsbundle_v8
                                                final String innerBundleName // 内置bundle 文件名 index.ios.jsbundle
    ) {
        // TODO download patch -> check patch md5 -> patch -> check patch bundle md5 -> rename file
        String downloadUrl = patchUrl;
        boolean is_ok = false;

        if (downloadUrl.startsWith("https://") || downloadUrl.startsWith("http://")) {
            try {

                InputStream response = DownloadHelper.downloadByUrl(downloadUrl);

                final String path = cachePath;
                // 随机生成patch文件名
                final String patch = RandomUtil.getRandomFileName();
                final String patchFilePath = path + patch;

                byte[] originFileBytes = getOriginBundleBytes(cachePath, deseAssetName, innerBundleName);
                if (originFileBytes == null) {
                    // read origin bytes error
                    return false;
                }

                String destPath = cachePath;
                String destFile = deseAssetName;
                String destFilePath = destPath + destFile;

                String tempDestFile = destFile + TEMP_SUBFIX;
                String tempDestFilePath = destPath + tempDestFile;

                if (!QTalkServicePatchDownloadHelper.saveResponse2file(response, path, patch)) {
                    // save patch
                    return false;
                }

                byte[] patchBytes = FileHelper.toByteArray(patchFilePath);
                if (QTalkServicePatchDownloadHelper.checkBundleMD5(path, patch, patchMd5) && // check patch md5
                        PatchHelper.patchByBytes(originFileBytes, patchBytes, tempDestFilePath) && //patch
                        QTalkServicePatchDownloadHelper.checkBundleMD5(destPath, tempDestFile, fullMd5) && // patch bundle md5
                        QTalkServicePatchDownloadHelper.renameFile(destPath, tempDestFile, destFile) // rename patch bundle file
                        ) {

                    is_ok = true;
                } else {
                    // check md5 or save or patch or check patch md5 orrename error
                }

            } catch (Exception e) {
                // download error
                LogUtil.e(TAG, e.toString());
            }

        }

        return is_ok;
    }

    public static byte[] getOriginBundleBytes(final String basePath, final String filename, final String innerBundleName) {
        String assetBundle = basePath + filename;
        File assetBundleFile = new File(assetBundle);

        byte[] content = null;

        try {
            if (!assetBundleFile.exists()) {
                // hot update bundle not existed
                InputStream in = CommonConfig.globalContext.getAssets().open(innerBundleName);
                content = FileHelper.input2byte(in);
            } else {
                content = FileHelper.toByteArray(assetBundle);
            }
        } catch (IOException e) {
            LogUtil.e(TAG, e.toString());
            content = null;
        }

        return content;
    }


    public static void clearFile(File file) {
        if (file.exists()) {
            file.delete();
        }
    }

    public static void makeDir(File path) {
        if (!path.exists()) {
            path.mkdirs();
        }
    }

    public static boolean saveResponse2file(InputStream response, String path, String filename) {
        final File finalFile = new File(path + filename);
        final File file = new File(path + filename + ".tmp");

        boolean is_ok = false;

        if (response != null) {
            FileOutputStream fileout = null;
            try {
                String basePath = file.getParent();
                File basePathFile = new File(basePath);

                makeDir(basePathFile);

                clearFile(file);
                clearFile(finalFile);

                file.createNewFile();
                fileout = new FileOutputStream(file);

                wriet2File(response, fileout);

                is_ok = file.renameTo(finalFile);
            } catch (Exception e) {
                LogUtil.e(TAG, e.toString());
                // 异常
            } finally {
                closeReaderWriter(fileout);
                closeReaderWriter(response);
            }
        }

        return is_ok;
    }

    public static void wriet2File(InputStream response, FileOutputStream fileout) throws IOException {

        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = response.read(buffer)) != -1) {
            fileout.write(buffer, 0, len);
        }
        fileout.flush();
    }

    public static void closeReaderWriter(Closeable closeable) {

        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            LogUtil.e(TAG, e.toString());
        }
    }

    public static boolean checkBundleMD5(String path, String filename, String MD5) {
        // check md5
        boolean result = false;

        try {
            result = MD5Helper.checkMD5ByFilename(path + filename, MD5);
        } catch (Exception e) {
            //LogUtil.e(TAG, "check bundle md5 error.");
        }

        return result;
    }

    public static boolean unzipBundle(String path, String filename) {
        // unzip package
        boolean result = true;

        try {
            ZipHelper.UnzipFileByFilename(path, filename);
        } catch (Exception e) {
            result = false;
            //LogUtil.e(TAG, "unzip bundle error.");
        }

        return result;
    }

    public static boolean renameFile(String path, String originName, String destName) {
        // unzip package
        boolean result = true;

        try {
            File file = new File(path + originName);
            File finalFile = new File(path + destName);
            file.renameTo(finalFile);
        } catch (Exception e) {
            result = false;
            //LogUtil.e(TAG, "unzip bundle error.");
        }

        return result;
    }
}
