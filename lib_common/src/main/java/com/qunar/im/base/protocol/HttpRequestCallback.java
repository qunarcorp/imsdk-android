package com.qunar.im.base.protocol;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by saber on 15-8-13.
 */
public interface HttpRequestCallback {
    void onComplete(InputStream response) throws IOException;
    void onFailure(Exception e);
}
