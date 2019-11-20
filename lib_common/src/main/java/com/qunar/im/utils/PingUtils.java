package com.qunar.im.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.Nullable;
import android.util.Log;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;

/**
 * Created by Lex lex on 2019-10-14.
 */
public class PingUtils {

    public static class Ping {
        public String net = "NO_CONNECTION";
        public String host;
        public String ip;
        public int dns = Integer.MAX_VALUE;
        public int cnt = Integer.MAX_VALUE;
    }
    /**
     * ping
     * @param pingNum ping的次数
     * @param url ping的目标URL
     * @return
     */
    public static boolean ping(int pingNum, String url) {
        int status = 0;
        Process p;
        try {
            p = Runtime.getRuntime().exec("/system/bin/ping -c " + pingNum + " " + url);
            status = p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (status == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static Ping ping(URL url, Context ctx) {
        Ping r = new Ping();
        if (isNetworkConnected(ctx)) {
            r.net = getNetworkType(ctx);
            try {
                String hostAddress;
                long start = System.currentTimeMillis();
                hostAddress = InetAddress.getByName(url.getHost()).getHostAddress();
                long dnsResolved = System.currentTimeMillis();
                Socket socket = new Socket(hostAddress, url.getPort());
                socket.close();
                long probeFinish = System.currentTimeMillis();
                r.dns = (int) (dnsResolved - start);
                r.cnt = (int) (probeFinish - dnsResolved);
                r.host = url.getHost();
                r.ip = hostAddress;
            }
            catch (Exception ex) {
                Logger.e("Unable to ping");
            }
        }
        return r;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Nullable
    public static String getNetworkType(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            return activeNetwork.getTypeName();
        }
        return null;
    }

    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }
    public static boolean isGoogleConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            try {
                URL url = new URL("http://www.google.com/");
                HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
                urlc.setRequestProperty("User-Agent", "test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1000); // mTimeout is in seconds
                urlc.setReadTimeout(1000);
                urlc.connect();

                Log.i("warning", "checking internet connection result " + urlc.getResponseCode());
                if (urlc.getResponseCode() == 200) {
                    return true;
                } else {
                    return false;
                }
            } catch (IOException e) {
                Log.i("warning", "Error checking internet connection", e);
                return false;
            }
        }

        return false;

    }
}
