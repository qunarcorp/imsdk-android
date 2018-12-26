package com.qunar.im.ui.view.chatExtFunc;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.qunar.im.ui.R;
import com.qunar.im.base.util.Utils;
import com.qunar.im.ui.view.faceGridView.DotFile;

/**
 * Created by xingchao.song on 2/29/2016.
 */
public class OperationView extends RelativeLayout {
    private Context mContext;
    private ViewPager vp_oprations;
    private LinearLayout ll_inicatore;
    private ImageView[] imageViews;
    ChatOperationsAdapter mChatOperationsAdapter;
    public OperationView(Context context) {
        super(context,null);
    }

    public OperationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OperationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public void init(Context context,FuncMap map){
        mContext = context;
        vp_oprations = this.findViewById(R.id.vp_oprations);
        ll_inicatore = this.findViewById(R.id.ll_inicatore);
        mChatOperationsAdapter  = new ChatOperationsAdapter(context,map);
        vp_oprations.setAdapter(mChatOperationsAdapter);
        initIndicator(0);
        vp_oprations.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                initIndicator(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    public void initIndicator(int index){
        ll_inicatore.removeAllViews();
        int viewPagerPageSize =  mChatOperationsAdapter.getCount();

        if (viewPagerPageSize > 0) {
            if (viewPagerPageSize == 1) {
                ll_inicatore.setVisibility(View.GONE);
            } else {
                ll_inicatore.setVisibility(View.VISIBLE);
                imageViews = new ImageView[viewPagerPageSize];
                int dotFileHeight = Utils.dipToPixels(mContext, 8);
                int dotFileWidth = Utils.dipToPixels(mContext, 8);
                int dotMargin = Utils.dipToPixels(mContext, 4);
                for (int i = 0; i < viewPagerPageSize; i++) {
                    ImageView image = new ImageView(mContext);
                    image.setTag(i);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dotFileWidth, dotFileHeight);
                    params.setMargins(dotMargin, dotMargin, dotMargin, dotMargin);
                    image.setBackgroundDrawable(new DotFile(mContext).setStateDrawable());
                    image.setEnabled(false);
                    ll_inicatore.addView(image, params);
                    imageViews[i] = image;
                }
                imageViews[index].setEnabled(true);
            }
        }
    }

}
