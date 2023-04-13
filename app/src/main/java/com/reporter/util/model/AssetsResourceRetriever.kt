package com.reporter.util.model

import android.content.Context
import com.itextpdf.styledxmlparser.resolver.resource.IResourceRetriever
import com.reporter.common.Texts
import com.reporter.common.readAsBytes
import com.reporter.util.ui.AbstractApplication
import java.io.IOException
import java.io.InputStream
import java.net.URL

/**
 * Implementation of [IResourceRetriever] that loads resources from the Android assets manager.
 */
class AssetsResourceRetriever(
    private val context: Context = AbstractApplication.INSTANCE
) : IResourceRetriever {

    override fun getInputStreamByUrl(url: URL): InputStream? {
        return try {
            if (url.path.startsWith(Texts.ASSETS_PREFIX)) {
                context.assets.open(url.path.substringAfter(Texts.ASSETS_PREFIX))
            } else {
                Teller.warn("unsupported assets url: $url")
                null
            }
        } catch (e: IOException) {
            Teller.error("failed to load assets resource from url: $url", e)
            null
        }
    }

    override fun getByteArrayByUrl(url: URL): ByteArray? = getInputStreamByUrl(url)?.readAsBytes()
}