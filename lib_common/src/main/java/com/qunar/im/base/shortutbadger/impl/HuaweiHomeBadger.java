package com.qunar.im.base.shortutbadger.impl;

import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.qunar.im.base.shortutbadger.Badger;
import com.qunar.im.base.shortutbadger.ShortcutBadgeException;

import java.util.Arrays;
import java.util.List;


/**
 * @author Jason Ling
 */
public class HuaweiHomeBadger implements Badger {

    @Override
    public void executeBadge(Context context, ComponentName componentName, int badgeCount) throws ShortcutBadgeException {
        Bundle localBundle = new Bundle();
        localBundle.putString("package", context.getPackageName());
        localBundle.putString("class", componentName.getClassName());
        localBundle.putInt("badgenumber", badgeCount);
        context.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, localBundle);
    }

    @Override
    public List<String> getSupportLaunchers() {
        return Arrays.asList(
                "com.huawei.android.launcher"
        );
    }
}
