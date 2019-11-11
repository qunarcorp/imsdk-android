package com.qunar.im.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.base.jsonbean.ImgVideoBean;
import com.qunar.im.base.util.Constants;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.ImageBrowersingActivity;
import com.qunar.im.ui.activity.VideoPlayerActivity;
import com.qunar.im.ui.view.MyGridView;
import com.qunar.im.utils.DeviceUtil;
import com.qunar.im.utils.QtalkStringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class MuliSizeImageAdapter extends RecyclerView.Adapter<MuliSizeImageAdapter.ViewHolder>{

    public static final int MAX_SEL_COUNT = 9;

    LinkedHashMap<String,List<ImgVideoBean>> map;
    List<String> keys;
    Context context;

    OnSelectedChangeListener listener;
    ArrayList<ImgVideoBean> selectedDatas = new ArrayList<>();
    boolean isSelecting;
    int w;
    public MuliSizeImageAdapter(Context context){
        this.context = context;
        w = DeviceUtil.getWindowWidthPX(context) / 4;
    }

    public void setData(LinkedHashMap<String,List<ImgVideoBean>> map,List<String> keys){
        this.map = map;
        this.keys = keys;
    }

    public void setSelecting(boolean selecting) {
        isSelecting = selecting;
        if(!isSelecting){
            selectedDatas.clear();
        }
    }

    public void setOnSelectedChangeListener(OnSelectedChangeListener listener){
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.atom_ui_list_search_recycle_item,null));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(keys == null || map == null){
            return;
        }
        String key = keys.get(position);
        holder.bindData(key,map.get(key));
    }

    @Override
    public int getItemCount() {
        return keys==null ? 0 :keys.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        MyGridView gridView;
        public ViewHolder(View view){
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            gridView = (MyGridView) view.findViewById(R.id.gridView);
        }

        public void bindData(String key, final List<ImgVideoBean> beans){
            title.setText(key);
            gridView.setAdapter(new ImageAdapter(beans));
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ImgVideoBean bean = beans.get(position);
                    if(bean.type == ImgVideoBean.IMG){
                        Intent intent = new Intent(context, ImageBrowersingActivity.class);
                        intent.putExtra(Constants.BundleKey.IMAGE_URL,QtalkStringUtils.addFilePathDomain(bean.url, true));
                        context.startActivity(intent);
                    }else if(bean.type == ImgVideoBean.VIDEO){
                        Intent intent = new Intent();
                        intent.setClass(context, VideoPlayerActivity.class);
                        intent.setData(Uri.parse(QtalkStringUtils.addFilePathDomain(bean.url, true)));
                        intent.putExtra(Constants.BundleKey.FILE_NAME, bean.fileName);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }

    class ImageAdapter extends BaseAdapter{
        List<ImgVideoBean> beans;
        public ImageAdapter(List<ImgVideoBean> beans){
            this.beans = beans;
        }

        @Override
        public int getCount() {
            return beans == null ? 0 :beans.size();
        }

        @Override
        public ImgVideoBean getItem(int position) {
            return beans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ImageViewHolder holder;
            if(convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.atom_ui_search_recycle_img_item,parent,false);
                holder = new ImageViewHolder(convertView);
                convertView.setTag(holder);
            }else {
                holder = (ImageViewHolder) convertView.getTag();
            }
            final ImgVideoBean bean = getItem(position);
            String url;
            if(bean.type == ImgVideoBean.IMG){
                url = QtalkStringUtils.addFilePathDomain(bean.url, true);
                holder.videoView.setVisibility(View.GONE);
                if(TextUtils.isEmpty(url)){
                    com.orhanobut.logger.Logger.i("图片崩溃错误5");
                    return new View(context);
                }
                Glide.with(context)
                        .load(url)//配置上下文
//                        .load(new MyGlideUrl(url))      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                        .centerCrop()
                        .error(R.drawable.atom_ui_ic_default_image)           //设置错误图片
                        .placeholder(R.drawable.atom_ui_ic_default_image)     //设置占位图片
                        .transform(new CenterCrop(context))
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)//缓存全尺寸
                        .override(w,w)
                        .dontAnimate()
                        .into(holder.image_item);
            }else if(bean.type == ImgVideoBean.VIDEO){
                holder.videoView.setVisibility(View.VISIBLE);
                if(!TextUtils.isEmpty(bean.Duration)){
                    int d = Integer.parseInt(bean.Duration);
                    String duration = d/60 + ":";
                    duration += String.format("%02d", d%60);
                    holder.video_duaration.setText(duration);
                }
                url = QtalkStringUtils.addFilePathDomain(bean.thumbUrl, true);
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setUri(Uri.parse(url))
                        .setAutoPlayAnimations(false)
                        .setOldController(holder.image_item.getController())
                        .build();
                holder.image_item.setController(controller);
            }
            holder.cb_check.setVisibility(isSelecting ? View.VISIBLE : View.GONE);
            holder.cb_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(selectedDatas.size() == MAX_SEL_COUNT){
                        if(isChecked){
                            Toast.makeText(context, context.getString(R.string.atom_ui_ip_select_limit, 9), Toast.LENGTH_SHORT).show();
                            holder.cb_check.setChecked(false);
                            return;
                        }
                    }
                    if(isChecked){
                        selectedDatas.add(bean);
                    }else {
                        selectedDatas.remove(bean);
                    }
                    if(listener != null){
                        listener.onSelected(selectedDatas);
                    }

                }
            });
            return convertView;
        }

        public class ImageViewHolder{
            SimpleDraweeView image_item;
            CheckBox cb_check;
            View videoView;
            TextView video_duaration;
            ImageViewHolder( View convertView) {
                image_item = (SimpleDraweeView)convertView.findViewById(R.id.image_item);
                cb_check = (CheckBox)convertView.findViewById(R.id.cb_check);
                video_duaration = (TextView) convertView.findViewById(R.id.video_duaration);
                videoView = convertView.findViewById(R.id.videoView);
                image_item.setLayoutParams(new FrameLayout.LayoutParams(w, w));
            }
        }
    }

   public interface OnSelectedChangeListener{
        void onSelected(ArrayList<ImgVideoBean> beans);
    }
}
