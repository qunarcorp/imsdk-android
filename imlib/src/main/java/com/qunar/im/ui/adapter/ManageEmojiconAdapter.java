package com.qunar.im.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.ui.util.FacebookImageUtil;
import com.qunar.im.base.module.UserConfigData;
import com.qunar.im.ui.util.EmotionUtils;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.ui.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaokai on 16-1-21.
 */
public class ManageEmojiconAdapter extends BaseAdapter {
    private List<String> filePaths = new ArrayList<>();
    private List<UserConfigData> fileHtmls = new ArrayList<>();
    private List<File> selectList = new ArrayList<>();
    private List<UserConfigData> selectUCDList = new ArrayList<>();
    private File baseDir;
    private Context context;
    private View.OnClickListener addEmojBtnEvent;

    private boolean canDelete;
    private CheckBox.OnCheckedChangeListener listener = new CheckBox.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked)
            {
//                selectList.add((File) buttonView.getTag());
                selectUCDList.add((UserConfigData) buttonView.getTag());
            }
            else {
//                selectList.remove(buttonView.getTag());
                selectUCDList.remove(buttonView.getTag());
            }
        }
    };

    public void setAddEmojBtnEvent(View.OnClickListener listener)
    {
        addEmojBtnEvent = listener;
    }


    public ManageEmojiconAdapter(Context context,File dir){
        super();
        this.baseDir = dir;
        this.context = context;
    }

    public List<UserConfigData> getSelectList()
    {
//        return selectList;
        return selectUCDList;
    }

    public void setStatus(boolean status)
    {
        canDelete = status;
        if(canDelete)
        {
            if(fileHtmls.size()>0&&fileHtmls.get(fileHtmls.size()-1).getValue().equals(EmotionUtils.FAVORITE_ID))
            {
                fileHtmls.remove(fileHtmls.size()-1);
            }
        }
        else {
//            selectList.clear();
            selectUCDList.clear();
            UserConfigData ucd = new UserConfigData();
            ucd.setValue(EmotionUtils.FAVORITE_ID);
            if(fileHtmls.size() > 0){
                if(!fileHtmls.get(fileHtmls.size() - 1).equals(EmotionUtils.FAVORITE_ID)){
                    fileHtmls.add(ucd);
                }
            }else {
                fileHtmls.add(ucd);
            }
        }


//        canDelete = status;
//        if(canDelete)
//        {
//            if(filePaths.size()>0&&filePaths.get(filePaths.size()-1).equals(EmotionUtils.FAVORITE_ID))
//            {
//                filePaths.remove(filePaths.size()-1);
//            }
//        }
//        else {
//            selectList.clear();
//            if(filePaths.size() > 0){
//                if(!filePaths.get(filePaths.size() - 1).equals(EmotionUtils.FAVORITE_ID)){
//                    filePaths.add(EmotionUtils.FAVORITE_ID);
//                }
//            }else {
//                filePaths.add(EmotionUtils.FAVORITE_ID);
//            }
//        }
        notifyDataSetChanged();
    }

    public void setFilePaths(List<String> filePath){
        filePaths.clear();
        filePaths.addAll(filePath);
    }

    public void setFileHtml(List<UserConfigData> filePath){
        fileHtmls.clear();
        fileHtmls.addAll(filePath);
    }

    @Override
    public int getCount() {
//        return filePaths.size();
        return fileHtmls.size();
    }

    @Override
    public Object getItem(int position) {
//        return filePaths.get(position);
        return fileHtmls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.atom_ui_item_manage_emojicon, null);
            holder = new ViewHolder();
            holder.emotion_manager_layout = convertView.findViewById(R.id.emotion_manager_layout);
            holder.delete = (CheckBox)convertView.findViewById(R.id.delete);
            holder.cover = convertView.findViewById(R.id.cover_shade);
            holder.draweeView = (SimpleDraweeView)convertView.findViewById(R.id.draweeView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final UserConfigData ucd = fileHtmls.get(position);
        convertView.setOnClickListener(null);
        if (EmotionUtils.FAVORITE_ID.equals(ucd.getValue())){
            FacebookImageUtil.loadFromResource(R.drawable.atom_ui_ic_add_custom_emoji,holder.draweeView);
            convertView.setOnClickListener(addEmojBtnEvent);
            return convertView;
        }
        holder.delete.setVisibility(canDelete?View.VISIBLE:View.GONE);
        holder.cover.setVisibility(canDelete?View.VISIBLE:View.GONE);
//        File f = new File(baseDir,path);
        holder.delete.setOnCheckedChangeListener(null);
        if(selectUCDList.indexOf(ucd)>-1)
        {
            holder.delete.setChecked(true);
        }
        else {
            holder.delete.setChecked(false);
        }
        holder.delete.setOnCheckedChangeListener(listener);
//        final ViewHolder finalHolder = holder;
//        holder.emotion_manager_layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(canDelete){
//                    if(finalHolder.delete.isChecked())
//                    {
//                        selectList.add((File) finalHolder.delete.getTag());
//                    }
//                    else {
//                        selectList.remove(finalHolder.delete.getTag());
//                    }
//                }
//            }
//        });
        holder.delete.setTag(ucd);
        ProfileUtils.displayEmojiconByImageSrc((Activity) context, ucd.getValue(),  holder.draweeView,
                context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size), context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
//        FacebookImageUtil.loadWithCache(FileUtils.toUri(f.getPath()).toString(),
//                holder.draweeView,true);
        return convertView;
    }


    private static final class ViewHolder{
        View emotion_manager_layout;
        SimpleDraweeView draweeView;
        View cover;
        CheckBox delete;
    }
}
