package dz.nexatech.reporter.client.core

import com.itextpdf.html2pdf.ConverterProperties
import com.itextpdf.html2pdf.HtmlConverter
import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.CompressionConstants
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.font.FontProvider
import com.itextpdf.layout.font.FontSet
import dz.nexatech.reporter.client.common.silverRatio
import java.io.OutputStream

class PdfConverter(
    resourceLoader: IResourceLoader,
    val pdfWriterSmartCachingEnabled: Boolean = false,
    val pdfWriterCompressionLevel: Int = DEFAULT_COMPRESSION_LEVEL,
    val fontsLoader: suspend () -> Collection<ByteArray>,
) {
    companion object {
        const val DEFAULT_PAGE_WIDTH = 595f
        const val DEFAULT_COMPRESSION_LEVEL = CompressionConstants.BEST_COMPRESSION
    }

    val pdfResourcesRetriever = ResourceRetrieverAdapter(resourceLoader)

    private suspend fun loadFontSet() = FontSet().apply {
        fontsLoader.invoke().forEach { resource ->
            addFont(resource, PdfEncodings.IDENTITY_H)
        }
    }

    private suspend fun buildConverterProperties(): ConverterProperties =
        ConverterProperties().apply {
            resourceRetriever = pdfResourcesRetriever
            isImmediateFlush = false
            fontProvider = FontProvider(loadFontSet(), "Helvetica")
        }

    suspend fun generatePDF(
        pageWidth: Float?,
        outputStream: OutputStream,
        html: String,
    ) {
        val pdfWriter = PdfWriter(outputStream).apply {
            compressionLevel = pdfWriterCompressionLevel
            setSmartMode(pdfWriterSmartCachingEnabled)
        }

        val width = pageWidth ?: DEFAULT_PAGE_WIDTH
        PdfDocument(pdfWriter).apply {
            defaultPageSize = PageSize(width, (width * silverRatio).toFloat())
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