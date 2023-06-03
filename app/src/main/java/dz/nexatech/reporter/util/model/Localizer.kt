package dz.nexatech.reporter.util.model

import android.os.Build
import com.google.common.collect.ImmutableList
import dz.nexatech.reporter.client.common.AbstractLocalizer
import dz.nexatech.reporter.client.common.Texts
import dz.nexatech.reporter.client.common.atomicLazy
import dz.nexatech.reporter.util.ui.AbstractApplication
import java.util.*

object Localizer : AbstractLocalizer() {

    private val preferredLanguages: ImmutableList<String> by atomicLazy {
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

        return@atomicLazy ImmutableList.copyOf(result)
    }

    override fun inPrimaryLang(
        latinText: String?,
        arabicText: String?,
    ): String = inPrimaryLang(latinText, latinText, arabicText)

    override fun inPrimaryLang(
        englishText: String?,
        frenchText: String?,
        arabicText: String?,
    ): String = Texts.inPrimaryLang(englishText, frenchText, arabicText, preferredLanguages)

    override fun inSecondaryLang(
        latinText: String?,
        arabicText: String?,
    ): String = inSecondaryLang(latinText, latinText, arabicText)

    override fun inSecondaryLang(
        englishText: String?,
        frenchText: String?,
        arabicText: String?,
    ): String = Texts.inSecondaryLang(englishText, frenchText, arabicText, preferredLanguages)
}