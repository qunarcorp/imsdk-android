package com.qunar.rn_service.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by wangyu.wang on 16/9/5.
 */
public class DownloadHelper {

    public static InputStream downloadByUrl(String urlDownload) throws IOException {
        URL url = new URL(urlDownload );
        URLConnection con = url.openConnection();
        InputStream is = con.getInputStream();

        return is;
    }
}
