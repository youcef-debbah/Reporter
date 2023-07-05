package dz.nexatech.reporter.client.common

import com.google.common.collect.ImmutableMap
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

abstract class AbstractLocalizer {

    abstract val locale: Locale

    abstract fun formatMonthName(monthIndex: Int): String

    abstract fun monthIndex(monthName: String): Int?

    abstract fun formatSimpleDate(epoch: Long?): String?

    abstract fun parseSimpleDate(templateDate: String): Long?

    abstract fun inPrimaryLang(latinText: String?, arabicText: String?): String

    abstract fun inPrimaryLang(
        englishText: String?,
        frenchText: String?,
        arabicText: String?,
    ): String

    abstract fun inSecondaryLang(latinText: String?, arabicText: String?): String

    abstract fun inSecondaryLang(
        englishText: String?,
        frenchText: String?,
        arabicText: String?,
    ): String

    companion object {

        val ARABIC_LOCALE: Locale = Locale("ar", "DZ")
        val FRENCH_LOCALE: Locale = Locale.FRANCE
        val ENGLISH_LOCALE: Locale = Locale.UK

        val utcTimeZone: TimeZone = TimeZone.getTimeZone("UTC")

        fun newCalendar(): Calendar = Calendar.getInstance(utcTimeZone)

        fun newCalendar(year: Int, month: Int, day: Int): Calendar =
            newCalendar().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

        val monthsIndexes = ImmutableMap.builder<String, Int>()
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

        val englishMonths = arrayOf(
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

        val frenchMonths = arrayOf(
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

        val arabicMonths = arrayOf(
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
    }
}