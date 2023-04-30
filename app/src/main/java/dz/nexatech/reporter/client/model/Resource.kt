package dz.nexatech.reporter.client.model

import android.webkit.WebResourceResponse
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.common.collect.ImmutableMap
import dz.nexatech.reporter.common.Texts
import dz.nexatech.reporter.common.Webkit
import dz.nexatech.reporter.common.readAsBytes
import dz.nexatech.reporter.util.ui.AbstractApplication
import java.io.ByteArrayInputStream
import java.io.InputStream

const val RESOURCE_TABLE = "resource"
const val RESOURCE_COLUMN_PATH = RESOURCE_TABLE + "_path"
const val RESOURCE_COLUMN_MIME_TYPE = "mime_type"
const val RESOURCE_COLUMN_DATA = "data"
const val RESOURCE_COLUMN_LAST_MODIFIED = "last_update"

@Entity
class Resource(
    @PrimaryKey
    @ColumnInfo(name = RESOURCE_COLUMN_PATH)
    override val path: String,
    @ColumnInfo(name = RESOURCE_COLUMN_MIME_TYPE)
    override val mimeType: String,
    @ColumnInfo(name = RESOURCE_COLUMN_DATA)
    val data: ByteArray,
    @ColumnInfo(name = RESOURCE_COLUMN_LAST_MODIFIED)
    override val lastModified: Long,
) : BinaryResource() {

    override fun equals(other: Any?) =
        this === other || (other is Resource && this.path == other.path)

    override fun hashCode() = path.hashCode()

    override fun toString() = "Resource(path='$path')"

    override fun size(): Int = data.size

    override fun asInputStream(): InputStream = ByteArrayInputStream(data)

    override fun asByteArray(): ByteArray = data
}

abstract class BinaryResource {

    abstract val path: String
    abstract val mimeType: String
    abstract val lastModified: Long

    @delegate:Ignore
    private val headers by lazy {
        ImmutableMap.builder<String, String>().apply {
            put("Content-Type", "$mimeType;charset=${Texts.UTF_8}")
            put("Cache-Control", "no-cache")
            put("Expires", "Thu, 01 Jan 1970 00:00:00 GMT")
            put("Last-Modified", Webkit.formatDate(lastModified))
            size()?.let { put("Content-Length", it.toString()) }
        }.build()
    }

    open fun asWebResourceResponse(encoding: String = Texts.UTF_8): WebResourceResponse =
        WebResourceResponse(
            mimeType,
            Texts.UTF_8,
            200,
            "ok",
            headers,
            asInputStream()
        )

    abstract fun asInputStream(): InputStream

    abstract fun asByteArray(): ByteArray

    abstract fun size(): Int?

    override fun toString(): String {
        return "BinaryResource(path='$path', mimeType='$mimeType')"
    }
}

class AssetResource(
    override val path: String,
    override val mimeType: String,
) : BinaryResource() {
    companion object {
        val application: AbstractApplication = AbstractApplication.INSTANCE
        val buildEpoch: Long = application.config.buildEpoch
    }
    override val lastModified: Long = buildEpoch
    override fun size(): Int? = null
    override fun asInputStream(): InputStream = application.assets.open(path)
    override fun asByteArray(): ByteArray = asInputStream().readAsBytes()
}

class CachedResource(private val resource: BinaryResource): BinaryResource() {

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