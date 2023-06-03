package dz.nexatech.reporter.client.core

import com.google.common.collect.ImmutableMap
import dz.nexatech.reporter.client.common.Formatters
import dz.nexatech.reporter.client.common.Texts
import dz.nexatech.reporter.client.common.atomicLazy
import java.io.ByteArrayInputStream
import java.io.InputStream

abstract class AbstractBinaryResource {

    abstract val path: String
    abstract val mimeType: String
    abstract val lastModified: Long

    abstract val headers: ImmutableMap<String, String>

    protected fun headersBuilder(): ImmutableMap.Builder<String, String> =
        ImmutableMap.builder<String, String>().apply {
            put("Content-Type", "$mimeType;charset=${Texts.UTF_8}")
            put("Cache-Control", "no-cache")
            put("Expires", "Thu, 01 Jan 1970 00:00:00 GMT")
            put("Last-Modified", Formatters.asHttpDate(lastModified))
            size()?.let { put("Content-Length", it.toString()) }
        }

    abstract fun asInputStream(): InputStream

    abstract fun asByteArray(): ByteArray

    abstract fun size(): Int?

    final override fun equals(other: Any?): Boolean =
        this === other || other is AbstractBinaryResource && this.path == other.path

    final override fun hashCode() = path.hashCode()

    override fun toString(): String {
        return "BinaryResource(path='$path', mimeType='$mimeType')"
    }
}

class SimpleBinaryResource(
    val data: ByteArray,
    override val path: String,
    override val mimeType: String,
    override val lastModified: Long,
) : AbstractBinaryResource() {
    override val headers: ImmutableMap<String, String> by atomicLazy { headersBuilder().build() }
    override fun asInputStream() = ByteArrayInputStream(data)
    override fun asByteArray() = data
    override fun size() = data.size
}