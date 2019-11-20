package com.qunar.im.base.structs;

public class WorkWorldItemState {

    public final static int normal = 0x01;
    public final static int top = normal<<1;
    public final static int hot = normal<<2;

    public final static int commentShow = 0x01;
    public final static int commentHidden = commentShow<<1;

}
