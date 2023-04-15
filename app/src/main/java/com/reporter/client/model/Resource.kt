package com.reporter.client.model

import android.webkit.WebResourceResponse
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.common.collect.ImmutableMap
import com.reporter.common.Texts
import com.reporter.common.Webkit
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
    val path: String,
    @ColumnInfo(name = RESOURCE_COLUMN_MIME_TYPE)
    val mimeType: String,
    @ColumnInfo(name = RESOURCE_COLUMN_DATA)
    val data: ByteArray,
    @ColumnInfo(name = RESOURCE_COLUMN_LAST_MODIFIED)
    val lastModified: Long,
) {

    @Ignore
    private val headers = ImmutableMap.builder<String, String>().apply {
        put("Content-Type", "$mimeType;charset=${Texts.UTF_8}")
        put("Content-Length", data.size.toString())
        put("Cache-Control", "no-cache")
        put("Expires", "Thu, 01 Jan 1970 00:00:00 GMT")
        put("Last-Modified", Webkit.formatDate(lastModified))
    }.build()

    override fun equals(other: Any?) =
        this === other || (other is Resource && this.path == other.path)

    override fun hashCode() = path.hashCode()

    override fun toString() = "Resource(path='$path')"

    fun asWebResourceResponse(encoding: String = Texts.UTF_8): WebResourceResponse =
        WebResourceResponse(
            mimeType,
            Texts.UTF_8,
            200,
            "ok",
            headers,
            asInputStream()
        )

    fun asInputStream(): InputStream = ByteArrayInputStream(data)
}