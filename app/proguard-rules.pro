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
-dontwarn javax.lang.model.SourceVersion
-dontwarn javax.lang.model.element.Element
-dontwarn javax.lang.model.element.ElementKind
-dontwarn javax.lang.model.element.ElementVisitor
-dontwarn javax.lang.model.element.ExecutableElement
-dontwarn javax.lang.model.element.Modifier
-dontwarn javax.lang.model.element.Name
-dontwarn javax.lang.model.element.PackageElement
-dontwarn javax.lang.model.element.TypeElement
-dontwarn javax.lang.model.element.TypeParameterElement
-dontwarn javax.lang.model.element.VariableElement
-dontwarn javax.lang.model.type.ArrayType
-dontwarn javax.lang.model.type.DeclaredType
-dontwarn javax.lang.model.type.ExecutableType
-dontwarn javax.lang.model.type.TypeKind
-dontwarn javax.lang.model.type.TypeMirror
-dontwarn javax.lang.model.type.TypeVariable
-dontwarn javax.lang.model.type.TypeVisitor
-dontwarn javax.lang.model.util.ElementFilter
-dontwarn javax.lang.model.util.SimpleElementVisitor8
-dontwarn javax.lang.model.util.SimpleTypeVisitor8
-dontwarn javax.lang.model.util.Types
-dontwarn sun.misc.**
-dontwarn org.xmlpull.v1.**
-dontwarn org.kxml2.io.**
-dontwarn android.content.res.**
-dontwarn org.slf4j.impl.StaticLoggerBinder

-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-keep,allowobfuscation,allowshrinking class indi.dmzz_yyhyy.lightnovelreader.data.json.**{ *;}
-keep,allowobfuscation,allowshrinking class indi.dmzz_yyhyy.lightnovelreader.data.web.zaicomic.**{ *;}
-keepclassmembers class indi.dmzz_yyhyy.lightnovelreader.data.web.zaicomic.**{ *;}
-keepnames class indi.dmzz_yyhyy.lightnovelreader.data.web.zaicomic.**{ *;}
-keepclassmembernames class indi.dmzz_yyhyy.lightnovelreader.data.web.exploration.**{ *;}
-keep,allowobfuscation,allowshrinking class indi.dmzz_yyhyy.lightnovelreader.data.web.zaicomic.json.**{ *;}
-keepclassmembers class indi.dmzz_yyhyy.lightnovelreader.data.web.zaicomic.json.**{ *;}
-keepnames class indi.dmzz_yyhyy.lightnovelreader.data.web.zaicomic.json.**{ *;}
-keepclassmembernames class indi.dmzz_yyhyy.lightnovelreader.data.web.exploration.**{ *;}
-keep,allowobfuscation,allowshrinking class indi.dmzz_yyhyy.lightnovelreader.data.web.zaicomic.exploration.**{ *;}
-keepclassmembers class indi.dmzz_yyhyy.lightnovelreader.data.web.zaicomic.exploration.**{ *;}
-keepnames class indi.dmzz_yyhyy.lightnovelreader.data.web.zaicomic.exploration.**{ *;}
-keepclassmembernames class indi.dmzz_yyhyy.lightnovelreader.data.web.zaicomic.exploration.**{ *;}
-keep,allowobfuscation,allowshrinking class indi.dmzz_yyhyy.lightnovelreader.data.web.zaicomic.json.**{ *;}
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.examples.android.model.** { <fields>; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault
-if class com.google.gson.reflect.TypeToken
-keep,allowobfuscation class com.google.gson.reflect.TypeToken
-keep,allowobfuscation class * extends com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowoptimization @com.google.gson.annotations.JsonAdapter class *
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.Expose <fields>;
  @com.google.gson.annotations.JsonAdapter <fields>;
  @com.google.gson.annotations.Since <fields>;
  @com.google.gson.annotations.Until <fields>;
}
-keepclassmembers class * extends com.google.gson.TypeAdapter {
  <init>();
}
-keepclassmembers class * implements com.google.gson.TypeAdapterFactory {
  <init>();
}
-keepclassmembers class * implements com.google.gson.JsonSerializer {
  <init>();
}
-keepclassmembers class * implements com.google.gson.JsonDeserializer {
  <init>();
}
-if class *
-keepclasseswithmembers,allowobfuscation class <1> {
  @com.google.gson.annotations.SerializedName <fields>;
}
-if class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
-keepclassmembers,allowobfuscation,allowoptimization class <1> {
  <init>();
}
-keep class org.xmlpull.** { *; }
-keepclassmembers class org.xmlpull.** { *; }