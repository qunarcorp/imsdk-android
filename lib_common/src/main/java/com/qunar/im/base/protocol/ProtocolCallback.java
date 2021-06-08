package com.qunar.im.base.protocol;

public class ProtocolCallback {
    public static abstract class IProtocolCallback {
        volatile boolean isCancel = false;

        public void doFailure() {
            onFailure("");
        }


        public void cancel() {
            isCancel = true;
        }

        public abstract void onFailure(String errMsg);
    }




    public static abstract class UnitCallback<T> extends IProtocolCallback {
        public UnitCallback(){super();}
        public abstract void onCompleted(final T t);
    }
}
