package com.qunar.im.ui.view.baseView.processor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.jsonbean.EncryptMsg;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.base.structs.TransitFileJSON;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.core.utils.GlobalConfigManager;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.DownloadFileActivity;
import com.qunar.im.ui.util.FileTypeUtil;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.ReceiveFileView;
import com.qunar.im.ui.view.baseView.ViewPool;
import com.qunar.im.ui.view.bubbleLayout.BubbleLayout;

/**
 * Created by zhaokai on 15-8-17.
 */
public class FileMessageProcessor extends DefaultMessageProcessor {



    @Override
    public void processChatView(ViewGroup parent,IMessageItem item) {
        final IMMessage message = item.getMessage();
        final Context context = item.getContext();
        if (message != null) {
            ReceiveFileView fileView = ViewPool.getView(ReceiveFileView.class,item.getContext());
            TransitFileJSON jsonObject = null;
            try {
                if(message.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeEncrypt_VALUE){
                    EncryptMsg encryptMsg = ChatTextHelper.getEncryptMessageBody(message);
                    if (encryptMsg != null) {
                        jsonObject = JsonUtils.getGson().fromJson(encryptMsg.Content, TransitFileJSON.class);
                    }
                }else {
                    try {
                        jsonObject = JsonUtils.getGson().fromJson(message.getExt(), TransitFileJSON.class);
                        if(jsonObject == null){
                            jsonObject = JsonUtils.getGson().fromJson(message.getBody(), TransitFileJSON.class);
                        }
                    }catch (Exception e){
                        jsonObject = JsonUtils.getGson().fromJson(message.getBody(), TransitFileJSON.class);
                    }
                }
                if (jsonObject != null) {
                    Logger.i("上传文件 progress = " + message.getProgress() + "  status = " + message.getReadState() + "  body = " + message.getBody()) ;
                    fileView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, DownloadFileActivity.class);
                            Bundle bundle=new Bundle();
                            bundle.putSerializable("file_message",message);
                            intent.putExtras(bundle);
                            context.startActivity(intent);
                        }
                    });
                    int ids = jsonObject.FileName.lastIndexOf(".");
                    int fileType = R.drawable.atom_ui_icon_zip_video;;
                    if(ids>0) {
                        String fExt = jsonObject.FileName.substring(ids+1);
                        fileType = FileTypeUtil.getInstance().getFileTypeBySuffix(fExt);
                    }
                    fileView.getFileIcon().setImageResource(fileType);
                    fileView.getFileFrom().setText(context.getString(R.string.atom_ui_from) + GlobalConfigManager.getAppName());
                    fileView.setFileName(jsonObject.FileName);
                    fileView.setFileSize(jsonObject.FileSize);
                    parent.addView(fileView);
                }else if(!TextUtils.isEmpty(message.getBody())){
                    fileView.setFileName(message.getBody());
                    parent.addView(fileView);
                }
                if(MessageStatus.isProcession(message.getMessageState())){
                    fileView.setProgress(message.getProgress());
                }else{
                    fileView.finish();
                }
            } catch (Exception e) {
                if(!TextUtils.isEmpty(message.getBody())){
                    fileView.setFileName(message.getBody());
                    Logger.i("上传文件 progress = " + message.getProgress() + "  status = " + message.getReadState() + "  body = " + message.getBody()) ;
                    if(MessageStatus.isProcession(message.getMessageState())){
                        fileView.setProgress(message.getProgress());
                    }else{
                        fileView.finish();
                    }
                    parent.addView(fileView);
                }
                LogUtil.e(TAG,"ERROR",e);
                LogUtil.d("debug", e.getMessage());
            }
        }
    }

    @Override
    public void processBubbleView(BubbleLayout bubbleLayout, IMessageItem item) {
        bubbleLayout.setBubbleColor(ContextCompat.getColor(item.getContext(), R.color.atom_ui_chat_bubble_left_bg));
        bubbleLayout.setStrokeColor(ContextCompat.getColor(item.getContext(),R.color.atom_ui_chat_bubble_left_stoken_color));
    }
}
