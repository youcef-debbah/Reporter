package dz.nexatech.reporter.util.model

import android.os.Build
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import dz.nexatech.reporter.client.common.AbstractLocalizer
import dz.nexatech.reporter.client.common.Texts
import dz.nexatech.reporter.client.common.atomicLazy
import dz.nexatech.reporter.client.common.splitIntoList
import dz.nexatech.reporter.util.ui.AbstractApplication
import java.util.*

class Localizer private constructor(formattingLang: String?) : AbstractLocalizer() {

    val isArabic: Boolean = formattingLang == Texts.LANG_AR

    val monthsNames: Array<String>? = when (formattingLang) {
        Texts.LANG_EN -> englishMonths
        Texts.LANG_FR -> frenchMonths
        Texts.LANG_AR -> arabicMonths
        else -> null
    }

    override fun formatMonthName(monthIndex: Int): String = monthsNames?.get(monthIndex % 12)
        ?: throw IllegalStateException("this localizer does not support formatting")

    override fun monthIndex(monthName: String): Int? = monthsIndexes[monthName]

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
            return newCalendar().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.HOUR_OF_DAY, 12)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
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

        val utcTimeZone: TimeZone = TimeZone.getTimeZone("UTC")

        fun newCalendar(): Calendar = Calendar.getInstance(utcTimeZone)

        fun from(lang: String): Localizer = Localizer(lang)

        private val monthsIndexes = ImmutableMap.builder<String, Int>()
            .put("January", 0)
            .put("February", 1)
            .put("March", 2)
            .put("April", 3)
            .put("May", 4)
            .put("June", 5)
            .put("July", 6)
            .put("August", 7)
            .put("September", 8)
            .put("October", 9)
            .put("November", 10)
            .put("December", 11)

            .put("Janvier", 0)
            .put("Février", 1)
            .put("Mars", 2)
            .put("Avril", 3)
            .put("Mai", 4)
            .put("Juin", 5)
            .put("Juillet", 6)
            .put("Août", 7)
            .put("Septembre", 8)
            .put("Octobre", 9)
            .put("Novembre", 10)
            .put("Décembre", 11)

            .put("\u062C\u0627\u0646\u0641\u064A", 0)
            .put("\u0641\u064A\u0641\u0631\u064A", 1)
            .put("\u0645\u0627\u0631\u0633", 2)
            .put("\u0623\u0641\u0631\u064A\u0644", 3)
            .put("\u0645\u0627\u064A", 4)
            .put("\u062C\u0648\u0627\u0646", 5)
            .put("\u062C\u0648\u064A\u0644\u064A\u0629", 6)
            .put("\u0623\u0648\u062A", 7)
            .put("\u0633\u0628\u062A\u0645\u0628\u0631", 8)
            .put("\u0623\u0643\u062A\u0648\u0628\u0631", 9)
            .put("\u0646\u0648\u0641\u0645\u0628\u0631", 10)
            .put("\u062F\u064A\u0633\u0645\u0628\u0631", 11)

            .build()

        private val englishMonths = arrayOf(
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December",
        )

        private val frenchMonths = arrayOf(
            "Janvier",
            "Février",
            "Mars",
            "Avril",
            "Mai",
            "Juin",
            "Juillet",
            "Août",
            "Septembre",
            "Octobre",
            "Novembre",
            "Décembre",
        )

        private val arabicMonths = arrayOf(
            "\u062C\u0627\u0646\u0641\u064A",
            "\u0641\u064A\u0641\u0631\u064A",
            "\u0645\u0627\u0631\u0633",
            "\u0623\u0641\u0631\u064A\u0644",
            "\u0645\u0627\u064A",
            "\u062C\u0648\u0627\u0646",
            "\u062C\u0648\u064A\u0644\u064A\u0629",
            "\u0623\u0648\u062A",
            "\u0633\u0628\u062A\u0645\u0628\u0631",
            "\u0623\u0643\u062A\u0648\u0628\u0631",
            "\u0646\u0648\u0641\u0645\u0628\u0631",
            "\u062F\u064A\u0633\u0645\u0628\u0631",
        )

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