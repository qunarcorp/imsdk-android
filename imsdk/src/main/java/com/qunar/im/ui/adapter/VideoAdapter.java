package com.qunar.im.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.VideoSelectorActivity;
import com.qunar.im.ui.view.bigimageview.view.MyGlideUrl;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by xingchao.song on 2/16/2016.
 */
public class VideoAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<VideoSelectorActivity.VideoInfo> mVideoInfos;

    public VideoAdapter(Context context, ArrayList<VideoSelectorActivity.VideoInfo> videoInfos) {
        mContext = context;
        mVideoInfos = videoInfos;
    }

    @Override
    public int getCount() {
        return mVideoInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mVideoInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.atom_ui_item_video_select, parent, false);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.iv_frist_frame = (ImageView) convertView.findViewById(R.id.iv_frist_frame);
            viewHolder.tv_duration = (TextView) convertView.findViewById(R.id.tv_duration);
            viewHolder.cb_selected = (CheckBox) convertView.findViewById(R.id.cb_selected);
            viewHolder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        VideoSelectorActivity.VideoInfo info = mVideoInfos.get(position);
        String fileName = "";
        if (!TextUtils.isEmpty(info.path)) {
            int indexOfSlash = info.path.lastIndexOf("/");
            if (indexOfSlash != -1) {
                fileName = info.path.substring(indexOfSlash + 1);
            } else {
                fileName = info.path;
            }
        }

        float duration = Long.parseLong(info.duration) / 1000;
        float filesize = Long.parseLong(info.size) / (1024 * 1024);
        viewHolder.tv_name.setText("名称: " + fileName);

        viewHolder.tv_duration.setText("时长: " + duration + "s");
        viewHolder.tv_size.setText("大小: " + filesize + "m");

        if (!TextUtils.isEmpty(info.thumbPath) && new File(info.thumbPath).exists()) {
            Glide.with(mContext).load(info.thumbPath).asBitmap().placeholder(R.drawable.atom_ui_sharemore_picture).into(viewHolder.iv_frist_frame);
        } else {
            Glide.with(mContext).load(info.path).asBitmap().placeholder(R.drawable.atom_ui_sharemore_picture).into(viewHolder.iv_frist_frame);
        }
        if (((GetCheckedItemNumber) mContext).getNumber() == position) {
            viewHolder.cb_selected.setChecked(true);
        } else {
            viewHolder.cb_selected.setChecked(false);
        }
        return convertView;
    }

    static class ViewHolder {
        TextView tv_name;
        ImageView iv_frist_frame;
        TextView tv_duration;
        CheckBox cb_selected;
        TextView tv_size;
    }


    public interface GetCheckedItemNumber {
        int getNumber();
    }
}
