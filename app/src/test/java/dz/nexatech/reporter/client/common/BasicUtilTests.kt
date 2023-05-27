package dz.nexatech.reporter.client.common

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class BasicUtilTests {
    @Test
    fun slice() {
        val list = (1..7).toList()
        assertThat(emptyList<Int>().slice(3).toString()).isEqualTo("[]")
        assertThat(listOf(1).slice(3).toString()).isEqualTo("[[1]]")
        assertThat(list.slice(3).toString()).isEqualTo("[[1, 2, 3], [4, 5, 6], [7]]")

        assertThat(emptyList<Int>().slice(1).toString()).isEqualTo("[]")
        assertThat(listOf(1).slice(1).toString()).isEqualTo("[[1]]")
        assertThat(list.slice(1).toString()).isEqualTo("[[1], [2], [3], [4], [5], [6], [7]]")
    }
}