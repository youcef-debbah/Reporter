package com.reporter.util.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.reporter.client.R
import com.reporter.util.model.Teller

object ExternalLink {

    const val PROTOCOL_MAILTO = "mailto:"
    const val ACTION_MAILTO = Intent.ACTION_SENDTO

    const val PROTOCOL_TEL = "tel:"
    const val ACTION_DIAL = Intent.ACTION_DIAL

    fun openLink(link: String, context: Context = AbstractApplication.INSTANCE) = try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(link)
        context.startActivity(intent)
    } catch (e: Exception) {
        Teller.error("could not open the default browser at: $link", e)
        Toasts.launchLong(R.string.activity_missing_action_link, context)
    }

    fun openNumberDialer(phoneNumber: String, context: Context = AbstractApplication.INSTANCE) {
        try {
            val intent = Intent(ACTION_DIAL)
            intent.data = Uri.parse(PROTOCOL_TEL + phoneNumber)
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Teller.error(
                "could not open an activity to handle: " +
                        "$ACTION_DIAL with '$PROTOCOL_TEL' protocol", e
            )
            Toasts.launchLong(R.string.activity_missing_action_dial, context)
        }
    }

    fun openEmailDialer(email: String, context: Context = AbstractApplication.INSTANCE) {
        try {
            val intent = Intent()
            intent.data = Uri.parse(PROTOCOL_MAILTO)
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.default_email_content))
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toasts.launchLong(R.string.activity_missing_action_mailto, context)
            Teller.error(
                "could not open an activity to handle action: " +
                        "$ACTION_MAILTO with '$PROTOCOL_MAILTO' protocol", e
            )
        }
    }
}