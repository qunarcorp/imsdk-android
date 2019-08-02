package com.qunar.im.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.orhanobut.logger.Logger;
import com.qunar.im.ui.util.FacebookImageUtil;
import com.qunar.im.base.util.Utils;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.EditPictureActivity;
import com.qunar.im.ui.activity.ImageClipActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xinbo.wang on 2015/5/22.
 */
public class PictureSelectorAdapter extends CommonAdapter<String> {
    private Context mContext;

    public List<String> selectedImages = new ArrayList<>(3);
    public Map<String, SimpleDraweeView> draweeViewList = new HashMap<>();

    private boolean showEditor = true;

    private ImageClickHandler imageClickHandler;

    private int maxSelect;

    private boolean isMultiSelector;

    public void setMaxSelect(int maxSelect) {
        this.maxSelect = maxSelect;
    }

    public void setMultiSelector(boolean isMultiSelector) {
        this.isMultiSelector = isMultiSelector;
    }

    public List<String> getSelectedImages() {
        return selectedImages;
    }

    public int getSelectedCount() {
        return selectedImages.size();
    }

    public void setImageClickHandler(ImageClickHandler imageClickHandler) {
        this.imageClickHandler = imageClickHandler;
    }

    public void chageDirAndDatas(List<String> datas) {
        super.changeData(datas);
    }

    public PictureSelectorAdapter(Context cxt, List<String> datas, int itemLayoutId,
                                  boolean isShowEditor) {
        super(cxt, datas, itemLayoutId);
        mContext = cxt;
        this.showEditor = isShowEditor;
    }

    @Override
    public void convert(CommonViewHolder viewHolder, final String item) {
        final SimpleDraweeView img = viewHolder.getView(R.id.show_local_image);
        final ImageButton imgStatus = viewHolder.getView(R.id.status_selector);
        final TextView tvEditPicture = viewHolder.getView(R.id.tv_edit_photo);
        draweeViewList.put(item, img);
        //   final String filePath = dirPath + "/" + item;
        final String filePath = item;
        int width = (Utils.getScreenWidth(mContext) / 3) - 8;
        FacebookImageUtil.loadLocalImage(new File(item), img, width, width, true, FacebookImageUtil.EMPTY_CALLBACK);
        if (isMultiSelector) {
            imgStatus.setImageResource(R.drawable.atom_ui_picture_unselected);
        } else {
            imgStatus.setVisibility(View.GONE);
        }
        if (isMultiSelector) {
            imgStatus.setImageResource(R.drawable.atom_ui_picture_unselected);
        } else {
            imgStatus.setVisibility(View.GONE);
        }

        img.setColorFilter(null);

        imgStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击角标选中
                if (isMultiSelector) {
                    if (selectedImages.contains(item)) {
                        selectedImages.remove(item);
                        imgStatus.setImageResource(R.drawable.atom_ui_picture_unselected);
                        img.setColorFilter(null);
                    } else {
                        if (selectedImages.size() == maxSelect) {
                            selectedImages.remove(0);
                            selectedImages.add(item);
                            notifyDataSetChanged();
                            return;
                        }
                        selectedImages.add(item);
                        imgStatus.setImageResource(R.drawable.atom_ui_pictures_selected);
                        img.setColorFilter(Color.parseColor("#77000000"));
                    }
                }
                if (imageClickHandler != null) {
                    imageClickHandler.imageClickEvent(item);
                }
            }
        });
        //单选直接返回
        if(!isMultiSelector){
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (imageClickHandler != null) {
                        imageClickHandler.imageClickEvent(item);
                    }
                }
            });
        }
        if(showEditor) {
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //点击图片编辑
    //                if (isMultiSelector) {
    //                    if (selectedImages.contains(item)) {
    //                        selectedImages.remove(item);
    //                        imgStatus.setImageResource(R.drawable.atom_ui_picture_unselected);
    //                        img.setColorFilter(null);
    //                    } else {
    //                        if (selectedImages.size() == maxSelect) {
    //                            selectedImages.remove(0);
    //                            selectedImages.add(item);
    //                            notifyDataSetChanged();
    //                            return;
    //                        }
    //                        selectedImages.add(item);
    //                        imgStatus.setImageResource(R.drawable.atom_ui_pictures_selected);
    //                        img.setColorFilter(Color.parseColor("#77000000"));
    //                    }
    //                }
    //                if (imageClickHandler != null) {
    //                    imageClickHandler.imageClickEvent(item);
    //                }
                    Intent intent = new Intent(mContext, EditPictureActivity.class);
                    intent.putExtra(ImageClipActivity.KEY_CAMERA_PATH, item);
                    mContext.startActivity(intent);
                }
            });

            tvEditPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, EditPictureActivity.class);

                    intent.putExtra(ImageClipActivity.KEY_CAMERA_PATH, item);
                    mContext.startActivity(intent);
                }
            });
        }
        else {
            tvEditPicture.setVisibility(View.GONE);
        }

        if (isMultiSelector) {

            /**
             * 已经选择过的图片，显示出选择过的效果
             */
            if (selectedImages.contains(item)) {
                imgStatus.setImageResource(R.drawable.atom_ui_pictures_selected);
                img.setColorFilter(Color.parseColor("#77000000"));
            }
        }
    }

    public void releaseDraweeView(){
        if(draweeViewList != null && draweeViewList.size() > 0){
            for (Map.Entry<String, SimpleDraweeView> entry : draweeViewList.entrySet()) {
                if(entry.getValue() != null && entry.getValue().getController() != null){
                    if(entry.getValue() != null){
                        if(entry.getValue().getController() != null){
                            entry.getValue().getController().onDetach();
                        }
                        entry.getValue().setImageDrawable(null);
                        Logger.i("release cache");
                    }
                }
            }
            draweeViewList.clear();
        }
        selectedImages.clear();
    }

    public interface ImageClickHandler {
        void imageClickEvent(String selectFilePath);
    }
}