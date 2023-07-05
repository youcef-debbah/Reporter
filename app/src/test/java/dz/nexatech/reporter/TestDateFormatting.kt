package dz.nexatech.reporter

import com.google.common.collect.ImmutableSet
import com.google.common.truth.Truth.assertThat
import dz.nexatech.reporter.util.model.Localizer
import org.junit.Test
import java.util.Calendar
import java.util.Date

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

    @Test
    fun name() {
        println(Date(829090800000))
    }

    @Test
    fun maxAndMinDateEpoch() {
        val day = 7
        val month = 11
        val year = 1995

        val min = Localizer.newCalendar().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        println("min: $min")

        val max = Localizer.newCalendar().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_MONTH, 1)
        }.timeInMillis - 1
        println("max: $max")
    }

    @Test
    fun set() {
        println(ImmutableSet.Builder<Data>()
            .add(Data("one", 1))
            .add(Data("two", 1))
            .add(Data("two", 2))
            .build())
    }
}

class Data(val name: String, val version: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Data

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return "Data(name=$name, version=$version)"
    }


}