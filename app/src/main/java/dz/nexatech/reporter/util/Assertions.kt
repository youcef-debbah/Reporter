package dz.nexatech.reporter.util

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
inline fun <T> T.alsoDebug(block: (T) -> Unit): T {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    if (BuildTypeSettings.DEBUG) {
        block(this)
    }
    return this
}

@OptIn(ExperimentalContracts::class)
fun <T> T.assertContainedIn(possibleValuesSupplier: () -> Collection<T>): T {
    contract {
        callsInPlace(possibleValuesSupplier, InvocationKind.AT_MOST_ONCE)
    }
    if (BuildTypeSettings.DEBUG) {
        val possibleValues = possibleValuesSupplier()
        if (!possibleValues.contains(this)) throw IllegalArgumentException("the value: $this is not included in $possibleValues")
    }
    return this
}