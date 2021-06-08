package com.qunar.im.base.common;

/**
 * Created by xinbo.wang on 2017-01-09.
 */
public class ExceptionEvent {
    public Throwable throwable;
    public ExceptionEvent(Throwable throwable)
    {
        this.throwable = throwable;
    }
}
