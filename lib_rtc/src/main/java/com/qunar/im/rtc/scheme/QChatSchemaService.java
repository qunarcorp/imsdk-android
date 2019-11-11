/**
 * Copyright © 2013 Qunar.com Inc. All Rights Reserved.
 */
package com.qunar.im.rtc.scheme;

import android.support.v7.app.AppCompatActivity;

import java.util.Map;

/**
 * Schema接口类
 * @author jerry
 * @since 2013-12-30上午10:58:01
 */
public interface QChatSchemaService {

    boolean startActivityAndNeedWating(AppCompatActivity context, Map<String, String> map);

}
