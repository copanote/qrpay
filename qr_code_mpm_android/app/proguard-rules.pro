# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-dontnote
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#-dontoptimize


-keepattributes SourceFile,LineNumberTable


#-keep public class * extends android.support.v4.app.FragmentActivity
#-keep public class * extends android.app.Activity
#-keep public class * extends android.app.Application
#-keep public class * extends android.app.Service
#-keep public class * extends android.content.BroadcastReceiver
#-keep public class * extends android.content.ContentProvider
#-keep public class * extends android.app.backup.BackupAgentHelper
#-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-dontnote com.android.vending.licensing.ILicensingService


# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}


-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# Solution??
-keep class com.initech.** { *; }
-keep class com.nshc.** { *; }
-keep class net.nshc.** { *; }
-keep class android.support.** { *; }
-keep class com.antivirus.** { *; }
-keep class org.bouncycastle.** { *; }
#-keep class com.bccard.mobilecard.** { *; }
#-keep class com.bccard.bcsmartapp.activity.web.** { *; }
#-keep class com.bccard.bcsmartapp.activity.web.KCPPayBridge.** { *; }
-keep class com.bccard.mpm.network.bean.** { *; }

#-kepp public class * extends com.initech.android.sfilter.client.SHTTPApplication

-dontwarn com.initech.**
-dontwarn com.nshc.**
-dontwarn net.nshc.**
#-dontwarn org.krysalis.barcode4j.**
-dontwarn android.support.**
-dontwarn com.antivirus.**
-dontwarn org.bouncycastle.**
#-dontwarn com.bccard.mobilecard.**
#-dontwarn com.bccard.bcsmartapp.activity.web.**
#-dontwarn com.bccard.bcsmartapp.activity.web.KCPPayBridge.**
-dontwarn android.support.v4.**
-dontwarn com.google.android.gms.**
-dontwarn com.google.android.gms.cast.**
-dontwarn com.google.android.gms.dynamic.**
-dontwarn com.google.android.gms.games.internal.**
-dontwarn com.google.android.gms.internal.**
-dontwarn kr.co.deotis.wiseportal.library.template.**

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

##---------------End: proguard configuration for Gson  ----------
