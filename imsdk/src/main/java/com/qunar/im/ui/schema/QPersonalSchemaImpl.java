package com.qunar.im.ui.schema;

import android.content.Intent;
import android.text.TextUtils;

import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.PersonalInfoActivity;

import java.util.Map;

/**
 * Created by xinbo.wang on 2016-09-22.
 */
public class QPersonalSchemaImpl implements QChatSchemaService {
    public final static QPersonalSchemaImpl instance = new QPersonalSchemaImpl();
    //这个方法后面都要返回false , 否则会出现白屏
    @Override
    public boolean startActivityAndNeedWating(final IMBaseActivity context, Map<String, String> map) {
        final Intent intent = new Intent(context.getApplication(),PersonalInfoActivity.class);
        //判断是否有jid
        String jid = map.get("jid");
        if (TextUtils.isEmpty(jid)){
            return false;
        }
        //当有jid的情况下,要根据jid去匹配当前热线账号列表
        //如果是热线账号列表,需要根据热线去获取背后的realJid
//        Logger.i("获取到的热线服务人员:"+str);
        intent.putExtra("jid",jid);
//        intent.putExtra("realUser",nick.getXmppId());
//        intent.putExtra("hotLine",nick.getHotLine());


        context.startActivity(intent);
        return false;
        //这一套都是tmd什么鬼 全部注释了
        //获取当前热线中值班的真实用户
//        IQStartSession iqStartSession = new IQStartSession(IQStartSessionProvider.ELEMENT_NAME_Q, IQStartSessionProvider.NAMESPAE);
//        //拼接用户名
//        iqStartSession.setTo(CurrentPreference.getInstance().getUserid() + "@" + QunarIMApp.getQunarIMApp().getConfiguration().getDomain());
//        if(map.containsKey(PersonalInfoActivity.JID))
//        {
//            intent.putExtra(PersonalInfoActivity.JID,map.get(PersonalInfoActivity.JID));
//            iqStartSession.setJid(QtalkStringUtils.parseLocalpart(map.get(PersonalInfoActivity.JID)));
//        }
//        if(map.containsKey(PersonalInfoActivity.IS_HIDE_BTN))
//        {
//            String isHideBtn = map.get(PersonalInfoActivity.IS_HIDE_BTN);
//            if(!TextUtils.isEmpty(isHideBtn)) {
//                intent.putExtra(PersonalInfoActivity.IS_HIDE_BTN,
//                        isHideBtn.equals("true"));
//            }
//        }
//        if (!TextUtils.isEmpty(iqStartSession.getJid())){
//            iqStartSession.setType(IQ.Type.get);
//            IMLogic.instance().sendIQMsg(iqStartSession, new StanzaListener() {
//                @Override
//                public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
//                    String realUser = null;
//                    IQStartSession iq = (IQStartSession) packet;
//                    //为Null判断为非热线帐号
//                    if (iq.getReal_user().equals("null")) {
//                        CurrentPreference.getInstance().setItConnection("null");
//                        CurrentPreference.getInstance().savePreference();
//                    } else {
//                        //获取并拼接真实用户
//                        realUser = iq.getReal_user() + "@" + QunarIMApp.getQunarIMApp().getConfiguration().getDomain();
//                        CurrentPreference.getInstance().setItConnection(realUser);
//                        CurrentPreference.getInstance().savePreference();
//                    }
//                    intent.putExtra("realUser", realUser);
//                    context.startActivity(intent);
//                }
//            }, new ExceptionCallback() {
//                @Override
//                public void processException(Exception e) {
//                    LogUtil.e("QPersonalSchemaImpl", "error", e);
//                    context.startActivity(intent);
//                }
//            });
//        }else {
//            context.startActivity(intent);
//        }

    }
}
