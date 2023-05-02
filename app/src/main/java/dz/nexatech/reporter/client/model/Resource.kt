package dz.nexatech.reporter.client.model

import android.webkit.WebResourceResponse
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.common.collect.ImmutableMap
import dz.nexatech.reporter.client.common.Texts
import dz.nexatech.reporter.client.common.readAsBytes
import dz.nexatech.reporter.client.core.AbstractBinaryResource
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
) : AbstractBinaryResource() {

    @delegate:Ignore
    override val headers: ImmutableMap<String, String> by lazy { headersBuilder().build() }

    override fun size(): Int = data.size

    override fun asInputStream(): InputStream = ByteArrayInputStream(data)

    override fun asByteArray(): ByteArray = data

    override fun equals(other: Any?) =
        this === other || (other is Resource && this.path == other.path)

    override fun hashCode() = path.hashCode()

    override fun toString() = "Resource(path='$path')"
}

class AssetResource(
    override val path: String,
    override val mimeType: String,
) : AbstractBinaryResource() {
    companion object {
        val application: AbstractApplication = AbstractApplication.INSTANCE
        val buildEpoch: Long = application.config.buildEpoch
    }

    override val headers: ImmutableMap<String, String> by lazy { headersBuilder().build() }
    override val lastModified: Long = buildEpoch
    override fun size(): Int? = null
    override fun asInputStream(): InputStream = application.assets.open(path)
    override fun asByteArray(): ByteArray = asInputStream().readAsBytes()
}

fun AbstractBinaryResource.asWebResourceResponse(encoding: String = Texts.UTF_8): WebResourceResponse =
    WebResourceResponse(
        mimeType,
        encoding,
        200,
        "ok",
        headers,
        asInputStream()
    )