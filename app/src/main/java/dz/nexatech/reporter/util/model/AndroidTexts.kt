package dz.nexatech.reporter.util.model

import android.os.BaseBundle
import dz.nexatech.reporter.client.common.Texts

fun BaseBundle.toDataString(): String {
    return if (this.isEmpty) Texts.NOT_AVAILABLE else {
        val keys = this.keySet()
        val data = StringBuilder(keys.size * 32)
        for (key in keys) {
            data.append(key)
                .append(Texts.KEY_VALUE_SEPARATOR)
                .append(getString(key) ?: getLong(key))
                .append(Texts.DATA_SEPARATOR)
        }
        data.toString()
    }
}