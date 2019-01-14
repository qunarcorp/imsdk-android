package com.qunar.im.ui.schema;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.structs.TransitFileJSON;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.ui.activity.DownloadFileActivity;
import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.MyFilesActivity;
import com.qunar.rn_service.protocal.NativeApi;

import java.util.Map;

public class QOpenFileDownLoadImpl implements QChatSchemaService  {
    public final static QOpenFileDownLoadImpl instance = new QOpenFileDownLoadImpl();
    @Override
    public boolean startActivityAndNeedWating(IMBaseActivity context, Map<String, String> map) {

        IMMessage message = new IMMessage();
        TransitFileJSON transitFileJSON = new TransitFileJSON();
        transitFileJSON.FileSize = map.get(NativeApi.KEY_FILE_SIZE);
        transitFileJSON.HttpUrl = map.get(NativeApi.KEY_FILE_URL);
        transitFileJSON.FileName = map.get(NativeApi.KEY_FILE_NAME);
        transitFileJSON.FILEMD5 =map.get(NativeApi.KEY_FILE_MD5);
        transitFileJSON.noMD5 = Boolean.parseBoolean(map.get(NativeApi.KEY_FILE_NOMD5));
//        if(TextUtils.isEmpty(transitFileJSON.FILEMD5)){
//            transitFileJSON.noMD5 = true;
//        }
        message.setBody(JsonUtils.getGson().toJson(transitFileJSON));

        Intent intent = new Intent(context, DownloadFileActivity.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable("file_message",message);
        intent.putExtras(bundle);
        context.startActivity(intent);
        return false;
    }
}

