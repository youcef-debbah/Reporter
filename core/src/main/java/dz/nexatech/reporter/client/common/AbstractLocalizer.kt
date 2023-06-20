package dz.nexatech.reporter.client.common

abstract class AbstractLocalizer() {

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

    abstract fun monthName(monthIndex: Int): String

    abstract fun monthIndex(monthName: String): Int?
}