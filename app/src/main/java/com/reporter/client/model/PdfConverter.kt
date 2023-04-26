package com.reporter.client.model

import com.itextpdf.html2pdf.ConverterProperties
import com.itextpdf.html2pdf.HtmlConverter
import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.CompressionConstants
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.font.FontProvider
import com.itextpdf.layout.font.FontSet
import com.itextpdf.styledxmlparser.resolver.resource.IResourceRetriever
import com.reporter.util.model.AppConfig
import java.io.OutputStream
import java.util.function.Supplier

class PdfConverter(
    val resourceLoader: IResourceRetriever,
    val fontsLoader: suspend () -> Collection<ByteArray>,
) {

    private suspend fun loadFontSet() = FontSet().apply {
        fontsLoader.invoke().forEach { resource ->
            addFont(resource, PdfEncodings.IDENTITY_H)
        }
    }

    private suspend fun buildConverterProperties(): ConverterProperties =
        ConverterProperties().apply {
            resourceRetriever = resourceLoader
            isImmediateFlush = false
            fontProvider = FontProvider(loadFontSet(), "Helvetica")
        }

    suspend fun generatePDF(outputStream: OutputStream,
                            html: String) {
        val pdfWriter = PdfWriter(outputStream).apply {
            compressionLevel = CompressionConstants.BEST_COMPRESSION
            setSmartMode(AppConfig.get(CONFIG_PDF_RESOURCES_CACHING_ENABLED))
        }

        PdfDocument(pdfWriter).apply {
            defaultPageSize = PageSize.A4
        }.use { pdfDocument ->
            val document = HtmlConverter.convertToDocument(
                html,
                pdfDocument,
                buildConverterProperties()
            )
            document.setMargins(0f, 0f, 0f, 0f)
            document.relayout()
            document.flush()
        }
    }
}