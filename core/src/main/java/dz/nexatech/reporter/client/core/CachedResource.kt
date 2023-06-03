package dz.nexatech.reporter.client.core

import com.google.common.collect.ImmutableMap
import dz.nexatech.reporter.client.common.atomicLazy
import java.io.ByteArrayInputStream
import java.io.InputStream

class CachedResource(private val resource: AbstractBinaryResource): AbstractBinaryResource() {

    override val headers: ImmutableMap<String, String> by atomicLazy { headersBuilder().build() }

    val data = lazy {
        resource.asByteArray()
    }

    override val path: String
        get() = resource.path
    override val mimeType: String
        get() = resource.mimeType
    override val lastModified: Long
        get() = resource.lastModified

    override fun asInputStream(): InputStream = ByteArrayInputStream(asByteArray())

    override fun asByteArray(): ByteArray = data.value

    override fun size(): Int? = if (data.isInitialized()) data.value.size else resource.size()
}