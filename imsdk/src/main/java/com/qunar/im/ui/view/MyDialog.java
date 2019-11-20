package com.qunar.im.ui.view;


import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.widget.TextView;

import com.qunar.im.ui.R;

/**
 * Created by xinbo.wang on 2016/5/3.
 */
public class MyDialog extends AlertDialog {
    TextView textView;
    public MyDialog(Context context) {
        this(context,0);
    }

    public MyDialog(Context context, int theme) {
        super(context, theme);
    }

    public MyDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_layout_my_dialog);
        textView = findViewById(R.id.toast_content);
    }

    public void setContent(String content)
    {
        textView.setText(content);
    }
}
