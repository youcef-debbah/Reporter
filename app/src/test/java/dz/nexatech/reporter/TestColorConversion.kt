package dz.nexatech.reporter

import com.google.common.truth.Truth.assertThat
import dz.nexatech.reporter.client.model.Variable
import org.junit.Test

class TestColorConversion {

    @Test
    fun testColorValueParsing() {
        assertThat(Variable.Type.Color.parseColor("#00FF00")).isEqualTo(0xFF00FF00)
    }
}