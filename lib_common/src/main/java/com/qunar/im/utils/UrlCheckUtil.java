package com.qunar.im.utils;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hubin on 2017/8/23.
 */

public class UrlCheckUtil {

    private static final Pattern[] ICON_PATTERNS = new Pattern[] {
            Pattern.compile( "rel=[\"']shortcut icon[\"'][^\r\n>]+?((?<=href=[\"']).+?(?=[\"']))" ),
            Pattern.compile( "((?<=href=[\"']).+?(?=[\"']))[^\r\n<]+?rel=[\"']shortcut icon[\"']" ) };
    private static final Pattern HEAD_END_PATTERN = Pattern.compile( "</head>" );


    public static String checkUrlForHttp(String host, String url) {
        if (TextUtils.isEmpty(url)) {
            return "";
        } else if (url.startsWith("http")) {
            return url;
        } else {
            return host + "/" + url;
        }

    }

    /**
     * 验证url
     * @param urlString
     * @return
     */
    private static String getFinalUrl( String urlString ) {
        HttpURLConnection connection = null;
        try {
            connection = getConnection( urlString );
            connection.connect();

            // 是否跳转，若跳转则跟踪到跳转页面
            if ( connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM
                    || connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP ) {
                String location = connection.getHeaderField( "Location" );
                if ( !location.contains( "http" ) ) {
                    location = urlString + "/" + location;
                }
                return location;
            }
        }
        catch ( Exception e ) {
            System.err.println( "获取跳转链接超时，返回原链接" + urlString );
        }
        finally {
            if ( connection != null )
                connection.disconnect();
        }
        return urlString;
    }

    /**
     * 获取Icon地址
     * @param urlString
     * @return
     * @throws MalformedURLException
     */
    public static String getIconUrlString(String urlString) {
        try {
//            urlString = exists(urlString);
            URL url = new URL(urlString);
            String iconUrl = url.getProtocol() + "://" + url.getHost() + "/favicon.ico";// 保证从域名根路径搜索
//            if (exists(iconUrl))
//                return iconUrl;
            return iconUrl;
//            return getIconUrlByRegex(urlString);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 判断在根目录下是否有Icon
     * @param urlString
     * @return
     */
    private static boolean hasRootIcon( String urlString ) {
        HttpURLConnection connection = null;

        try {
            connection = getConnection( urlString );
            connection.connect();
            return HttpURLConnection.HTTP_OK == connection.getResponseCode() && connection.getContentLength() > 0;
        }
        catch ( Exception e ) {
            e.printStackTrace();
            return false;
        }
        finally {
            if ( connection != null )
                connection.disconnect();
        }
    }

    private static boolean exists(String URLName){
        try {
            HttpURLConnection.setFollowRedirects(false);
            // note : you may also need
            //        HttpURLConnection.setInstanceFollowRedirects(false)
            HttpURLConnection con =
                    (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 从html中获取Icon地址
     * @param urlString
     * @return
     */
    private static String getIconUrlByRegex( String urlString ) {

        try {
            String headString = getHead( urlString );

            for ( Pattern iconPattern : ICON_PATTERNS ) {
                Matcher matcher = iconPattern.matcher( headString );

                if ( matcher.find() ) {
                    String iconUrl = matcher.group( 1 );
                    if ( iconUrl.contains( "http" ) )
                        return iconUrl;

                    if ( iconUrl.charAt( 0 ) == '/' ) {//判断是否为相对路径或根路径
                        URL url = new URL( urlString );
                        iconUrl = url.getProtocol() + "://" + url.getHost() + iconUrl;
                    }
                    else {
                        iconUrl = urlString + "/" + iconUrl;
                    }
                    return iconUrl;
                }
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取截止到head尾标签的文本
     * @param urlString
     * @return
     */
    private static String getHead( String urlString ) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            connection = getConnection( urlString );
            connection.connect();
            reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );

            String line = null;
            StringBuilder headBuilder = new StringBuilder();
            while ( ( line = reader.readLine() ) != null ) {
                Matcher matcher = HEAD_END_PATTERN.matcher( line );
                if ( matcher.find() )
                    break;
                headBuilder.append( line );
            }

            return headBuilder.toString();
        }
        catch ( Exception e ) {
            e.printStackTrace();
            return null;
        }
        finally {
            try {
                if ( reader != null )
                    reader.close();
                if ( connection != null )
                    connection.disconnect();
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取一个连接
     * @param urlString
     * @return
     * @throws IOException
     */
    private static HttpURLConnection getConnection( String urlString ) throws IOException {
        URL url = new URL( urlString );
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects( false );
        connection.setConnectTimeout( 3000 );
        connection.setReadTimeout( 3000 );
        connection
                .setRequestProperty( "User-Agent",
                        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.131 Safari/537.36" );
        return connection;
    }
}
