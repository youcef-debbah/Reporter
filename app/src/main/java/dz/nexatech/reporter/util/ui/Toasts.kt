package dz.nexatech.reporter.util.ui

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.client.common.mainLaunch

object Toasts {
    fun short(text: String, context: Context = AbstractApplication.INSTANCE) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    fun short(@StringRes text: Int, context: Context = AbstractApplication.INSTANCE) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    fun long(text: String, context: Context = AbstractApplication.INSTANCE) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    fun long(@StringRes text: Int, context: Context = AbstractApplication.INSTANCE) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

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