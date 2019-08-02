package com.qunar.im.ui.view.chatExtFunc;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.qunar.im.ui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xingchao.song on 2/29/2016.
 */
public class ChatOperationsAdapter extends PagerAdapter {

    private GridView mGridView;
    private Context mContext;
    LinearLayout ll_inicatore;
    FuncMap funcMap;

    private static final int ITEM_PER_PAGE = 8;

    private String[] funcKeys;

    public ChatOperationsAdapter(Context context,FuncMap map){
        mContext = context;
        funcMap = map;
        funcKeys = new String[funcMap.getCount()];
        funcMap.getKeys().toArray(funcKeys);
    }
    @Override
    public int getCount() {
        if(funcMap.getCount()%8==0){
            return   funcMap.getCount()>>3;
        }else{
            return  (funcMap.getCount()>>3)+1;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.atom_ui_item_grid_operations, container,false);
        mGridView = view.findViewById(R.id.gv_options);
        ll_inicatore = view.findViewById(R.id.ll_inicatore);
        initGridView(position);
        container.addView(view);

        return view;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        if (object instanceof View) {
            container.removeView((View) object);
        }
    }

    private void initGridView(int position){
        List<FuncItem> thisPageData = new ArrayList<>();
        int nextPage = (position+1)*ITEM_PER_PAGE>=funcMap.getCount()?
                funcMap.getCount():(position+1)*ITEM_PER_PAGE;
        for(int i=position*ITEM_PER_PAGE;i<nextPage;i++)
        {
            String key = funcKeys[i];
            FuncItem item = funcMap.getItem(key);
            thisPageData.add(item);
        }
        GridFuncAdapter adapter = new GridFuncAdapter(mContext, thisPageData, R.layout.atom_ui_item_gridview_chat);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FuncItem item =(FuncItem)adapterView.getItemAtPosition(i);
                if(item!=null)
                {
                    FuncHanlder hanlder = item.hanlder;
                    if(hanlder!= null)
                    {
                        hanlder.handelClick();
                    }
                }
            }
        });
    }
}
