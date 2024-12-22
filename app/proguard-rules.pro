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

-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}
-keep @com.squareup.moshi.JsonQualifier interface *
-dontwarn org.jetbrains.annotations.**
-keep class kotlin.Metadata { *; }
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

-keepclassmembers class * {
    @com.squareup.moshi.FromJson <methods>;
    @com.squareup.moshi.ToJson <methods>;
}

-keep class com.skydoves.sandwich.** { *; }

-keep class kotlin** { *; }
-keepclassmembers class **  {
    @kotlin.Metadata * ;
}
-keepclassmembers class androidx.lifecycle.ViewModel {*; }
-keepnames class androidx.lifecycle.LiveData { *; }

-keep class kotlinx.coroutines.flow.** { *; }

-keepnames class com.plantcare.ai.identifier.plantid.data.network.** { *; }
-keepnames class com.plantcare.ai.identifier.plantid.data.database.** { *; }
-keepnames class com.plantcare.ai.identifier.plantid.domains.** { *; }
-keepnames class com.plantcare.ai.identifier.plantid.utils.network_adapter_factory.** { *; }

-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken
-keep public class * implements java.lang.reflect.Type