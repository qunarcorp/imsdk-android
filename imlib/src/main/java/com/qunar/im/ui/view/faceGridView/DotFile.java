package com.qunar.im.ui.view.faceGridView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import com.qunar.im.ui.R;

/**
 * Created by xinbo.wang on 2015/2/5.
 */
public class DotFile {
    Context context;
    public DotFile(Context context)
    {
        this.context = context;
    }


    public  StateListDrawable setStateDrawable() {
        StateListDrawable sd = new StateListDrawable();

        Drawable down = context.getResources().getDrawable(R.drawable.atom_ui_dot_normal);
        Drawable up = context.getResources().getDrawable(R.drawable.atom_ui_dot_foucsed);
        // 负号代表false
        sd.addState(new int[] { -android.R.attr.state_enabled }, down);
        sd.addState(new int[] { android.R.attr.state_enabled }, up);
        return sd;
    }
}
