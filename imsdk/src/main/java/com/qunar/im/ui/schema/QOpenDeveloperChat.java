package com.qunar.im.ui.schema;

import android.content.Intent;
import android.widget.Toast;

import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.core.utils.GlobalConfigManager;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.PbChatActivity;

import java.util.Map;
import java.util.Random;

/**
 * Created by hubin on 2018/4/9.
 */

public class QOpenDeveloperChat implements QChatSchemaService {
    public final static QOpenDeveloperChat instance = new QOpenDeveloperChat();

    private final static String[] devs = new String[]{"hubin.hu@" + QtalkNavicationService.getInstance().getXmppdomain(), "hubo.hu@" + QtalkNavicationService.getInstance().getXmppdomain(), "lihaibin.li@" + QtalkNavicationService.getInstance().getXmppdomain()};//ejabhost1

    //这个方法后面都要返回false , 否则会出现白屏
    @Override
    public boolean startActivityAndNeedWating(final IMBaseActivity context, Map<String, String> map) {

        if(GlobalConfigManager.isQchatPlat()){
            Intent intent = new Intent(context.getApplication(), PbChatActivity.class);
            intent.putExtra("jid", "qcxjfu@ejabhost1");
            intent.putExtra("isFromChatRoom", false);
            context.startActivity(intent);
        }else if(GlobalConfigManager.isQtalkPlat()){
            Random random = new Random();
            int r = random.nextInt(devs.length);
            if (r < 0) {
                r = 0;
            } else if (r >= devs.length) {
                r = devs.length - 1;
            }
            Intent intent = new Intent(context.getApplication(), PbChatActivity.class);
            String jid = devs[r];
            intent.putExtra("jid", jid);
            intent.putExtra("isFromChatRoom", false);
            context.startActivity(intent);
        }else {
            Toast.makeText(context,context.getString(R.string.atom_ui_look_forward),Toast.LENGTH_SHORT).show();
        }

        return false;
    }
}