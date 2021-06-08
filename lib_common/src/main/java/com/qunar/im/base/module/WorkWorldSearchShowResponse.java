package com.qunar.im.base.module;

import java.util.List;

public class WorkWorldSearchShowResponse {


    /**
     * data : [{"commentUUID":"anim dolore consequat","content":"ut incididunt irure","createTime":3.099762472128433E7,"eventType":1.1442698770179763E7,"fromAnonymousName":"sed dolor","fromAnonymousPhoto":"dolor ex in minim","fromIsAnonymous":"quis deserunt laboris proident dolor","postUUID":"in esse nulla reprehenderit","toAnonymousName":"sint quis magna qui","toAnonymousPhoto":"culpa esse","toIsAnonymous":"minim","userFrom":"aliqua volup","userFromHost":"sit eiusmod voluptate","userTo":"exercitation qui dolor sed","userToHost":"Ut sit mollit"},{"commentUUID":"non in Ut magna","content":"anim pariatur do deserunt","createTime":6.866229945473602E7,"eventType":3.0427675619761497E7,"fromAnonymousName":"deserunt sit aute","fromAnonymousPhoto":"do consequat id","fromIsAnonymous":"velit reprehenderit in ut sunt","postUUID":"aliquip ea sed cupidatat Duis","toAnonymousName":"laboris Duis commodo","toAnonymousPhoto":"voluptate","toIsAnonymous":"dolor consectetur","userFrom":"exercitation est","userFromHost":"aliqua","userTo":"Excepteur culpa","userToHost":"deserunt tempor consectetur"},{"commentUUID":"eu ad","content":"nulla exercita","createTime":5.847311014517978E7,"eventType":-2.008385416340053E7,"fromAnonymousName":"nisi ipsum culpa","fromAnonymousPhoto":"esse","fromIsAnonymous":"ut","postUUID":"occaecat aliqua esse amet","toAnonymousName":"ullamco labore sunt nisi","toAnonymousPhoto":"ipsum","toIsAnonymous":"reprehenderit ullamco velit sed","userFrom":"voluptate aute labore","userFromHost":"adipisicing","userTo":"veniam tempor dolor","userToHost":"ulla"}]
     * errcode : 8.508089982814023E7
     * errmsg : ut elit in
     * ret : true
     */

    private double errcode;
    private String errmsg;
    private boolean ret;
    private List<WorkWorldAtShowItem> data;

    public double getErrcode() {
        return errcode;
    }

    public void setErrcode(double errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public boolean isRet() {
        return ret;
    }

    public void setRet(boolean ret) {
        this.ret = ret;
    }

    public List<WorkWorldAtShowItem> getData() {
        return data;
    }

    public void setData(List<WorkWorldAtShowItem> data) {
        this.data = data;
    }

}
