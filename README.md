QTalk IM SDK
=====
Qtalk是一款企业级im工具,由去哪儿网自主研发，在内部稳定运行3年多，同时为去哪儿网上万商家提供售前及售后咨询。Qtalk基本支持所有的消息类型，如：文本、表情、文件、音视频、图片、位置、红包、代码……
支持全平台接入，iOS、安卓、Windows、Mac、Linux。

Gradle
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
  compile 'com.qunar.im:sdk-im:2.0.0' //或者直接引用imlib Library工程,compile project(':imlib')
}
```

manifestPlaceholders
--------

```manifestPlaceholders
buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'

            manifestPlaceholders = [
                    PACKAGE_NAME:"your application id",
                    serverDoMain: true,
                    SCHEME      : "qtalkaphone",
                    currentPlat : "QTalk",
                    MAIN_SCHEMA : "start_qtalk_activity"
            ]
        }
        debug {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'

            manifestPlaceholders = [
                    PACKAGE_NAME:"your application id",
                    serverDoMain: true,
                    SCHEME      : "qchataphone",
                    currentPlat : "QChat",
                    MAIN_SCHEMA : "start_qchat_activity"
            ]
        }

    }
```
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
 
 开始使用(主要api)
 -------------
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
## 意见反馈

-   qchat@qunar.com（Email）