package dz.nexatech.reporter.client.common

import java.util.Locale

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
        arabicText: String?
    ): String

    abstract fun inSecondaryLang(latinText: String?, arabicText: String?): String

    abstract fun inSecondaryLang(
        englishText: String?,
        frenchText: String?,
        arabicText: String?
    ): String
}