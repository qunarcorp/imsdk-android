package com.qunar.im.ui.schema;

import android.content.Intent;
import android.text.TextUtils;

import com.qunar.im.base.util.Constants;
import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.SearchUserActivity;

import java.util.Map;

/**
 * Created by xinbo.wang on 2016-09-22.
 */
public class QSearchSchemaImpl implements QChatSchemaService {
    public static final QSearchSchemaImpl instance = new QSearchSchemaImpl();
    @Override
    public boolean startActivityAndNeedWating(IMBaseActivity context, Map<String, String> map) {
        Intent intent = new Intent(context.getApplication(),SearchUserActivity.class);
        if(map.containsKey(SearchUserActivity.SEARCH_SCOPE))
        {
            int scope;
            try {
                scope = Integer.parseInt(map.get(SearchUserActivity.SEARCH_SCOPE));
            }
            catch (Exception ex)
            {
                scope = 7;
            }
            intent.putExtra(SearchUserActivity.SEARCH_SCOPE,scope);
        }
        if(map.containsKey(SearchUserActivity.SEARCH_TERM))
        {
            intent.putExtra(SearchUserActivity.SEARCH_TERM,map.get(SearchUserActivity.SEARCH_TERM));
        }
        if(map.containsKey(Constants.BundleKey.IS_FROM_SHARE))
        {
            String isFromShare = map.get(Constants.BundleKey.IS_FROM_SHARE);
            if(!TextUtils.isEmpty(isFromShare))
            {
                intent.putExtra(Constants.BundleKey.IS_FROM_SHARE,isFromShare.equals("true"));
            }
        }
        context.startActivity(intent);
        return false;
    }
}
