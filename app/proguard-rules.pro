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

# a fix to Firebase installation crashs
-keepattributes AutoValue
-keep class com.google.firebase.installations.** {
  *;
}

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
-keepclassmembers enum ** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# keep itext Typography entry point
-keep class com.itextpdf.typography.shaping.TypographyApplier {
    public static void registerForLayout();
}

-keepnames class ** extends com.itextpdf.commons.actions.AbstractITextEvent
-keepnames class ** extends com.itextpdf.commons.actions.contexts.IMetaInfo
-keepnames class ** extends com.itextpdf.html2pdf.attach.ITagWorker
-keepnames class ** extends com.itextpdf.io.source.IRandomAccessSource
-keepnames class ** extends com.itextpdf.forms.fields.merging.OnDuplicateFormFieldNameStrategy
-keepnames class ** extends com.itextpdf.kernel.pdf.canvas.parser.clipper.IClipper
-keepnames class com.itextpdf.signatures.CertificateVerifier
-keepnames class com.itextpdf.kernel.pdf.canvas.parser.clipper.Edge

-keep class com.itextpdf.forms.util.RegisterDefaultDiContainer { *; }
-keep class **.DictionaryData { *; }
-keep class sun.misc.Unsafe { *; }
-keepnames class ** extends java.lang.Throwable
-keepnames class ** extends com.itextpdf.kernel.pdf.PdfObject
-keepnames class ** extends com.itextpdf.layout.renderer.BlockRenderer
-keepnames class ** extends com.itextpdf.styledxmlparser.jsoup.parser.Token

-keep class ** extends java.lang.ref.PhantomReference { *; }
-keep class ** extends java.nio.Buffer { *; }
-keep class com.itextpdf.kernel.pdf.PdfName { *; }

-dontwarn com.itextpdf.bouncycastle.BouncyCastleFactory