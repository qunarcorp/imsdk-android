package com.qunar.im.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.qunar.im.base.module.MultiItemEntity;
import com.qunar.im.ui.view.recyclerview.BaseMultiItemQuickAdapter;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.base.structs.TransitFileJSON;
import com.qunar.im.base.util.DateTimeUtils;
import com.qunar.im.base.util.FileUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.MyFilesActivity;
import com.qunar.im.ui.activity.MyFilesDetailActivity;
import com.qunar.im.ui.entity.MyFilesItem;
import com.qunar.im.ui.entity.MyFilesTitle;
import com.qunar.im.ui.util.FileTypeUtil;
import com.qunar.im.ui.view.recyclerview.BaseViewHolder;
import com.qunar.im.utils.QtalkStringUtils;

import java.io.File;
import java.util.List;

/**
 * Created by Lex lex on 2018/5/30.
 */

public class MyFilesAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    public static final int TYPE_LEVEL_0 = 0;
    public static final int TYPE_LEVEL_1 = 1;
    private Context context;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public MyFilesAdapter(Context context, List<MultiItemEntity> data) {
        super(data);
        this.context = context;
        addItemType(TYPE_LEVEL_0, R.layout.activity_ui_myfiles_title_item);
        addItemType(TYPE_LEVEL_1, R.layout.activity_ui_myfiles_item);
    }

    @Override
    protected void convert(final BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()){
            case TYPE_LEVEL_0:
                final MyFilesTitle myFilesTitle = (MyFilesTitle) item;
                helper.setText(R.id.myfile_title, myFilesTitle.title);
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = helper.getAdapterPosition();
                        if (myFilesTitle.isExpanded()) {
                            collapse(pos);
                        } else {
                            expand(pos);
                        }
                    }
                });
                break;
            case TYPE_LEVEL_1:
                MyFilesItem myFilesItem = (MyFilesItem) item;
                final IMMessage imMessage = myFilesItem.filemessage;
                String conntent = imMessage.getBody();
                if(!TextUtils.isEmpty(conntent)) {
                    TransitFileJSON transitFileJSON = JsonUtils.getGson().fromJson(conntent, TransitFileJSON.class);
                    if(transitFileJSON != null) {
                        helper.setText(R.id.myfile_name, transitFileJSON.FileName);
                        helper.setText(R.id.myfile_size, transitFileJSON.FileSize);
                        helper.setText(R.id.myfile_time, DateTimeUtils.getTime(imMessage.getTime().getTime(), false, true));
                        int ids = transitFileJSON.FileName.lastIndexOf(".");
                        int fileType = R.drawable.atom_ui_icon_zip_video;;
                        if(ids>0) {
                            String fExt = transitFileJSON.FileName.substring(ids+1);
                            fileType = FileTypeUtil.getInstance().getFileTypeBySuffix(fExt);
                        }
                        helper.setImageResource(R.id.myfile_icon, fileType);

                        File file = new File(FileUtils.savePath + transitFileJSON.FileName);
                        if(CurrentPreference.getInstance().getPreferenceUserId().equals(imMessage.getFromID())) {
                            if (MessageStatus.isExistStatus (imMessage.getMessageState(),MessageStatus.LOCAL_STATUS_SUCCESS)) {
                                helper.setText(R.id.myfile_status, context.getString(R.string.atom_ui_common_sent));
                            } else {
                                helper.setText(R.id.myfile_status, context.getString(R.string.atom_ui_common_notsent));
                            }
                            if (imMessage.getToID().contains("@conference")) {
                                ConnectionUtil.getInstance().getMucCard(imMessage.getToID(), new IMLogicManager.NickCallBack() {
                                    @Override
                                    public void onNickCallBack(Nick nick) {
                                        StringBuilder builder = new StringBuilder();
                                        builder.append(context.getString(R.string.atom_ui_common_issue) + " ");
                                        if(nick != null){
                                            builder.append(nick.getName());
                                        } else {
                                            builder.append(QtalkStringUtils.parseId(imMessage.getFromID()));
                                        }
                                        helper.setText(R.id.myfile_from, builder.toString());

                                    }
                                }, false, true);
                            }else {
                                ConnectionUtil.getInstance().getMucCard(imMessage.getToID(), new IMLogicManager.NickCallBack() {
                                    @Override
                                    public void onNickCallBack(Nick nick) {
                                        StringBuilder builder = new StringBuilder();
                                        builder.append(context.getString(R.string.atom_ui_common_issue) + " ");
                                        if(nick != null){
                                            builder.append(nick.getName());
                                        } else {
                                            builder.append(QtalkStringUtils.parseId(imMessage.getFromID()));
                                        }
                                        helper.setText(R.id.myfile_from, builder.toString());
                                    }
                                }, false, true);
                            }

                        } else {
                            if (file.exists()) {
                                helper.setText(R.id.myfile_status, context.getString(R.string.atom_ui_common_already_download));
                            } else {
                                helper.setText(R.id.myfile_status, context.getString(R.string.atom_ui_common_not_download));
                            }
                            ConnectionUtil.getInstance().getUserCard(imMessage.getFromID(), new IMLogicManager.NickCallBack() {
                                @Override
                                public void onNickCallBack(Nick nick) {
                                    StringBuilder builder = new StringBuilder();
                                    builder.append(context.getString(R.string.atom_ui_from) + " ");
                                    if(nick != null && !TextUtils.isEmpty(nick.getName())){
                                        builder.append(nick.getName());
                                    } else {
                                        builder.append(QtalkStringUtils.parseId(imMessage.getFromID()));
                                    }
                                    helper.setText(R.id.myfile_from, builder.toString());

                                }
                            }, false, true);
                        }
                    }
                    helper.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startFileDetailActivity(imMessage);
                        }
                    });
                }
                break;
        }
    }

    private void startFileDetailActivity(IMMessage imMessage) {
        Intent intent = new Intent(context, MyFilesDetailActivity.class);
        intent.putExtra("message", imMessage);
        ((Activity)context).startActivityForResult(intent, MyFilesActivity.REQUEST_FILE_DETAIL);
    }
}
