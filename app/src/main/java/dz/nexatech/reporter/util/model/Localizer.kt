package dz.nexatech.reporter.util.model

import com.google.common.collect.ImmutableList
import dz.nexatech.reporter.client.common.AbstractLocalizer
import dz.nexatech.reporter.client.common.Texts
import dz.nexatech.reporter.client.common.atomicLazy
import dz.nexatech.reporter.client.common.splitIntoList
import dz.nexatech.reporter.util.ui.AbstractApplication
import java.util.Calendar
import java.util.LinkedList
import java.util.Locale

class Localizer private constructor(formattingLang: String?) : AbstractLocalizer() {

    override val locale: Locale = when (formattingLang) {
        Texts.LANG_FR -> FRENCH_LOCALE
        Texts.LANG_AR -> ARABIC_LOCALE
        else -> ENGLISH_LOCALE
    }

    val monthsNames: Array<String> = when (formattingLang) {
        Texts.LANG_FR -> frenchMonths
        Texts.LANG_AR -> arabicMonths
        else -> englishMonths
    }

    val isArabic: Boolean = formattingLang == Texts.LANG_AR

    override fun formatMonthName(monthIndex: Int): String = monthsNames[monthIndex % 12]

    override fun monthIndex(monthName: String): Int? = monthsIndexes[monthName]

    override fun formatDateTime(epoch: Long): String {
        val date = newCalendar().apply {
            this.timeInMillis = epoch
        }

        val year = date.get(Calendar.YEAR)
        val month = date.get(Calendar.MONTH)
        val day = date.get(Calendar.DAY_OF_MONTH)
        val hour = date.get(Calendar.HOUR_OF_DAY)
        val min = date.get(Calendar.MINUTE)
        val sec = date.get(Calendar.SECOND)

        return if (isArabic) {
            String.format(
                "%04d/%s/%02d %02d:%02d:%02d",
                year,
                month + 1,
                day,
                hour,
                min,
                sec,
            )
        } else {
            String.format(
                "%02d %s %04d %02d:%02d:%02d",
                day,
                formatMonthName(month),
                year,
                hour,
                min,
                sec,
            )
        }
    }

    override fun formatSimpleDate(epoch: Long?): String? {
        if (epoch == null) return null

        val date = newCalendar().apply {
            this.timeInMillis = epoch
        }

        val year = date.get(Calendar.YEAR)
        val month = date.get(Calendar.MONTH)
        val day = date.get(Calendar.DAY_OF_MONTH)
        return if (isArabic) {
            String.format(
                "%04d/%s/%02d",
                year,
                month + 1,
                day,
            )
        } else {
            String.format(
                "%02d %s %04d",
                day,
                formatMonthName(month),
                year,
            )
        }
    }

    override fun parseSimpleDate(templateDate: String): Long? {
        val length = templateDate.length
        if (length < 10) {
            return null
        } else {
            return parseDateTokens(templateDate.splitIntoList(' '), true)
                ?: parseDateTokens(templateDate.splitIntoList('/'), false)
        }
    }

    private fun parseDateTokens(tokens: ImmutableList<String>, namedMonth: Boolean): Long? {
        if (tokens.size == 3) {
            val t0 = tokens[0]
            val t1 = if (namedMonth) monthIndex(tokens[1]) else tokens[1].toIntOrNull()
            val t2 = tokens[2]

            if (t0.length == 2 && t2.length == 4) {
                return calcEpoch(t0, t1, t2)
            } else if (t0.length == 4 && t2.length == 2) {
                return calcEpoch(t2, t1, t0)
            }
        }

        return null
    }

    private fun calcEpoch(dayNumber: String, month: Int?, yearNumber: String): Long? {
        val day = dayNumber.toIntOrNull()
        val year = yearNumber.toIntOrNull()

        if (day != null && month != null && year != null) {
            val calendar = newCalendar().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.HOUR_OF_DAY, 12)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            if (calendar.get(Calendar.YEAR) == year
                && calendar.get(Calendar.MONTH) == month
                && calendar.get(Calendar.DAY_OF_MONTH) == day
            ) {
                return calendar.timeInMillis
            }
        }

        return null
    }

    override fun inPrimaryLang(
        latinText: String?,
        arabicText: String?,
    ): String = Localizer.inPrimaryLang(latinText, arabicText)

    override fun inPrimaryLang(
        englishText: String?,
        frenchText: String?,
        arabicText: String?,
    ): String = Localizer.inPrimaryLang(englishText, frenchText, arabicText)

    override fun inSecondaryLang(
        latinText: String?,
        arabicText: String?,
    ): String = Localizer.inSecondaryLang(latinText, arabicText)

    override fun inSecondaryLang(
        englishText: String?,
        frenchText: String?,
        arabicText: String?,
    ): String = Localizer.inSecondaryLang(englishText, frenchText, arabicText)

    companion object {

        fun from(lang: String): Localizer = Localizer(lang)

        private val preferredLanguages: ImmutableList<String> by atomicLazy {
            val result = LinkedList<String>()
            try {
                val configuration = AbstractApplication.INSTANCE.resources.configuration
                val locales = configuration.locales
                for (i in 0 until locales.size()) {
                    val userLang = locales[i].language
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
    }
}