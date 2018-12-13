package com.qunar.im.ui.view;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import com.qunar.im.ui.R;


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
        textView = (TextView) findViewById(R.id.toast_content);
    }

    public void setContent(String content)
    {
        textView.setText(content);
    }
}
