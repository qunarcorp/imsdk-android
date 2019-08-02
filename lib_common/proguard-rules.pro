# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-keepattributes Signature
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keeppackagenames
-dontshrink

-dontwarn com.google.zxing.**
-dontwarn com.facebook.**
-dontwarn com.squareup.**
-dontwarn android.support.v4.app.**
-dontwarn android.support.v7.app.**

## okhttp begin ##
-dontwarn okio.**
-keep class okio.** {*;}
-keep class com.squareup.okhttp3.** {*;}
-keep class org.androidannotations.** { *; }
-keep class com.google.common.** { *; }
-keep class sdk.** { *; }
-keepattributes InnerClasses

# See http://sourceforge.net/p/proguard/bugs/466/
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keep class com.google.gson.** { *;}


-keepattributes *Annotation*

-keep public class com.asqw.android{
    public void Start(java.lang.String);
}

#-keepnames public class com.qunar.im.**{
#    public <fields>;
#    public <methods>;
#    public static <fields>;
#    public static <methods>;
#}
#
#-keepnames class com.qunar.im.**$*{
#    public <fields>;
#    public <methods>;
#    public static <fields>;
#    public static <methods>;
#}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep native methods
-keepclassmembers class * {
    native <methods>;
}

-keepclassmembers class * implements java.io.Serializable {
   static final long serialVersionUID;
   private static final java.io.ObjectStreamField[] serialPersistentFields;
   !static !transient <fields>;
   private void writeObject(java.io.ObjectOutputStream);
   private void readObject(java.io.ObjectInputStream);
   java.lang.Object writeReplace();
   java.lang.Object readResolve();
}
