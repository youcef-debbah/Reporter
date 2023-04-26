package com.reporter.client.model

import com.itextpdf.styledxmlparser.resolver.resource.IResourceRetriever

interface PdfResourceRetriever : IResourceRetriever {
    suspend fun loadFonts(fontNames: Collection<String>): List<ByteArray>
}