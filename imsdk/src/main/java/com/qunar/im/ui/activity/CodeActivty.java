package com.qunar.im.ui.activity;

import android.os.Bundle;

import com.qunar.im.base.util.Constants;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.codeView.CodeView;
import com.qunar.im.ui.view.codeView.CodeViewTheme;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;
import com.qunar.im.utils.ConnectionUtil;

public class CodeActivty extends SwipeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_layout_code);

        CodeView codeView = (CodeView)findViewById(R.id.codeView);
        codeView.setTheme(CodeViewTheme.listThemes()[0]);
        codeView.fillColor();

        String msgId = getIntent().getStringExtra(Constants.BundleKey.MESSAGE_ID);
        String code = ConnectionUtil.getInstance().queryMessageContent(msgId);
        codeView.showCode(code);
    }
}
