package dz.nexatech.reporter.util.ui

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.client.common.PublicAPI
import dz.nexatech.reporter.client.common.mainLaunch
import dz.nexatech.reporter.client.common.withMain

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
    fun notImplemented(context: Context = AbstractApplication.INSTANCE) {
        short(R.string.not_implemented_yet, context)
    }

    fun launchShort(msg: Int, context: Context = AbstractApplication.INSTANCE) =
        launchShort(context.getString(msg), context)

    fun launchShort(msg: String, context: Context = AbstractApplication.INSTANCE) {
        mainLaunch {
            short(msg, context)
        }
    }

    fun launchLong(msg: Int, context: Context = AbstractApplication.INSTANCE) =
        launchLong(context.getString(msg), context)

    fun launchLong(msg: String, context: Context = AbstractApplication.INSTANCE) {
        mainLaunch {
            long(msg, context)
        }
    }
}