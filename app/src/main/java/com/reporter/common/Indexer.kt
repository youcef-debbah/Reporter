package com.reporter.common

import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import org.apache.commons.codec.language.DoubleMetaphone

object Indexer {

    private val indexIgnoredLetters: ImmutableSet<Char> = ImmutableSet.builder<Char>().apply {
        addAll(Texts.ARABIC_VOWELS)
        addAll(Texts.ARABIC_DIACRITICS)
        addAll(Texts.EXTRA_ALEPH_LETTER_FORMS)
        addAll(Texts.NON_ALEPH_HAMZA_LETTERS)
        add(Texts.ARABIC_UNDERSCORE)
    }.build()

    private val indexIncludedLetters: ImmutableMap<Char, Char> = ImmutableMap.Builder<Char, Char>().apply {
        for (letter in Texts.ALPHABET) put(letter, letter)
        for (letterForms in Texts.EXTRA_LATIN_LETTERS_FORMS) {
            val originalLetterForm = letterForms.key
            for (letterForm in letterForms.value) put(letterForm, originalLetterForm)
        }
        for (letter in Texts.ARABIC_CONSONANTS) put(letter, letter)
        put(Texts.TAH_MARBOOTAH, 'ت')
    }.build()

    private val normalizedIndexIgnoredLetters: ImmutableSet<Char> = ImmutableSet.builder<Char>().apply {
        addAll(Texts.ARABIC_DIACRITICS)
        add(Texts.ARABIC_UNDERSCORE)
    }.build()

    private val normalizedIndexIncludedLetter: ImmutableMap<Char, Char> = ImmutableMap.Builder<Char, Char>().apply {
        putAll(indexIncludedLetters)
        for (letter in Texts.ARABIC_VOWELS) put(letter, letter)
        for (letter in Texts.NON_ALEPH_HAMZA_LETTERS) put(letter, letter)
        for (letter in Texts.EXTRA_ALEPH_LETTER_FORMS) put(letter, letter)
    }.build()

    fun index(input: String?): String = joinAll(indexedTokens(input), String?::orEmpty)

    fun indexedTokens(input: String?): Set<String> = calcIndexTokens(input).build()

    fun indexAll(vararg input: Any?): String {
        val builder = ImmutableSet.builder<String>()
        for (value in input) {
            calcIndexTokens(value?.toString(), builder)
        }
        return joinAll(builder.build(), String?::orEmpty)
    }

    private fun calcIndexTokens(
        input: String?,
        result: ImmutableSet.Builder<String> = ImmutableSet.builder(),
    ): ImmutableSet.Builder<String> = parseToTokens(input, result, indexIncludedLetters, indexIgnoredLetters)

    private fun calcNormalizedIndexTokens(
        input: String?,
        result: ImmutableSet.Builder<String> = ImmutableSet.builder(),
    ): ImmutableSet.Builder<String> =
        parseToTokens(input, result, normalizedIndexIncludedLetter, normalizedIndexIgnoredLetters)

    fun phoneticIndex(input: String?): String = joinAll(phoneticIndexedTokens(input), String?::orEmpty)

    fun phoneticIndexedTokens(input: String?): Set<String> = calcPhoneticIndexTokens(input).build()

    fun phoneticIndexAll(vararg input: Any?): String {
        val builder = ImmutableSet.builder<String>()
        for (value in input) {
            calcPhoneticIndexTokens(value?.toString(), builder)
        }
        return joinAll(builder.build(), String?::orEmpty)
    }

    fun normalizedIndex(input: String?): String = joinAll(normalizedIndexedTokens(input), ::normalizeLatIndex)

    fun normalizedIndexedTokens(input: String?): Set<String> = calcNormalizedIndexTokens(input).build()
}

private fun parseToTokens(
    input: String?,
    result: ImmutableSet.Builder<String>,
    included: ImmutableMap<Char, Char>,
    ignored: ImmutableSet<Char>,
): ImmutableSet.Builder<String> {
    var buffer = StringBuilder()
    if (input.isNotNullOrEmpty()) {
        val text = input.removePrefix("ال").replace(" ال", Texts.SPACE).lowercase()
        val iterator = text.iterator()
        while (iterator.hasNext()) {
            val char = iterator.nextChar()
            val letter = included[char]
            if (letter != null) {
                buffer.append(letter)
            } else if (buffer.isNotEmpty() && !ignored.contains(char)) {
                result.add(buffer.toString())
                buffer = StringBuilder()
            }
        }
    }

    if (buffer.isNotEmpty()) result.add(buffer.toString())

    return result
}

private fun calcPhoneticIndexTokens(
    input: String?,
    result: ImmutableSet.Builder<String> = ImmutableSet.builder(),
): ImmutableSet.Builder<String> {
    val tokens = Indexer.indexedTokens(input)
    if (tokens.isNotEmpty()) {
        val encoder = DoubleMetaphone()
        for (token in tokens) {
            if (token.length > 2) {
                val normalizedToken: String = normalizeLatIndex(token)
                if (normalizedToken.length > 2) result.add(encoder.encode(normalizedToken))
            }
        }
    }
    return result
}

private fun normalizeLatIndex(token: String?): String {
    return if (token == null) {
        ""
    } else if (token.length > 1) {
        when (token[0]) {
            'a' -> removeAbdOrAl(token)
            'b' -> removeBen(token)
            'e' -> removeEl(token)
            else -> token
        }
    } else {
        token
    }
}

private fun removeAbdOrAl(aPrefixedToken: String): String {
    val length = aPrefixedToken.length

    if ((length == 2 || length > 4) && aPrefixedToken[1] == 'l') {
        return aPrefixedToken.substring(2)
    }

    if (length > 2 && aPrefixedToken[1] == 'b') {
        var index = 2
        if (aPrefixedToken[index] == 'b') index++

        if (index < length && aPrefixedToken[index] == 'd') {
            index++
            if (index < length && Texts.MAIN_VOWELS.contains(aPrefixedToken[index])) {
                index++
                if (index < length && aPrefixedToken[index] == 'l') index++
            }

            return aPrefixedToken.substring(index)
        }
    }

    return aPrefixedToken
}

private fun removeBen(bPrefixedToken: String): String {
    val second = bPrefixedToken[1]
    return if (bPrefixedToken.length == 2 && bPrefixedToken[1] == 'n') {
        ""
    } else if (bPrefixedToken.length > 2 && (second == 'e' || second == 'a') && bPrefixedToken[2] == 'n') {
        bPrefixedToken.substring(3)
    } else {
        bPrefixedToken
    }
}

private fun removeEl(ePrefixedToken: String): String = if (ePrefixedToken[1] == 'l') ePrefixedToken.substring(2)
else ePrefixedToken.substring(1)

private fun <T> joinAll(values: Iterable<T?>, mapper: (T?) -> String): String = StringBuilder().apply {
    for (value in values) {
        val part = mapper.invoke(value)
        if (part.isNotEmpty()) {
            if (this.isNotEmpty()) this.append(Texts.SPACE)
            this.append(part)
        }
    }
}.toString()