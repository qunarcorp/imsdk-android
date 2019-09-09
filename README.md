Startalk, The Best open sourced instant messenger software in the world!
* [Chinese Version(中文版)](https://github.com/qunarcorp/imsdk-android/blob/master/README_zh_CN.md)

Public Cloud(Startalk App)
=====
Based on Startalk server and client-side, users can build their own domain,
Sign up an account, create new domains, add users, download client app, and configure navigation for domain,
After the 5 steps above, you own strong IM abilities.

Download client app[Download](https://im.qunar.com/new/#/download)

- Android

[![Startalk on Android](https://s.qunarzz.com/qtalk_official_web/pages/download/android.png)](https://qt.qunar.com/downloads/qtalk_android.apk)

Configure navigation for client app[Configure navigation](https://im.qunar.com/new/#/platform/access_guide/manage_nav?id=manage_nav_mb)

Private Cloud(Startalk SDK)
=====
Private Cloud is a way for decentralized deployment. Customers or enterprises would deploy the back end code on their own servers, embedding SDK into their own app. Every enterprise is an independent node; every node works independently, and the data would only be saved in the node.  

Please see the guide of embedding Android SDK and the configuration below.

Configure Gradle
--------
```gradle
buildscript {
    repositories {
        jcenter()
        google()
    }
}
 
allprojects {
    repositories {
        maven {
            url "http://qt.qunar.com/package/mvn/repository/maven-qim/"
        }
        
        maven {
            url "http://developer.huawei.com/repo/"
        }
    mavenCentral()
        jcenter {
           url "http://jcenter.bintray.com/"
        }
        jcenter()
        google()
    }
}
```

```gradle
dependencies {
  compile 'com.qunar.im:sdk-im:3.0.5' //Or quote the imsdk Library directly,compile project(':imsdk')
}
```

Deploy manifestPlaceholders (If your own app can be deployed by any channel, please deploymanifestPlaceholders in buildTypes, or you need to deploy manifestPlaceholders in every flavor)
--------

```manifestPlaceholders
flavorDimensions "qim"
    //Mutiple channels
    productFlavors {
        //startalk
        startalk {
            dimension "qim"

            manifestPlaceholders = [
                    PACKAGE_NAME : "sdk.im.qunar.com.qtalksdkdemo",//Replace it with the application ID of your own project
                    serverDoMain  : true,
                    baiduMap :"xxxxx",//key of Baidu map (for sending location)
                    HUAWEI_APPID : "123",//HUAWEI push
                    OPPO_APP_ID : "123",//OPPO push
                    OPPO_APP_KEY : "123",
                    OPPO_APP_SECRET : "123",
                    MIPUSH_APP_ID : "123",//XIAOMI push
                    MIPUSH_APP_KEY : "123",
                    MEIZU_APP_ID : "123",//MEIZU push
                    MEIZU_APP_KEY : "123",
                    SCHEME : "qtalkaphone",
                    currentPlat  : "QTalk",
                    MAIN_SCHEMA : "start_qtalk_activity"
            ]
        }
        // QChat
        qchat {
            dimension "qim"

            manifestPlaceholders = [
                    PACKAGE_NAME : "sdk.im.qunar.com.qtalksdkdemo",//Replace it with the application ID of your own project
                    serverDoMain  : false,
                    baiduMap :"xxxxx",//key of Baidu map (for sending location)
                    HUAWEI_APPID : "123",//HUAWEI push
                    OPPO_APP_ID : "123",//OPPO push
                    OPPO_APP_KEY : "123",
                    OPPO_APP_SECRET : "123",
                    MIPUSH_APP_ID : "123",//xiaomi push
                    MIPUSH_APP_KEY : "123",
                    MEIZU_APP_ID : "123",//meizu push
                    MEIZU_APP_KEY : "123",
                    SCHEME : "qchataphone",
                    currentPlat  : "QChat",
                    MAIN_SCHEMA : "start_qchat_activity",
            ]
        }

    }
```
Configure Manifest of main project
--------

```
Please see the AndroidManifest configuration in app
```
How to Use (main ports)
--------
First, please initialize SDK. Then configure the navigation Url and log in。
 ```init
  1.Initialize SDK
  
  QIMSdk.getInstance().init(Application application)
  ```
 ```config
  2.Configure navigation Ur
  
  QIMSdk.getInstance().setNavigationUrl(String url)
  ```  
 ```login
  3.Log in with username and password
  
  QIMSdk.getInstance().login(String uid,String password,LoginStatesListener loginStatesListener)
  ```   
 ```Autologin
  4.Auto login (save usernames in the local cache to achieve auto login after token
  
  QIMSdk.getInstance().autoLogin(LoginStatesListener loginStatesListener)
  ```   
 ```logout
  5.log out
  
  QIMSdk.getInstance().signOut()
  ```
 ```debug
  6.Turn on debug mode

  QIMSdk.getInstance().openDebug()
  ```
  other api[api.md](doc/api.md)

  
Scheme Support
--------
Please see scheme[scheme doc](https://github.com/qunarcorp/imsdk-android/wiki/Scheme-Support)

ProGuard
--------
Depending on your ProGuard (DexGuard) config and usage, you may need to include the following lines in your proguard.cfg

```pro
-dontwarn com.mqunar.**
-keep class com.mqunar.**{*;}
-dontwarn com.qunar.**
-keep class com.qunar.**{*;}
```

Compatibility
-------------

 * **Minimum Android SDK**: QTalk SDK requires a minimum API level of 16.
 * **Compile Android SDK**: QTalk SDK requires you to compile against API 26 or later.
 
Frequent error
--------
Add android.enableAapt2=false in “gradle.properties”
```error1
error:style attribute '@android:attr/windowEnterAnimation' not found.
Message{kind=ERROR, text=error: style attribute '@android:attr/windowEnterAnimation' not found.
```
Add style below in “values styles.xml” in the main project
```style
<style name="SplashTheme" parent="AppTheme">
   <item name="android:windowIsTranslucent">true</item>
</style>
```

```error2
﻿error:No resource found that matches the given name (at 'theme' with value '@style/SplashTheme').
```


Feedback
=====
-   qchat@qunar.com（Email）