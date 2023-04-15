package com.reporter.client.model

import android.content.Context
import android.net.Uri
import com.itextpdf.styledxmlparser.resolver.resource.IResourceRetriever
import com.reporter.common.readAsBytes
import com.reporter.util.model.Teller
import dagger.Lazy
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import javax.inject.Inject

class ResourcesRepository @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val resourcesDAO: Lazy<ResourcesDAO>,
): IResourceRetriever {

    fun load(path: String?): Resource? {
        if (path == null) return null
//      load from the assets and the database
//      support smart caching
//      context.assets.open(Texts.ASSETS_PREFIX + path)
        val dao = resourcesDAO.get()
        val paths =  dao.paths()
        val resource = dao.load(path)
        if (resource != null) {
            return resource
        } else {
            Teller.warn("resource not found for path: $path")
            return null
        }
    }

    override fun getInputStreamByUrl(url: URL): InputStream? {
        val path = url.path
        Teller.debug("loading binary resource from: $path")
        return load(path)?.asInputStream()
    }

    override fun getByteArrayByUrl(url: URL): ByteArray? = getInputStreamByUrl(url)?.readAsBytes()

    fun openFile(uri: Uri): OutputStream? = context.contentResolver.openOutputStream(uri)
}