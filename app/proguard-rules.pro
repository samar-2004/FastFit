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

# ---------------------------------------------------------------------------
# FastFit — keep rules for libraries used in this app
# ---------------------------------------------------------------------------

# Firebase Firestore serializes/deserializes our model classes via reflection.
# Keep the model package and their no-arg constructors + fields intact.
-keepclassmembers class com.example.fastfit.model.** {
    <init>();
    <fields>;
    public <methods>;
}
-keep class com.example.fastfit.model.** { *; }

# Firebase / Google Play services
-keepattributes Signature, *Annotation*, EnclosingMethod, InnerClasses
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule { <init>(...); }
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** { **[] $VALUES; public *; }

# MPAndroidChart
-keep class com.github.mikephil.charting.** { *; }
-dontwarn io.realm.**

# YouTube player
-keep class com.pierfrancescosoffritti.androidyoutubeplayer.** { *; }
