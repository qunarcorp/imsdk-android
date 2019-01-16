公有云（Startalk APP）
=====
基于Startalk服务器及客户端，用户可建立属于自己的域,

注册账号、新建域、添加域用户、下载客户端、配置域导航，

仅需5步，您就可以拥有强大的im能力，

客户端下载[下载](https://im.qunar.com/new/#/download)

- Android

[![Startalk on Android](https://s.qunarzz.com/qtalk_official_web/pages/download/android.png)](https://qt.qunar.com/downloads/qtalk_android.apk)

客户端导航配置[配置导航](https://im.qunar.com/new/#/platform/access_guide/manage_nav?id=manage_nav_mb)

私有云（Startalk SDK）
=====
Startalk私有云是一种去中心化的部署方式，

用户或企业将Startalk后端代码完全部署在自己的服务器上，

选择SDK嵌入自己的APP中，

每个公司都是一个单独的节点，每个节点独立运营，数据只保存在节点中，

下面是Android sdk的嵌入方式以及配置。

配置Gradle
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
  compile 'com.qunar.im:sdk-im:3.0.0' //或者直接引用imlib Library工程,compile project(':imlib')
}
```

配置manifestPlaceholders(如果自己的app不分渠道，可直接在buildTypes里配置manifestPlaceholders，反之需在每个Flavor下配置manifestPlaceholders)
--------

```manifestPlaceholders
flavorDimensions "qim"
    //分渠道
    productFlavors {
        //QTalk
        qtalk {
            dimension "qim"

            manifestPlaceholders = [
                    PACKAGE_NAME : "sdk.im.qunar.com.qtalksdkdemo",
                    serverDoMain  : true,
                    SCHEME : "qtalkaphone",
                    currentPlat  : "QTalk",
                    MAIN_SCHEMA : "start_qtalk_activity"
            ]
        }
        // QChat
        qchat {
            dimension "qim"

            manifestPlaceholders = [
                    PACKAGE_NAME : "sdk.im.qunar.com.qtalksdkdemo",
                    serverDoMain  : false,
                    SCHEME : "qchataphone",
                    currentPlat  : "QChat",
                    MAIN_SCHEMA : "start_qchat_activity",
            ]
        }

    }
```
主工程Manifest配置
--------

```
参照app下的AndroidManifest配置
```
如何使用(主要接口)
--------
首先需要对sdk进行初始化操作，之后配置导航Url，然后进行登录。
 ```init
  1.初始化SDK
  
  QIMSdk.getInstance().init(Application application)
  ```
 ```config
  2.配置导航地址
  
  QIMSdk.getInstance().setNavigationUrl(String url)
  ```  
 ```login
  3.用户名密码登录
  
  QIMSdk.getInstance().login(String uid,String password,LoginStatesListener loginStatesListener)
  ```   
 ```Autologin
  4.自动登录(本地缓存用户之前登录的用户名、token后可自动登录)
  
  QIMSdk.getInstance().autoLogin(LoginStatesListener loginStatesListener)
  ```   
 ```logout
  5.登出
  
  QIMSdk.getInstance().signOut()
  ```   
  其他参考[api.md](doc/api.md)
  
Scheme Support
--------
参考文档[scheme文档](https://github.com/qunarcorp/imsdk-android/wiki/Scheme-Support)

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
 
意见反馈
=====
-   qchat@qunar.com（Email）