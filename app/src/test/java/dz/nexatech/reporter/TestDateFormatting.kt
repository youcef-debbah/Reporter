package dz.nexatech.reporter

import com.google.common.truth.Truth.assertThat
import dz.nexatech.reporter.util.model.Localizer
import org.junit.Test

class TestDateFormatting {

    val localizer = Localizer.from("fr")
    val testEpoch = 1687258800000L
    val testDate = "20 Juin 2023"
    val epochEveDate = "31 Décembre 1969"

    @Test
    fun testTemplateDateFormatting() {
        assertThat(localizer.formatSimpleDate(testEpoch)).isEqualTo(testDate)
        assertThat(localizer.formatSimpleDate(null)).isNull()
        assertThat(localizer.formatSimpleDate(-1)).isEqualTo(epochEveDate)
    }

    @Test
    fun testTemplateDateParsingErrors() {
        assertThat(localizer.parseSimpleDate("")).isNull()
        assertThat(localizer.parseSimpleDate("123")).isNull()
        assertThat(localizer.parseSimpleDate("1234567890123")).isNull()
    }

    @Test
    fun testParsingEpoch() {
        assertThat(localizer.parseSimpleDate(testDate)).isEqualTo(testEpoch)
    }

    @Test
    fun testParsingBothWays() {
        assertThat(localizer.formatSimpleDate(localizer.parseSimpleDate(testDate)))
            .isEqualTo(testDate)
    }
}