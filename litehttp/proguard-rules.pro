# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/matianyu/develop/android/sdk/tools/proguard/proguard-android.txt
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
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 7
-allowaccessmodification
-dontpreverify

# The remainder of this file is identical to the non-optimized version
# of the Proguard configuration file (except that the other file has
# flags to turn off optimization).

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# 使用注解
-keepattributes *Annotation*,Signature,Exceptions

# 保持混淆时类的实名及行号(--------------- 调试时打开 --------------)
#-keepattributes SourceFile,LineNumberTable

# 开发者需要调用，不可以混淆
-keep public class com.litesuits.http.* { *; }
-keep public class com.litesuits.http.response.Response { *; }
-keep public class com.litesuits.http.utils.HttpUtil { *; }
-keep public class com.litesuits.http.annotation.* { *; }
-keep public class com.litesuits.http.concurrent.* { *; }
-keep public class com.litesuits.http.data.* { *; }
-keep public class com.litesuits.http.listener.* { *; }
-keep public class com.litesuits.http.log.* { *; }
-keep public class com.litesuits.http.parser.* { *; }
-keep public class com.litesuits.http.exception.** { *; }
-keep public class com.litesuits.http.request.** { *; }
# LiteHttp Http状态用了反射，不可混淆。
-keep class com.litesuits.http.data.HttpStatus { *; }
# http参数不可混淆
-keep public class * implements com.litesuits.http.request.param.HttpParamModel { *; }

# 枚举须保住 see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    **[] $VALUES;
    public *;
}

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keep class com.google.gson.** { *; }
# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }
##---------------End: proguard configuration for Gson  ----------

# HTTP混淆建议：
# 1. 最好保证每一个HTTP参数类（Java Model）不被混淆
# 2. 最好保证每一个HTTP响应类（Java Model）不被混淆