# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\nesto\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
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

# butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
 
  #### -- Picasso --
  -dontwarn com.squareup.picasso.**
  
  # rxjava
  -keep class rx.schedulers.Schedulers {
      public static <methods>;
  }
  -keep class rx.schedulers.ImmediateScheduler {
      public <methods>;
  }
  -keep class rx.schedulers.TestScheduler {
      public <methods>;
  }
  -keep class rx.schedulers.Schedulers {
      public static ** test();
  }
  -keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
      long producerIndex;
      long consumerIndex;
  }
  -keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
      long producerNode;
      long consumerNode;
  }
  -dontwarn sun.misc.**
  
  #retrofit 
  -keep class com.google.gson.** { *; }
  -keep class com.google.inject.** { *; }
  -keep class org.apache.http.** { *; }
  -keep class org.apache.james.mime4j.** { *; }
  -keep class javax.inject.** { *; }
  -dontwarn retrofit2.**
  -keep class retrofit2.** { *; }
  -keepattributes Signature
  -keepattributes Exceptions

  -keepclasseswithmembers class * {
      @retrofit2.http.* <methods>;
  }
  
  
  #okhttp3
  -keepattributes Signature
  -keepattributes Annotation
  -keep class okhttp3.** { *; }
  -keep interface okhttp3.* { *; }
  -dontwarn okhttp3.*

-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,Annotation,EnclosingMethod,MethodParameters

-keep class **.R$* {*;}

-dontwarn okio.**
-dontwarn javax.annotation.**

-keep class nesto.gankio.model.** { *; }