package dz.nexatech.reporter.client.core
import dz.nexatech.reporter.client.common.asDoubleOrNull
import io.pebbletemplates.pebble.extension.AbstractExtension
import io.pebbletemplates.pebble.template.EvaluationContext
import io.pebbletemplates.pebble.template.PebbleTemplate
import java.util.Locale
import io.pebbletemplates.pebble.extension.Function as PebbleFunction

object ReporterExtension : AbstractExtension() {
    override fun getFunctions(): MutableMap<String, PebbleFunction> =
        HashMap<String, PebbleFunction>().apply {
            put("prod", Product())
            put("sum", Sum())
            put("formatDouble", FormatDouble())
        }

    class FormatDouble : PebbleFunction {
        override fun getArgumentNames(): MutableList<String>? = null

        override fun execute(
            args: MutableMap<String, Any>?,
            self: PebbleTemplate?,
            context: EvaluationContext?,
            lineNumber: Int,
        ): Any? {
            if (args.isNullOrEmpty()) return null
            var format: String? = null
            val doubleArgs = ArrayList<Any>(args.size - 1)
            for (entry in args) {
                if (entry.key == "0") {
                    format = entry.value.toString()
                } else {
                    val doubleArg = entry.value.asDoubleOrNull() ?: continue
                    doubleArgs.add(doubleArg)
                }
            }
            if (format == null || doubleArgs.isEmpty()) {
                return null
            } else {
                return format.format(locale = Locale.FRANCE, args = doubleArgs.toArray())
            }
        }
    }

    class Product : Aggregator() {
        override fun aggregate(result: Double?, newValue: Double): Double =
            if (result == null) newValue else result * newValue
    }

    class Sum : Aggregator() {
        override fun aggregate(result: Double?, newValue: Double): Double =
            if (result == null) newValue else result + newValue
    }

    abstract class Aggregator : PebbleFunction {
        override fun getArgumentNames(): MutableList<String>? = null

        override fun execute(
            args: MutableMap<String, Any>?,
            self: PebbleTemplate?,
            context: EvaluationContext?,
            lineNumber: Int,
        ): Any? {
            var result: Double? = null
            if (args != null) {
                for (value in args.values) {
                    val doubleValue = value.asDoubleOrNull() ?: continue
                    result = aggregate(result, doubleValue)
                }
            }
            return result
        }

        abstract fun aggregate(result: Double?, newValue: Double): Double
    }
}