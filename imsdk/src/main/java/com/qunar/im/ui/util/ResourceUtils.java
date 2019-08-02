package com.qunar.im.ui.util;

import android.content.Context;

import com.qunar.im.base.util.Utils;

/**
 * Created by huayu.chen on 2016/5/24.
 */
public class ResourceUtils {
    public static int getFontSizeIntervalPX(Context context)
    {
        return Utils.dipToPixels(context,2);
    }
}
