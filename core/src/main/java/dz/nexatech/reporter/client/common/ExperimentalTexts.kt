@file:OptIn(ExperimentalContracts::class)

package dz.nexatech.reporter.client.common

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

fun CharSequence?.isNotNullOrEmpty(): Boolean {
    contract {
        returns(true) implies (this@isNotNullOrEmpty != null)
    }

    return !isNullOrEmpty()
}

fun CharSequence?.isNotNullOrBlank(): Boolean {
    contract {
        returns(true) implies (this@isNotNullOrBlank != null)
    }

    return !isNullOrBlank()
}