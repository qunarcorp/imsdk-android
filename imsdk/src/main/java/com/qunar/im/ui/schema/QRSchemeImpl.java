package com.qunar.im.ui.schema;

import android.content.Intent;

import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.view.zxing.activity.CaptureActivity;

import java.util.Map;

/**
 * Created by wangxinbo on 2017/1/22.
 */
public class QRSchemeImpl implements QChatSchemaService {
    public final static QRSchemeImpl instance = new QRSchemeImpl();
    @Override
    public boolean startActivityAndNeedWating(IMBaseActivity context, Map<String, String> map) {
        Intent scanQRCodeIntent = new Intent(context,  CaptureActivity.class);
        context.startActivityForResult(scanQRCodeIntent, QchatSchemeActivity.SCAN_REQUEST);
        return true;
    }
}
