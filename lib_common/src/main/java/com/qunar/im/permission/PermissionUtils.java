package com.qunar.im.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

public class PermissionUtils {

    private PermissionUtils() {
    }

    /**
     * Checks all given permissions have been granted.
     *
     * @param grantResults results
     * @return returns true if all permissions have been granted.
     */
    public static boolean verifyPermissions(int... grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * check given permission has been granted
     * @param context
     * @param permission
     * @return returns true if permission has been granted.
     */
    public static boolean checkPermissionGranted(Context context, String permission) {
        return checkSelfPermission(context, permission) == PermissionChecker.PERMISSION_GRANTED;
    }

    /**
     * Returns true if <code>Activity</code> or <code>Fragment</code> has access to all given;permissions.
     *
     * @param context     context
     * @param permissions permissions
     * @return returns true if <code>Activity</code> or <code>Fragment</code> has access to all given permissions.
     */
    public static List<String> hasSelfPermissions(Context context,List<String> permissions) {
        List<String> result = new ArrayList<String>(permissions.size());
        for (String permission : permissions) {
            if (checkSelfPermission(context, permission) != PermissionChecker.PERMISSION_GRANTED) {
                result.add(permission);
            }
        }
        return result;
    }

    /**
     * Checks given permissions are needed to show rationale.
     *
     * @param activity    activity
     * @param permissions permission list
     * @return returns true if one of the permission is needed to show rationale.
     */
    public static boolean shouldShowRequestPermissionRationale(Activity activity, String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks given permissions are needed to show rationale.
     *
     * @param fragment fragment
     * @param permissions permission list
     * @return returns true if one of the permission is needed to show rationale.
     */
    public static boolean shouldShowRequestPermissionRationale(Fragment fragment, String... permissions) {
        for (String permission : permissions) {
            if (fragment.shouldShowRequestPermissionRationale(permission)) {
                return true;
            }
        }
        return false;
    }

}
