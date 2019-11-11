# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\adt\sdk/tools/proguard/proguard-android.txt
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
#*,!class/merging/*/
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

-dontwarn com.google.common.**
-dontwarn org.androidannotations.**
-dontwarn net.sqlcipher.**
-dontwarn com.amap.api.**
-dontwarn javax.annotation.**
-dontwarn com.google.zxing.**
-dontwarn com.facebook.**
-dontwarn com.squareup.**
-dontwarn android.support.v4.app.**
-dontwarn de.greenrobot.event.**
-dontwarn com.qunar.im.base.**

-keep class com.qunar.im.base.database.** {*;}


-keep public interface com.qunar.im.base.jsonbean.BaseResult$BaseData
-keep public class * extends android.app.Activity
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * implements com.android.vending.licensing.ILicensingService

-keep class android.support.v7.widget.ShareActionProvider { *;}

-keep class org.androidannotations.** { *; }
-keep class com.google.common.** { *; }
-keep class net.sqlcipher.** { *; }
-keep class sdk.** { *; }
-keep class com.qunar.im.base.module.** { *; }
-keep class com.qunar.im.base.jsonbean.** { *; }
-keep class com.qunar.im.base.statistics.bean.** { *; }
-keep class com.qunar.im.base.structs.** { *; }
-keepattributes InnerClasses

-keep class android.support.v4.app.** { *;}
-keep class android.support.v4.app.NotificationCompatGingerbread

#smack proguard
-keep class com.qunar.im.base.org.jivesoftware.smack.initializer.VmArgInitializer {*;}
-keep class com.qunar.im.base.org.jivesoftware.smack.ReconnectionManager {*;}
-keep class com.qunar.im.base.org.jivesoftware.smack.util.dns.javax.JavaxResolver {*;}
-keep class com.qunar.im.base.org.jivesoftware.smack.util.dns.minidns.MiniDnsResolver {*;}
-keep class com.qunar.im.base.org.jivesoftware.smack.util.dns.dnsjava.DNSJavaResolver {*;}
-keep class com.qunar.im.base.org.jivesoftware.smack.extensions.ExtensionsInitializer {*;}
-keep class com.qunar.im.base.org.jivesoftware.smack.experimental.ExperimentalInitializer {*;}
-keep class com.qunar.im.base.org.jivesoftware.smack.legacy.LegacyInitializer {*;}
-keep class com.qunar.im.base.org.jivesoftware.smack.tcp.TCPInitializer {*;}
-keep class com.qunar.im.base.org.jivesoftware.smack.sasl.javax.SASLJavaXSmackInitializer {*;}
-keep class com.qunar.im.base.org.jivesoftware.smack.sasl.provided.SASLProvidedSmackInitializer {*;}
-keep class com.qunar.im.base.org.jivesoftware.smack.android.AndroidSmackInitializer {*;}
-keep class com.qunar.im.base.org.jivesoftware.smack.java7.Java7SmackInitializer {*;}
-keep class com.qunar.im.base.org.jivesoftware.smack.im.SmackImInitializer {*;}
-keep class com.qunar.im.base.org.jivesoftware.smack.sm.provider.StreamManagementStreamFeatureProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.disco.ServiceDiscoveryManager {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.xhtmlim.XHTMLManager {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.muc.MultiUserChatManager {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamManager {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.bytestreams.socks5.Socks5BytestreamManager {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.filetransfer.FileTransferManager {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.iqlast.LastActivityManager {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.commands.AdHocCommandManager {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.ping.PingManager {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.privacy.PrivacyListManager {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.time.EntityTimeManager {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.vcardtemp.VCardManager {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.xdata.XDataManager {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.xdatalayout.XDataLayoutManager {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.xdatavalidation.XDataValidationManager {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.receipts.DeliveryReceiptManager {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.iqversion.VersionManager {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.caps.EntityCapsManager {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.rsm.provider.RSMSetProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.iqregister.provider.RegistrationStreamFeatureProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.iqregister.provider.RegistrationProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.jiveproperties.provider.JivePropertiesExtensionProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.amp.provider.AMPExtensionProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.privacy.provider.PrivacyProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.ping.provider.PingProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.forward.provider.ForwardedProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.caps.provider.CapsExtensionProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.receipts.DeliveryReceiptRequest$Provider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.receipts.DeliveryReceipt$Provider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.packet.AttentionExtension$Provider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.pubsub.provider.SimpleNodeProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.pubsub.provider.RetractEventProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.pubsub.provider.ItemProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.pubsub.provider.ItemsProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.pubsub.provider.SimpleNodeProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.pubsub.provider.ConfigEventProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.pubsub.provider.EventProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.pubsub.provider.SubscriptionProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.pubsub.provider.SubscriptionsProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.pubsub.provider.FormNodeProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.pubsub.provider.PubSubProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.pubsub.provider.AffiliationProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.pubsub.provider.AffiliationsProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.shim.provider.HeaderProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.shim.provider.HeadersProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.commands.provider.AdHocCommandDataProvider$SessionExpiredError {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.commands.provider.AdHocCommandDataProvider$BadSessionIDError {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.commands.provider.AdHocCommandDataProvider$BadPayloadError {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.commands.provider.AdHocCommandDataProvider$BadLocaleError {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.commands.provider.AdHocCommandDataProvider$MalformedActionError {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.commands.provider.AdHocCommandDataProvider$BadActionError {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.commands.provider.AdHocCommandDataProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.bytestreams.ibb.provider.DataPacketProvider$PacketExtensionProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.bytestreams.ibb.provider.CloseIQProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.bytestreams.ibb.provider.DataPacketProvider$IQProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.bytestreams.ibb.provider.OpenIQProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.si.provider.StreamInitiationProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.address.provider.MultipleAddressesProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.sharedgroups.packet.SharedGroupsInfo$Provider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.search.UserSearch$Provider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.iqlast.packet.LastActivity$Provider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.iqversion.provider.VersionProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.delay.provider.DelayInformationProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.delay.provider.LegacyDelayInformationProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.muc.provider.MUCOwnerProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.muc.provider.MUCAdminProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.muc.provider.MUCUserProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.xdata.provider.DataFormProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.disco.provider.DiscoverInfoProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.disco.provider.DiscoverItemsProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.muc.packet.GroupChatInvitation$Provider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.xhtmlim.provider.XHTMLExtensionProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.chatstates.packet.ChatStateExtension$Provider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.time.provider.TimeProvider {*;}
-keep class com.qunar.im.base.org.jivesoftware.smackx.iqprivate.PrivateDataManager$PrivateDataIQProvider {*;}

# See http://sourceforge.net/p/proguard/bugs/466/
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}

-keepnames class com.qunar.im.base.jsonbean.**$*{ *; }
-keepnames class com.qunar.im.base.statistics.bean.**$*{ *; }
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
   static final long serialVersionUID;
   private static final java.io.ObjectStreamField[] serialPersistentFields;
   !static !transient <fields>;
   private void writeObject(java.io.ObjectOutputStream);
   private void readObject(java.io.ObjectInputStream);
   java.lang.Object writeReplace();
   java.lang.Object readResolve();
}

-keep class android.support.v4.** {*;}

-keepattributes *Annotation*

-keep public class com.asqw.android{
    public void Start(java.lang.String);
}
# Keep native methods
-keepclassmembers class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

-keepclassmembers class * { public void onEvent*(...); }

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}
-keepattributes SourceFile,LineNumberTable

-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** w(...);
    public static *** v(...);
    public static *** i(...);
}

-assumenosideeffects class example.** {*;}

-keepclassmembers class * extends de.greenrobot.event.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}


-keepnames public class de.greenrobot.event.**{*;}

##---------------Begin: proguard configuration for Gson  ----------
# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keep class com.google.gson.** { *;}

##---------------End: proguard configuration for Gson  ----------

## okhttp begin ##
-dontwarn okio.**
-keep class okio.** {*;}
##vivo
-dontwarn com.vivo.push.**
-keep class com.vivo.push.**{*; }
-keep class com.vivo.vms.**{*; }
-keep class com.qunar.im.thirdpush.client.vivo.PushMessageReceiverImpl{*;}