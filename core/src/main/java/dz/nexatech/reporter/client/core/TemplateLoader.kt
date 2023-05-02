package dz.nexatech.reporter.client.core

import io.pebbletemplates.pebble.error.LoaderException
import io.pebbletemplates.pebble.loader.Loader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.function.Function
import java.util.function.Predicate

class TemplateLoader(
    val templateContentLoader: Function<String, InputStream?>,
    val templateExistenceChecker: Predicate<String>,
) : Loader<String> {

    override fun getReader(templateName: String): Reader {
        val inputStream = templateContentLoader.apply(templateName)
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

    override fun resourceExists(templateName: String): Boolean =
        templateExistenceChecker.test(templateName)
}
