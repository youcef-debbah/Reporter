# file: app/proguard-rules.pro
# docs:
# http://developer.android.com/guide/developing/tools/proguard.html
# https://www.guardsquare.com/manual/configuration/usage
# https://jebware.com/blog/?p=418

# this rule keep WebView JS,
#-keepclassmembers class fqcn.of.my.javascript.interface.for.webview {
#   public *;
#}

# this rule keep the line number information
-keepattributes SourceFile,LineNumberTable

# this rule hide the original source file name (use in case the line number information is kept)
#-renamesourcefileattribute SourceFile

# Keep mmkv methods
-keepclasseswithmembers,includedescriptorclasses class com.tencent.mmkv.** {
    native <methods>;
    long nativeHandle;
    private static *** onMMKVCRCCheckFail(***);
    private static *** onMMKVFileLengthError(***);
    private static *** mmkvLogImp(...);
    private static *** onContentChangedByOuterProcess(***);
}

# fix a reflection access bug
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# keep itext Typography entry point
-keep class com.itextpdf.typography.shaping.TypographyApplier {
    public static void registerForLayout();
}

# supress warning about bouncycastle (an unused PDF signing/encrypting lib)
-dontwarn com.itextpdf.bouncycastle.BouncyCastleFactory