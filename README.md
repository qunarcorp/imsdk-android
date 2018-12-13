QTalk IM SDK
=====
提供im通讯能力，包含单聊、群聊，通知推送。支持发送文本、图片、表情、语音、文件、地理位置….支持音视频通话。

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
## 问题反馈

-   qchat@qunar.com（邮件）