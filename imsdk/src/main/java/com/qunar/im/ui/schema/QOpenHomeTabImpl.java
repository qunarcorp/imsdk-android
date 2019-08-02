package com.qunar.im.ui.schema;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.qunar.im.base.util.Constants;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.TabMainActivity;

import java.util.HashMap;
import java.util.Map;

public class QOpenHomeTabImpl implements QChatSchemaService{
    private  QOpenHomeTabImpl(){

    }
    @Override
    public boolean startActivityAndNeedWating(IMBaseActivity context, Map<String, String> map) {
        if(map != null){
            String value = map.get("tab");
            if(!TextUtils.isEmpty(value)){
                Intent intent = new Intent(context, TabMainActivity.class);
                intent.putExtra(Constants.BundleKey.HOME_TAB,value);
                context.startActivity(intent);
            }
        }
        return false;
    }

    private static class LazyHolder{
        private static final QOpenHomeTabImpl INSTANCE = new QOpenHomeTabImpl();
    }

    public static QOpenHomeTabImpl getInstance(){
        return LazyHolder.INSTANCE;
    }

    public int getTabIndex(Context context,String key,String[] tabs){
        Map<String,String> map = new HashMap<>();
        map.put("message",context.getString(R.string.atom_ui_tab_message));
        map.put("trip",context.getString(R.string.atom_ui_tab_trip));
        map.put("contact",context.getString(R.string.atom_ui_tab_contacts));
        map.put("discovery",context.getString(R.string.atom_ui_tab_title_contact));
        map.put("mine",context.getString(R.string.atom_ui_tab_mine));

        String s = map.get(key);
        if(TextUtils.isEmpty(s)){
            return 0;
        }else {
            if(tabs == null || tabs.length == 0){
                return 0;
            }else {
                for(int i = 0;i<tabs.length;i++){
                    if(s.equals(tabs[i])){
                        return i;
                    }
                }
            }
        }

        return 0;
    }
}
