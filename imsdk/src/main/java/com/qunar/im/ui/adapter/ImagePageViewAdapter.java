package com.qunar.im.ui.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.request.ImageRequest;
import com.qunar.im.ui.presenter.views.IBrowsingConversationImageView;
import com.qunar.im.ui.view.facebookimageview.zoomable.ZoomableDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saber on 16-1-29.
 */
public class ImagePageViewAdapter extends PagerAdapter {
    List<IBrowsingConversationImageView.PreImage> datas = new ArrayList<>();
    SparseArray<View> viewSparseArray = new SparseArray<>();
    Context context;
    private View.OnTouchListener onTouchListener;
    private View.OnClickListener onClickListener;

    public void setListener(View.OnCreateContextMenuListener listener) {
        this.listener = listener;
    }

    private View.OnCreateContextMenuListener listener = null;

    public ImagePageViewAdapter(Context context){
        this.context = context;
    }

    public void setDatas(List<IBrowsingConversationImageView.PreImage> imgs)
    {
        datas = imgs;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container,int position,Object object)
    {
        if(viewSparseArray.indexOfKey(position)>-1)
        {
            container.removeView(viewSparseArray.get(position));
            viewSparseArray.delete(position);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container,int position)
    {
        IBrowsingConversationImageView.PreImage data = datas.get(position);
        ZoomableDraweeView mImageView = new ZoomableDraweeView(context);
        final DraweeController ctrl = Fresco.newDraweeControllerBuilder()
                .setImageRequest(ImageRequest.fromUri(data.originUrl))
                .setLowResImageRequest(ImageRequest.fromUri(data.smallUrl))
                .setTapToRetryEnabled(false).setAutoPlayAnimations(true).build();

        GenericDraweeHierarchyBuilder hierarchyBuilder = new GenericDraweeHierarchyBuilder(
                context.getResources())
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .setProgressBarImage(new ProgressBarDrawable());

        mImageView.setHierarchy(hierarchyBuilder.build());
        mImageView.setController(ctrl);
        mImageView.setOnCreateContextMenuListener(listener);
//        mImageView.setOnClickListener(onClickListener);
        mImageView.setOnTouchListener(onTouchListener);
        viewSparseArray.append(position,mImageView);
        container.addView(mImageView);
        return mImageView;
    }

    public void setOnTouchListener(View.OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}