package com.reporter.util.ui

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import com.reporter.common.PublicAPI
import com.reporter.client.R

object Toasts {
    @PublicAPI
    fun short(text: String, context: Context = AbstractApplication.INSTANCE) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    @PublicAPI
    fun short(@StringRes text: Int, context: Context = AbstractApplication.INSTANCE) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    @PublicAPI
    fun long(text: String, context: Context = AbstractApplication.INSTANCE) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    @PublicAPI
    fun long(@StringRes text: Int, context: Context = AbstractApplication.INSTANCE) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    @PublicAPI
    fun notImplemented() {
        short(R.string.not_implemented_yet)
    }
}