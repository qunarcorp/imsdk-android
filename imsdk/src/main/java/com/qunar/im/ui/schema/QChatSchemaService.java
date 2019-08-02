/**
 * Copyright © 2013 Qunar.com Inc. All Rights Reserved.
 */
package com.qunar.im.ui.schema;

import com.qunar.im.ui.activity.IMBaseActivity;

import java.util.Map;

/**
 * Schema接口类
 * @author jerry
 * @since 2013-12-30上午10:58:01
 */
public interface QChatSchemaService {

    boolean startActivityAndNeedWating(IMBaseActivity context, Map<String, String> map);

}
