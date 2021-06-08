package com.qunar.im.base.callbacks;

/**
 * Created by xingchao.song on 10/13/2015.
 */
public interface  BasicCallback<T> {
        void onSuccess(T t);
        void onError();
}
