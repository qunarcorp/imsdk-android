package com.qunar.im.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.qunar.im.base.module.MultiItemEntity;
import com.qunar.im.base.module.ReleaseCircleNoChangeItemDate;
import com.qunar.im.base.util.MessageUtils;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.ui.R;
import com.qunar.im.ui.imagepicker.ImagePicker;
import com.qunar.im.base.module.ImageItem;
import com.qunar.im.ui.imagepicker.util.Utils;
import com.qunar.im.ui.view.recyclerview.BaseMultiItemQuickAdapter;
import com.qunar.im.ui.view.recyclerview.BaseViewHolder;

import java.util.List;

import static com.qunar.im.base.module.ReleaseCircleType.TYPE_CLICKABLE;
import static com.qunar.im.base.module.ReleaseCircleType.TYPE_UNCLICKABLE;
import static com.qunar.im.base.module.ReleaseCircleType.TYPE_WORK_WORLD_ITEM;

public class ReleaseCircleGridAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    private ImagePicker imagePicker;
    private Activity mActivity;
    private int mImageSize;
    private OnStartDragListener onStartDragListener;
    private boolean isok = true;
    private Context context;
    private int imgSize = 9;
    private int columns = 0;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public ReleaseCircleGridAdapter(List<? extends  MultiItemEntity> data, Activity activity) {
        super((List<MultiItemEntity>) data);
        this.context = context;
        imagePicker = ImagePicker.getInstance();
        mActivity = activity;
        mImageSize = Utils.getImageItemWidth(mActivity)-30;
        addItemType(TYPE_CLICKABLE, R.layout.atom_ui_release_circle_item);
        addItemType(TYPE_UNCLICKABLE, R.layout.atom_ui_release_circle_item);
        addItemType(TYPE_WORK_WORLD_ITEM, R.layout.atom_ui_release_circle_item);

    }

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public ReleaseCircleGridAdapter(List<? extends  MultiItemEntity> data, Activity activity,int columns) {
        super((List<MultiItemEntity>) data);
        this.context = context;
        imagePicker = ImagePicker.getInstance();
        mActivity = activity;
        mImageSize = Utils.getImageItemWidth(mActivity)-5;
        this.columns =columns;
        addItemType(TYPE_CLICKABLE, R.layout.atom_ui_release_circle_item);
        addItemType(TYPE_UNCLICKABLE, R.layout.atom_ui_release_circle_item);
        addItemType(TYPE_WORK_WORLD_ITEM, R.layout.atom_ui_release_circle_item);

    }



//    public ReleaseCircleGridAdapter(Activity activity) {
//        super(R.layout.atom_ui_release_circle_item);
//
//        addItemType(TYPE_LEVEL_0, R.layout.atom_ui_collection_user_item);
//        addItemType(TYPE_LEVEL_1, R.layout.atom_ui_rosteritem_collection);
//    }

    public void setOnStartDragListener(OnStartDragListener onStartDragListener) {
        this.onStartDragListener = onStartDragListener;
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        try {
            switch (helper.getItemViewType()) {
                case TYPE_CLICKABLE:
                    ImageItem data1 = (ImageItem) item;
                    helper.itemView.setLayoutParams(new AbsListView.LayoutParams(mImageSize, mImageSize));

                    imagePicker.getImageLoader().displayImage(mActivity, data1.getPath(), (ImageView) helper.getView(R.id.img_item), mImageSize, mImageSize); //显示图片
                    break;
                case TYPE_WORK_WORLD_ITEM:
                    ImageItem data_world = (ImageItem) item;
                    int size = Utils.getImageItemWidthForWorld(mActivity, columns);
                    helper.itemView.setLayoutParams(new LinearLayout.LayoutParams(size, size));
                    String url = data_world.getPath();
                    MessageUtils.ImageMsgParams params = new MessageUtils.ImageMsgParams();
                    params.sourceUrl = url;
                    MessageUtils.getDownloadFile(params,mActivity,false);

//                    MessageUtils.initImageUrl(params,mActivity,false);
//                    if (!(url.startsWith("http") || url.startsWith("https"))) {
//                        url = QtalkNavicationService.getInstance().getInnerFiltHttpHost() + "/" + url;
//                    }
//                    if(url.contains("?")){
//                        url+="&w="+size+"&h="+size;
//                    }else{
//                        url+="?w="+size+"&h="+size;
//                    }
//                imagePicker.getImageLoader().displayImage(mActivity, data_world.getPath(), (ImageView) helper.getView(R.id.img_item), mImageSize, mImageSize); //显示图片
                    ProfileUtils.displaySquareByImageSrc(mActivity, params.thumbUrl, (ImageView) helper.getView(R.id.img_item),
                            size, size);
                    break;
                case TYPE_UNCLICKABLE:
                    ReleaseCircleNoChangeItemDate data2 = (ReleaseCircleNoChangeItemDate) item;
                    helper.itemView.setLayoutParams(new AbsListView.LayoutParams(mImageSize, mImageSize));

                    helper.itemView.setTag("noChange");

                    ProfileUtils.displaySquareByImageSrc(mActivity, data2.getImgUrl(), (ImageView) helper.getView(R.id.img_item),
                            mImageSize, mImageSize);

//                imagePicker.getImageLoader().displayImage(mActivity, data2.getImgUrl(), (ImageView) helper.getView(R.id.img_item), mImageSize, mImageSize); //显示图片
                    break;
            }
        }catch (Exception e){

        }

//        helper.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                helper.itemView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mImageSize+5));
//                return false;
//            }
//        });
    }

    public interface OnStartDragListener {

        void onStartDrag(BaseViewHolder viewHolder);

    }
}
