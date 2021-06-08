package com.qunar.im.other;

import com.qunar.im.protobuf.common.ProtoMessageOuterClass;

/**
 * Created by hubin on 2017/8/9.
 */

public interface IQGetVirtualUserRoleDelegate {
    void onVirtualUserResult(ProtoMessageOuterClass.IQMessage iqMessage, String err);
}
