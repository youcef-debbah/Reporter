package com.reporter.util.ui

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import com.reporter.common.PublicAPI
import com.reporter.client.R

@PublicAPI
fun shortToast(text: String, context: Context = AbstractApplication.INSTANCE) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

@PublicAPI
fun shortToast(@StringRes text: Int, context: Context = AbstractApplication.INSTANCE) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

@PublicAPI
fun longToast(text: String, context: Context = AbstractApplication.INSTANCE) {
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
}

@PublicAPI
fun longToast(@StringRes text: Int, context: Context = AbstractApplication.INSTANCE) {
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
}

@PublicAPI
fun toastNotImplemented() {
    shortToast(R.string.not_implemented_yet)
}