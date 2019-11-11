/**
 * Copyright © 2013 Qunar.com Inc. All Rights Reserved.
 */
package com.qunar.im.rtc.scheme;

/**
 * jerry.li
 * 度假scheme跳转的枚举类
 */
public enum QchatSchemaEnum {
    webrtc(RTCSchemeImpl.instance,"/webrtc");


    private String path;
    private QChatSchemaService service;
    /**
     * 如果是startActivityForResult启动的，需要backToActivity的scheme需要在这个集合里
     */

    QchatSchemaEnum(QChatSchemaService service, String path) {
        this.service = service;
        this.path = path;
    }

    public String getPath(){
        return this.path;
    }

    public QChatSchemaService getService() {
        return service;
    }


    public static QchatSchemaEnum getSchemeEnumByPath(String path){
        if(path == null)
            return null;
        for(QchatSchemaEnum e:QchatSchemaEnum.values()){
            if(e.path.equalsIgnoreCase(path)){
                return e;
            }
        }
        return null;
    }

}
