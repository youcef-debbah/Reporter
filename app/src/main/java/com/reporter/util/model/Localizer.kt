package com.reporter.util.model

import android.os.Build
import androidx.annotation.StringRes
import com.google.common.collect.ImmutableList
import com.reporter.common.Texts
import com.reporter.util.ui.AbstractApplication
import java.util.*

object Localizer {

    val preferredLanguages: ImmutableList<String> by lazy {
        val result = LinkedList<String>()
        try {
            val configuration = AbstractApplication.INSTANCE.resources.configuration
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val locales = configuration.locales
                for (i in 0 until locales.size()) {
                    val userLang = locales[i].language
                    for (lang in Texts.SUPPORTED_LANGUAGES)
                        if (userLang.startsWith(lang))
                            result.add(lang)
                }
            } else {
                @Suppress("DEPRECATION") // only executed on old platforms
                val userLang = configuration.locale.language
                for (lang in Texts.SUPPORTED_LANGUAGES)
                    if (userLang.startsWith(lang))
                        result.add(lang)
            }
        } catch (e: RuntimeException) {
            Teller.error("could not get user lang preferences", e)
        }

        if (!result.contains(Texts.LANG_EN))
            result.add(Texts.LANG_EN)
        if (!result.contains(Texts.LANG_FR))
            result.add(Texts.LANG_FR)
        if (!result.contains(Texts.LANG_AR))
            result.add(Texts.LANG_AR)

        return@lazy ImmutableList.copyOf(result)
    }

    fun inPrimaryLang(
        latinText: String?,
        arabicText: String?,
    ): String = inPrimaryLang(latinText, latinText, arabicText)

    fun inPrimaryLang(
        englishText: String?,
        frenchText: String?,
        arabicText: String?,
    ): String = Texts.inPrimaryLang(englishText, frenchText, arabicText, preferredLanguages)

    fun inSecondaryLang(
        latinText: String?,
        arabicText: String?,
    ): String = inSecondaryLang(latinText, latinText, arabicText)

    fun inSecondaryLang(
        englishText: String?,
        frenchText: String?,
        arabicText: String?,
    ): String = Texts.inSecondaryLang(englishText, frenchText, arabicText, preferredLanguages)

    fun getString(@StringRes resId: Int) = AbstractApplication.INSTANCE.getString(resId)
}