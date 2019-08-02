package com.qunar.im.ui.schema;

public enum QIMSchemaEnum {
    /**
     * qtalkaphone://start_qtalk_activity //首页
     * qtalkaphone://rnsearch //搜索
     * qtalkaphone://router/openHome?tab=0 //消息
     * qtalkaphone://router/openHome?tab=1 //通讯录
     * qtalkaphone://router/openHome?tab=2 //日历
     * qtalkaphone://router/openHome?tab=3 //发现
     * qtalkaphone://router/openHome?tab=4 //我的
     *
     */
    openHome(QOpenHomeTabImpl.getInstance(),"/openHome");//打开home页某个tab


    QChatSchemaService qChatSchemaService;
    String path;
    QIMSchemaEnum(QChatSchemaService qChatSchemaService,String path){
        this.qChatSchemaService = qChatSchemaService;
        this.path = path;
    }

    public QChatSchemaService getqChatSchemaService() {
        return qChatSchemaService;
    }

    public String getPath() {
        return path;
    }

    public static QIMSchemaEnum getSchemeEnumByPath(String path){
        if(path == null)
            return null;
        for(QIMSchemaEnum e:QIMSchemaEnum.values()){
            if(e.path.equalsIgnoreCase(path)){
                return e;
            }
        }
        return null;
    }
}
