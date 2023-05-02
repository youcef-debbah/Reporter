package dz.nexatech.reporter.util.model

import android.content.Context
import android.net.Uri
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.client.common.withIO
import dz.nexatech.reporter.util.ui.Toasts
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

suspend fun Context.useOutputStream(uri: Uri, block: suspend (OutputStream) -> Unit) = withIO {
    var outputStream: OutputStream? = null

    try {
        outputStream = this@useOutputStream.contentResolver.openOutputStream(uri)
    } catch (e: IOException) {
        Toasts.launchShort(R.string.output_stream_io_failure, this@useOutputStream)
        Teller.error("failed to open OutputStream for path: ${uri.path}", e)
    }

    if (outputStream == null) {
        Toasts.launchShort(R.string.output_stream_system_failure, this@useOutputStream)
        Teller.warn("null OutputStream for path: ${uri.path}")
        return@withIO
    } else {
        outputStream.use { block(it) }
    }
}

suspend fun Context.useInputStream(uri: Uri, block: suspend (InputStream) -> Unit) = withIO {
    var inputStream: InputStream? = null

    try {
        inputStream = this@useInputStream.contentResolver.openInputStream(uri)
    } catch (e: IOException) {
        Toasts.launchShort(R.string.input_stream_io_failure, this@useInputStream)
        Teller.error("failed to open InputStream for path: ${uri.path}", e)
    }

    if (inputStream == null) {
        Toasts.launchShort(R.string.input_stream_system_failure, this@useInputStream)
        Teller.warn("null InputStream for path: ${uri.path}")
        return@withIO
    } else {
        inputStream.use { block(it) }
    }
}