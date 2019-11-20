package com.qunar.im.permission;

import android.app.Activity;
import androidx.core.app.ActivityCompat;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by saber on 15-11-25.
 */
public class PermissionDispatcher {

    /**
     * <uses-permission android:name="android.permission.INTERNET" />
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
     * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
     * <uses-permission android:name="android.permission.READ_PHONE_STATE" />
     * <uses-permission android:name="android.permission.WAKE_LOCK" />
     * <uses-permission android:name="android.permission.VIBRATE" />
     * <uses-permission android:name="android.permission.CAPTURE_VIDEO_OUTPUT" />
     * <uses-permission android:name="android.permission.RECORD_AUDIO" />
     * <uses-permission android:name="android.permission.CAMERA" />
     * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
     * <uses-permission android:name="android.permission.GET_TASKS" />
     * <uses-permission android:name="android.permission.REAL_GET_TASKS" />
     * <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
     * <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
     * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
     * <uses-permission android:name="android.permission.READ_PHONE_STATE" />
     * <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
     * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
     * <uses-permission android:name="android.permission.CHANGE_CONFIGURATION" />
     * <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>
     */
    private static AtomicInteger AUTO_INCREASE_KEY = new AtomicInteger(0);

    public static int getRequestCode()
    {
        int requestCode =  AUTO_INCREASE_KEY.getAndIncrement();
//        if(requestCode>127)
//        {
//            requestCode = 1;
//        }
        return requestCode;
    }

    public final static int REQUEST_CAMERA = 1;
    public final static int REQUEST_READ_PHONE_STATE = 2;
    public final static int REQUEST_ACCESS_COARSE_LOCATION = 4;
    public final static int REQUEST_ACCESS_FINE_LOCATION = 8;
    public final static int REQUEST_RECORD_AUDIO = 16;
    public final static int REQUEST_READ_EXTERNAL_STORAGE = 32;
    public final static int REQUEST_WRITE_EXTERNAL_STORAGE = 64;
    public final static int REQUEST_CALL = 128;
    public final static int REQUEST_READ_CALENDAR=256;
    public final static int REQUEST_WRITE_CALENDAR=512;
    public final static SparseArray<String> permissions = new SparseArray<String>();
    private final static SparseArray<PermissionCallback> stashCallback = new SparseArray<PermissionCallback>();
    static {
        permissions.append(REQUEST_CAMERA, "android.permission.CAMERA");
        permissions.append(REQUEST_READ_PHONE_STATE, "android.permission.READ_PHONE_STATE");
        permissions.append(REQUEST_ACCESS_COARSE_LOCATION, "android.permission.ACCESS_COARSE_LOCATION");
        permissions.append(REQUEST_ACCESS_FINE_LOCATION, "android.permission.ACCESS_FINE_LOCATION");
        permissions.append(REQUEST_RECORD_AUDIO, "android.permission.RECORD_AUDIO");
        permissions.append(REQUEST_READ_EXTERNAL_STORAGE, "android.permission.READ_EXTERNAL_STORAGE");
        permissions.append(REQUEST_WRITE_EXTERNAL_STORAGE, "android.permission.WRITE_EXTERNAL_STORAGE");
        permissions.append(REQUEST_CALL,"android.permission.CALL_PHONE");
        permissions.append(REQUEST_READ_CALENDAR,"android.permission.READ_CALENDAR");
        permissions.append(REQUEST_WRITE_CALENDAR,"android.permission.WRITE_CALENDAR");
    }

    public static void requestPermissionWithCheck(Activity ctx,int[] permissionFlags,PermissionCallback callback,int requestCode) {
        List<String> permissionStr = new ArrayList<String>(permissionFlags.length);
        for(int i=0;i<permissionFlags.length;i++)
        {
            int key = permissionFlags[i];
            String permission = permissions.get(key,null);
            if(permission!=null) permissionStr.add(permission);
        }
        permissionStr = PermissionUtils.hasSelfPermissions(ctx, permissionStr);
        if (permissionStr.size() == 0) {
            callback.responsePermission(requestCode,true);
        } else {
            stashCallback.append(requestCode, callback);
            String[] strings = new String[permissionStr.size()];
            ActivityCompat.requestPermissions(ctx, permissionStr.toArray(strings), requestCode);
        }
    }

    public static void onRequestPermissionsResult(int requestCode, int[] grantResults) {
        PermissionCallback callback = stashCallback.get(requestCode, null);
        if(callback!=null)
        {
            boolean results = PermissionUtils.verifyPermissions(grantResults);
            callback.responsePermission(requestCode,results);
            stashCallback.remove(requestCode);
        }
    }
}
