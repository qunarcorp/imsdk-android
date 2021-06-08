package com.qunar.im.permission;

/**
 * Created by saber on 15-11-25.
 */
public interface PermissionCallback {
    void responsePermission(int requestCode, boolean granted);
}
