package com.qunar.im.ui.view.baseView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.ui.R;
import com.qunar.im.ui.util.ResourceUtils;
import com.qunar.im.ui.view.medias.play.PlayVoiceView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by zhaokai on 15-8-19.
 * <p/>
 * <p/>
 * View Pool 用于生成View对象,如果当前View不需要被回收,请不要调用此类生成View对象,或者调用
 * view.setTag(ViewPool.TAG_VIEW_CANNOT_RECYCLE,new Object());
 * 即可
 */
public class ViewPool {
    public static final int TAG_VIEW_CANNOT_RECYCLE = 0xff;
    private static final String TAG = "ViewPool";
    private static final int MAX_SIZE = 15;
    private static final Map<Class<? extends View>, LinkedList<View>> usingMap = new HashMap<>();
    private static final Map<Class<? extends View>, LinkedList<View>> recycleMap = new HashMap<>(MAX_SIZE);
    private static final int l_r_padding_size = QunarIMApp.getContext().getResources().
            getDimensionPixelSize(R.dimen.atom_ui_chat_item_padding_l_r);
    private static final int t_b_padding_size = QunarIMApp.getContext().getResources().
            getDimensionPixelSize(R.dimen.atom_ui_chat_item_padding);
    private static final Drawable defaultImage = QunarIMApp.getContext().getResources().getDrawable(R.drawable.atom_ui_sharemore_picture);
    public static <T extends View> T getView(Class<T> type,Context context) {
        View v = null;
        if (type != null) {
            LinkedList<View> recycleQueue = recycleMap.get(type);
            LinkedList<View> usingList = usingMap.get(type);
            if (recycleQueue == null) {
                recycleQueue = new LinkedList<>();
                recycleMap.put(type, recycleQueue);
            }
            if (usingList == null) {
                usingList = new LinkedList<>();
                usingMap.put(type, usingList);
            }
            //如果队列中有该view 则赋值
            if (!recycleQueue.isEmpty()) {
                v = recycleQueue.remove(0);
            } else {
                //队列中没有该view 则创建
                try {
                    Constructor<T> constructor = type.getConstructor(Context.class);
                    v = constructor.newInstance(context);
                }catch (InstantiationException e) {
                    LogUtil.e(TAG, "instantiationException", e);
                } catch (IllegalAccessException e) {
                    LogUtil.e(TAG, "illegal access Exception", e);
                } catch (NoSuchMethodException e) {
                    LogUtil.e(TAG, "no such mechod exception");
                } catch (InvocationTargetException e) {
                    LogUtil.e(TAG, "invocation target exception");
                }
            }
            if (v != null) {
                Object o = v.getTag(TAG_VIEW_CANNOT_RECYCLE);
                if (o == null) {
                    v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    v.setBackground(null);
                    v.setOnClickListener(null);
                    if(v instanceof PlayVoiceView)
                    {
                        v.setPadding(l_r_padding_size, t_b_padding_size, l_r_padding_size, t_b_padding_size);
                        ((PlayVoiceView)v).onCreate();
                    }
                    else if (v instanceof TextView) {
                        int fontSizeMode = com.qunar.im.protobuf.common.CurrentPreference.getInstance().getFontSizeMode();
                        float fontSize=context.getResources().getDimensionPixelSize(R.dimen.atom_ui_text_size_medium);
                        switch (fontSizeMode)
                        {
                            case 1://small font size
                                fontSize=fontSize- ResourceUtils.getFontSizeIntervalPX(context);
                                break;
                            case 2://middle font size
                                break;
                            case 3://big font size
                                fontSize=fontSize+ResourceUtils.getFontSizeIntervalPX(context);
                                break;
                        }
                        ((TextView)v).setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
                        ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        ((TextView) v).setMovementMethod(LinkMovementMethod.getInstance());
                        ((TextView)v).setText(null);
//                        ((TextView)v).setMaxLines(128);
//                        ((TextView)v).setFilters(
//                                new InputFilter[]{new InputFilter.LengthFilter(2048)});
//                        ((TextView)v).setEllipsize(TextUtils.TruncateAt.END);
                        ((TextView)v).setTextColor(QunarIMApp.getContext().getResources().getColor(R.color.atom_ui_light_gray_33));
                        v.setPadding(l_r_padding_size, t_b_padding_size, l_r_padding_size, t_b_padding_size);
                    }
                    else if(v instanceof SimpleDraweeView)
                    {
                        GenericDraweeHierarchy hierarchy =  new GenericDraweeHierarchyBuilder(QunarIMApp.getContext().getResources())
                                .setActualImageScaleType(ScalingUtils.ScaleType.FOCUS_CROP)
                                .setRoundingParams(RoundingParams.fromCornersRadius(15f))
                                .setPlaceholderImage(defaultImage, ScalingUtils.ScaleType.FIT_CENTER)
                                .setFailureImage(defaultImage, ScalingUtils.ScaleType.FIT_CENTER)
                                .setFadeDuration(100)
                                .build();
                        ((SimpleDraweeView)v).setHierarchy(hierarchy);
                    }
                    else {
                        v.setPadding(0,0,0,0);
                    }
                    usingList.add(v);
                }
            }
            return (T) v;
        }
        return null;
    }

    public static void recycleView(View v) {
        Class type = v.getClass();
        if (v != null && type != null) {
            LinkedList<View> recycleQueue = recycleMap.get(type);
            LinkedList<View> usingList = usingMap.get(type);
            if (recycleQueue == null) {
                recycleQueue = new LinkedList<>();
                recycleMap.put(type, recycleQueue);
            }
            if (usingList == null) {
                usingList = new LinkedList<>();
                usingMap.put(type, usingList);
            }
            int index = usingList.indexOf(v);
            if (index != -1) {
                Object o = v.getTag(TAG_VIEW_CANNOT_RECYCLE);
                if (o == null) {
                    recycleQueue.add(v);
                    usingList.remove(v);
                }
            }
        }
    }

    public static void clear()
    {
        for(LinkedList<View> linkedList:usingMap.values())
        {
            for(View view:linkedList)
            {
                if(view instanceof IClearableView)
                {
                    ((IClearableView)view).clear();
                }
            }
        }
        for(LinkedList<View> linkedList:recycleMap.values())
        {
            for(View view:linkedList)
            {
                if(view instanceof IClearableView)
                {
                    ((IClearableView)view).clear();
                }
            }
        }
        recycleMap.clear();
        usingMap.clear();
    }
}