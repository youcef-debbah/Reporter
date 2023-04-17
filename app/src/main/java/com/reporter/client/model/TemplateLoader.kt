package com.reporter.client.model

import io.pebbletemplates.pebble.error.LoaderException
import io.pebbletemplates.pebble.loader.Loader
import kotlinx.coroutines.runBlocking
import java.io.InputStreamReader
import java.io.Reader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class TemplateLoader(val repository: TemplatesRepository) : Loader<String> {

    override fun getReader(templateName: String): Reader {
        val inputStream = runBlocking { repository.loadTemplateContent(templateName) }
            ?: throw LoaderException(null, "Could not load template content for: '$templateName'")
        return InputStreamReader(inputStream, StandardCharsets.UTF_8)
    }

    override fun setSuffix(suffix: String) {}

    override fun setPrefix(prefix: String) {}

    override fun setCharset(charset: String) {
        if (Charset.forName(charset) != StandardCharsets.UTF_8) {
            throw UnsupportedOperationException("unsupported charset: $charset")
        }
    }

    override fun resolveRelativePath(relativePath: String, anchorPath: String): String =
        relativePath

    override fun createCacheKey(templateName: String): String = templateName

    override fun resourceExists(templateName: String): Boolean = runBlocking {
        repository.templateExists(templateName)
    }
}
