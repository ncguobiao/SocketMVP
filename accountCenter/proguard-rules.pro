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









# glide 的混淆代码
# gl -keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
# banner 的混淆代码
-keep class com.youth.banner.** {
    *;
 }


#SwipeRecyclerView
-keepclasseswithmembers class android.support.v7.widget.RecyclerView$ViewHolder {
   public final View *;
}
#其它类都是可以混淆的，如果添加了上述规则后还是存在问题，那么再追加：

-dontwarn com.yanzhenjie.recyclerview.swipe.**
-keep class com.yanzhenjie.recyclerview.swipe.** {*;}