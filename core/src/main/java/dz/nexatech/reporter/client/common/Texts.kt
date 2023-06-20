package dz.nexatech.reporter.client.common

import com.google.common.collect.ImmutableCollection
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Integer.max
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.min

object Texts {

    const val ASSETS_URL_PREFIX: String = "file:///android_asset/"
    val NEW_LINE: String = System.getProperty("line.separator")!!

    const val NB_SPACE_CHAR: Char = '\u00A0'
    const val SPACE_CHAR: Char = ' '
    const val SPACE: String = SPACE_CHAR.toString()
    const val DATA_SEPARATOR_CHAR: Char = ';'
    const val DATA_SEPARATOR: String = DATA_SEPARATOR_CHAR.toString()

    const val NOT_AVAILABLE = "N/A"
    const val NULL = "null"
    const val FALSE = "false"
    const val TRUE = "true"
    const val KEY_VALUE_SEPARATOR = "="

    const val ASSETS_PREFIX = "/android_asset/"
    const val FILE_PROTOCOL_ASSETS_PREFIX = "file://$ASSETS_PREFIX"

    const val UTF_8 = "UTF-8";

    val ZEROS: Array<String> = Array(20) { "0".repeat(it) }
    const val MIN_ABS_INT = "2147483648"
    const val MIN_ABS_LONG = "9223372036854775808"

    private const val ARABIC_CHARS =
        "\u0623\u0625\u0622\u0626\u0630\u0621\u0624\u0631\u0649\u0629" +
                "\u0648\u0632\u0638\u062F\u0634\u0633\u064A\u0628\u0644\u0627\u062A\u0646\u0645\u0643" +
                "\u0637\u0636\u0635\u062B\u0642\u0641\u063A\u0639\u0647\u062E\u062D\u062C"
    private const val ARABIC_NUMBERS_CHARS =
        "\u0661\u0662\u0663\u0664\u0665\u0666\u0667\u0668\u0669\u0660"
    private const val ARABIC_EXTRA_CHARS =
        "\u060C\u060C\u061F\u0651\u064D\u0650\u0652\u064C\u064F\u064B\u064E"

    private const val FRENCH_ACCENTS_CHARS = "\u00E7\u00E9\u00E2\u00EA\u00EE\u00F4\u00FB\u00E0" +
            "\u00E8\u00F9\u00EB\u00EF\u00FC\u00C7\u00C9\u00C2\u00CA\u00CE\u00D4\u00DB\u00C0\u00C8" +
            "\u00D9\u00CB\u00CF\u00DC"

    val ALPHABET_ARRAY = arrayOf(
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
        'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    )

    val NUMBERS_ARRAY = arrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')

    val SWAPPED_DIGITS: ImmutableMap<Char, Char> = ImmutableMap.builder<Char, Char>()
        .put('0', '9')
        .put('1', '8')
        .put('2', '7')
        .put('3', '6')
        .put('4', '5')
        .put('5', '4')
        .put('6', '3')
        .put('7', '2')
        .put('8', '1')
        .put('9', '0')
        .build()

    val ALPHABET: ImmutableList<Char> =
        ImmutableList.Builder<Char>().addAll(ALPHABET_ARRAY.iterator()).build()

    val MAIN_VOWELS: ImmutableList<Char> = ImmutableList.of('a', 'e', 'i', 'o', 'u')

    val ARABIC_CONSONANTS: ImmutableList<Char> = ImmutableList.of(
        '\u0636',
        '\u0635',
        '\u062B',
        '\u0642',
        '\u0641',
        '\u063A',
        '\u0639',
        '\u0647',
        '\u062E',
        '\u062D',
        '\u062C',
        '\u0634',
        '\u0633',
        '\u0628',
        '\u0644',
        '\u062A',
        '\u0646',
        '\u0645',
        '\u0643',
        '\u0637',
        '\u0630',
        '\u0631',
        '\u0632',
        '\u0638',
        '\u062F'
    )
    val ARABIC_VOWELS: ImmutableList<Char> = ImmutableList.of('\u0627', '\u0648', '\u064A')
    val ARABIC_DIACRITICS: ImmutableList<Char> =
        ImmutableList.of(
            '\u0651',
            '\u064E',
            '\u064B',
            '\u064F',
            '\u064C',
            '\u0650',
            '\u064D',
            '\u0652'
        )

    val EXTRA_ALEPH_LETTER_FORMS: ImmutableList<Char> =
        ImmutableList.of('\u0622', '\u0623', '\u0625', '\u0649')
    val NON_ALEPH_HAMZA_LETTERS: ImmutableList<Char> =
        ImmutableList.of('\u0626', '\u0621', '\u0624')
    const val TAH_MARBOOTAH = '\u0629'
    const val ARABIC_UNDERSCORE = '\u0640'

    val ARABIC_LETTERS: ImmutableSet<Char> = ImmutableSet.Builder<Char>()
        .addAll(ARABIC_CONSONANTS)
        .addAll(ARABIC_VOWELS)
        .addAll(EXTRA_ALEPH_LETTER_FORMS)
        .addAll(NON_ALEPH_HAMZA_LETTERS)
        .add(TAH_MARBOOTAH)
        .add(ARABIC_UNDERSCORE)
        .build()

    val EXTRA_A_LETTER_FORMS: ImmutableList<Char> = ImmutableList.of(
        '\u00E0', '\u00E2', '\u00E1', '\u00E3', '\u00E4',
        '\u00E5', '\u00E6', '\u0101', '\u0103', '\u0105'
    )
    val EXTRA_Z_LETTER_FORMS: ImmutableList<Char> = ImmutableList.of('\u017A', '\u017C', '\u017E')
    val EXTRA_E_LETTER_FORMS: ImmutableList<Char> = ImmutableList.of(
        '\u00E9', '\u00E8', '\u00EA', '\u00EB', '\u0113',
        '\u0117', '\u0119', '\u011B', '\u0115', '\u0259'
    )
    val EXTRA_R_LETTER_FORMS: ImmutableList<Char> = ImmutableList.of('\u0155', '\u0159')
    val EXTRA_T_LETTER_FORMS: ImmutableList<Char> =
        ImmutableList.of('\u00FE', '\u0165', '\u021B', '\u0163')
    val EXTRA_Y_LETTER_FORMS: ImmutableList<Char> = ImmutableList.of('\u00FD')
    val EXTRA_U_LETTER_FORMS: ImmutableList<Char> =
        ImmutableList.of(
            '\u00FB',
            '\u00F9',
            '\u00FA',
            '\u00FC',
            '\u016B',
            '\u016F',
            '\u0171',
            '\u0173'
        )
    val EXTRA_I_LETTER_FORMS: ImmutableList<Char> =
        ImmutableList.of('\u00EE', '\u00EF', '\u00EC', '\u00ED', '\u012B', '\u012F', '\u0131')
    val EXTRA_O_LETTER_FORMS: ImmutableList<Char> =
        ImmutableList.of(
            '\u00F3',
            '\u00F4',
            '\u0153',
            '\u00F2',
            '\u00F5',
            '\u00F6',
            '\u00F8',
            '\u0151'
        )
    val EXTRA_S_LETTER_FORMS: ImmutableList<Char> =
        ImmutableList.of('\u00DF', '\u00A7', '\u015B', '\u0161', '\u015F')
    val EXTRA_D_LETTER_FORMS: ImmutableList<Char> = ImmutableList.of('\u010F', '\u0111')
    val EXTRA_G_LETTER_FORMS: ImmutableList<Char> = ImmutableList.of('\u0123', '\u011F')
    val EXTRA_K_LETTER_FORMS: ImmutableList<Char> = ImmutableList.of('\u0137')
    val EXTRA_L_LETTER_FORMS: ImmutableList<Char> =
        ImmutableList.of('\u013A', '\u013C', '\u013E', '\u0142')
    val EXTRA_C_LETTER_FORMS: ImmutableList<Char> = ImmutableList.of('\u00E7', '\u0107', '\u010D')
    val EXTRA_N_LETTER_FORMS: ImmutableList<Char> =
        ImmutableList.of('\u00F1', '\u0144', '\u0146', '\u0148')

    val EXTRA_LATIN_LETTERS_FORMS: ImmutableMap<Char, List<Char>> =
        ImmutableMap.builder<Char, List<Char>>()
            .put('a', EXTRA_A_LETTER_FORMS)
            .put('z', EXTRA_Z_LETTER_FORMS)
            .put('e', EXTRA_E_LETTER_FORMS)
            .put('r', EXTRA_R_LETTER_FORMS)
            .put('t', EXTRA_T_LETTER_FORMS)
            .put('y', EXTRA_Y_LETTER_FORMS)
            .put('u', EXTRA_U_LETTER_FORMS)
            .put('i', EXTRA_I_LETTER_FORMS)
            .put('o', EXTRA_O_LETTER_FORMS)
            .put('s', EXTRA_S_LETTER_FORMS)
            .put('d', EXTRA_D_LETTER_FORMS)
            .put('g', EXTRA_G_LETTER_FORMS)
            .put('k', EXTRA_K_LETTER_FORMS)
            .put('l', EXTRA_L_LETTER_FORMS)
            .put('c', EXTRA_C_LETTER_FORMS)
            .put('n', EXTRA_N_LETTER_FORMS)
            .build()

    const val FRENCH_LETTER_REGEX_GROUP_CONTENT = "a-zA-Z$FRENCH_ACCENTS_CHARS"
    const val ARABIC_LETTER_REGEX_GROUP_CONTENT = ARABIC_CHARS
    const val ARABIC_FRENCH_LETTER_REGEX_GROUP_CONTENT = "a-zA-Z$FRENCH_ACCENTS_CHARS$ARABIC_CHARS"

    val NORMALIZED_PHONE_PATTERN: Pattern = Pattern.compile("^\\+213\\d{9}$")
    val INPUT_PHONE_PATTERN: Pattern = Pattern.compile("^\\d{9}$")

    val LANG_EN: String = Locale("en").language
    val LANG_FR: String = Locale("fr").language
    val LANG_AR: String = Locale("ar").language
    val SUPPORTED_LANGUAGES: List<String> = ImmutableList.of(LANG_EN, LANG_FR, LANG_AR)

    val WHITESPACE_REGEX = Regex("\\s+")

    fun inPrimaryLang(
        englishText: String?,
        frenchText: String?,
        arabicText: String?,
        languages: List<String>,
    ): String {
        for (language in languages) {
            if (language == LANG_EN) {
                if (englishText.isNotNullOrBlank()) return englishText else continue
            } else if (language == LANG_FR) {
                if (frenchText.isNotNullOrBlank()) return frenchText else continue
            } else if (language == LANG_AR) {
                if (arabicText.isNotNullOrBlank()) return arabicText
            }
        }
        return ""
    }

    fun inSecondaryLang(
        englishText: String?,
        frenchText: String?,
        arabicText: String?,
        languages: List<String>,
    ): String {
        var primary: String? = null
        for (language in languages) {
            if (language == LANG_EN) {
                if (englishText.isNotNullOrBlank()) {
                    if (primary != null && primary != englishText) {
                        return englishText
                    } else {
                        primary = englishText
                    }
                }
            } else if (language == LANG_FR) {
                if (frenchText.isNotNullOrBlank()) {
                    if (primary != null && primary != frenchText) {
                        return frenchText
                    } else {
                        primary = frenchText
                    }
                }
            } else if (language == LANG_AR) {
                if (arabicText.isNotNullOrBlank()) {
                    if (primary != null && primary != arabicText) {
                        return arabicText
                    } else {
                        primary = arabicText
                    }
                }
            }
        }
        return ""
    }
}

fun String.preFill(targetLength: Int, filler: Char = '0') = if (targetLength < 1) {
    throw StrictlyPositiveNumberExpectedException("targetLength", targetLength)
} else if (length < targetLength) {
    val builder = StringBuilder(targetLength)
    repeat(targetLength - length) {
        builder.append(filler)
    }
    builder.append(this).toString()
} else {
    this
}

fun String.postFill(targetLength: Int, filler: Char = '0'): String = if (targetLength < 1) {
    throw StrictlyPositiveNumberExpectedException("targetLength", targetLength)
} else if (length < targetLength) {
    val builder = StringBuilder(targetLength).append(this)
    repeat(targetLength - length) {
        builder.append(filler)
    }
    builder.toString()
} else {
    this
}

fun Int.digitsCount(): Int =
    if (this == 0) 1 else if (this == Integer.MIN_VALUE) 10 else log10(abs(this).toDouble()).toInt() + 1

fun Long.digitsCount(): Int =
    if (this == 0L) 1 else if (this == Long.MIN_VALUE) 10 else log10(abs(this).toDouble()).toInt() + 1

fun Int.toPaddedString(max: Int): String = String.format("%0${max.digitsCount()}d", this)

fun Long.toPaddedString(max: Long): String = String.format("%0${max.digitsCount()}d", this)

fun Int.format(length: Int = 10): String {
    if (length <= 0 || length >= Texts.ZEROS.size) throw IllegalArgumentException("illegal length: $length")
    if (this == 0) return Texts.ZEROS[length]
    val abs = if (this == Int.MIN_VALUE) Texts.MIN_ABS_INT else abs(this).toString()
    val absLength = max(length, abs.length)
    val buffer = if (this < 0) {
        StringBuilder(absLength + 1).append("-")
    } else {
        StringBuilder(absLength)
    }
    return buffer
        .append(Texts.ZEROS[max(0, length - abs.length)])
        .append(abs)
        .toString()
}

fun Int.lexicalFormat(): String {
    if (this == 0) {
        return "b0"
    } else {
        val digitsCount = this.digitsCount()
        if (this > 0) {
            return StringBuilder(digitsCount + 1).append(Texts.ALPHABET_ARRAY[digitsCount])
                .append(this).toString()
        } else if (this == Int.MIN_VALUE) {
            return "!p" + Texts.MIN_ABS_INT
        } else {
            val buffer = StringBuilder(digitsCount + 2)
            buffer.append('!')
            buffer.append(Texts.ALPHABET_ARRAY[Texts.ALPHABET_ARRAY.size - 1 - digitsCount])
            val absValue = abs(this).toString()
            for (digit in absValue) {
                buffer.append(Texts.SWAPPED_DIGITS[digit] ?: digit)
            }
            return buffer.toString()
        }
    }
}

class StrictlyPositiveNumberExpectedException(paramName: String, value: Int) :
    IllegalArgumentException("$paramName must be strictly greater than zero, found: $value")

class PositiveNumberExpectedException(paramName: String, value: Int) :
    IllegalArgumentException("$paramName must be greater or equal to zero, found: $value")

@Suppress("ReplaceIsEmptyWithIfEmpty")
fun <C : CharSequence> C.nullIfEmpty(): C? = if (isEmpty()) null else this

@Suppress("ReplaceIsEmptyWithIfEmpty")
fun <C : CharSequence> C.nullIfBlank(): C? = if (isBlank()) null else this

fun <T : ImmutableCollection.Builder<String>> String.splitInto(delimiter: Char, builder: T): T {
    val iterator = this.iterator()
    var index = 0
    var buffer = StringBuilder(this.length)
    while (iterator.hasNext()) {
        index++
        val currentChar = iterator.nextChar()
        if (currentChar != delimiter) {
            buffer.append(currentChar)
        } else {
            if (buffer.isNotEmpty()) {
                val token = buffer.toString()
                builder.add(token)
                buffer = StringBuilder(this.length - index)
            }
        }
    }

    if (buffer.isNotEmpty()) {
        builder.add(buffer.toString())
    }

    return builder
}

fun String.splitIntoSet(delimiter: Char): ImmutableSet<String> =
    if (isEmpty()) ImmutableSet.of() else splitInto(delimiter, ImmutableSet.Builder()).build()

fun String.splitIntoList(delimiter: Char): ImmutableList<String> =
    if (isEmpty()) ImmutableList.of() else splitInto(delimiter, ImmutableList.Builder()).build()

fun String.containsAll(substrings: Iterable<String>): Boolean {
    val iterator = substrings.iterator()
    while (iterator.hasNext()) {
        if (!this.contains(iterator.next()))
            return false
    }
    return true
}

fun Any?.classDescription(): String {
    return if (this == null) "null" else this::class.java.description()
}

fun Class<*>.description(): String {
    val canonicalName = this.canonicalName
    if (canonicalName != null) {
        return canonicalName
    } else {
        val description = StringBuilder()
        if (this.isAnonymousClass) description.append("Anonymous class")
        else if (this.isLocalClass) description.append("Local class")
        else description.append("Unknown class")

        this.declaringClass?.let {
            description.append(" declared inside " + it.description())
        }

        this.enclosingClass?.let {
            description.append(" enclosed in " + it.description())
        }

        return description.toString()
    }
}

fun String.joinWith(another: String, separator: Char = Texts.SPACE_CHAR): String {
    if (another.isEmpty()) {
        return this
    } else if (this.isEmpty()) {
        return another
    } else {
        return this + separator + another
    }
}

fun InputStream.readAsString(
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    autoClose: Boolean = true,
): String = try {
    val buffer = CharArray(if (bufferSize < 1) DEFAULT_BUFFER_SIZE else bufferSize)
    val result = StringBuilder(buffer.size)
    val inputReader: (InputStreamReader) -> Unit = { reader ->
        var bytesRead: Int
        while (reader.read(buffer, 0, buffer.size).also { bytesRead = it } != -1) {
            result.appendRange(buffer, 0, bytesRead)
        }
    }
    inputReader.invoke(InputStreamReader(this, StandardCharsets.UTF_8))
    result.toString()
} finally {
    if (autoClose) {
        close()
    }
}

fun InputStream.readAsBytes(
    expectedSize: Int = -1,
    autoClose: Boolean = true,
): ByteArray = try {
    val bufferSize = if (expectedSize > 0) min(DEFAULT_BUFFER_SIZE, expectedSize) else DEFAULT_BUFFER_SIZE
    val buffer = ByteArray(bufferSize)
    val output = ByteArrayOutputStream()
    var bytesRead: Int
    while (this.read(buffer).also { bytesRead = it } != -1) {
        output.write(buffer, 0, bytesRead)
    }
    output.toByteArray()
} finally {
    if (autoClose) {
        close()
    }
}

object Formatters {
    val httpDateFormatter = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("GMT")
    }

    fun asHttpDate(epoch: Long): String = asHttpDate(Date(epoch))

    fun asHttpDate(date: Date): String = httpDateFormatter.format(date)
}

fun Int.addHash(instance: Any): Int = (31 * this) xor instance.hashCode()