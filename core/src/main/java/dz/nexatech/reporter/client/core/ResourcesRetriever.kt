package dz.nexatech.reporter.client.core

import com.itextpdf.styledxmlparser.resolver.resource.IResourceRetriever
import java.io.InputStream
import java.net.URL

interface IResourceLoader {
    fun getInputStreamByUrl(url: URL?): InputStream?
    fun getByteArrayByUrl(url: URL?): ByteArray?
}

class ResourceRetrieverAdapter(private val loader: IResourceLoader) : IResourceRetriever {
    override fun getInputStreamByUrl(url: URL?): InputStream? = loader.getInputStreamByUrl(url)
    override fun getByteArrayByUrl(url: URL?): ByteArray? = loader.getByteArrayByUrl(url)
}