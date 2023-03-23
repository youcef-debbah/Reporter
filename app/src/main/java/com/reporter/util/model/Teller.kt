package com.reporter.util.model

import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.ParametersBuilder
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.reporter.common.*
import com.reporter.util.ui.AbstractApplication
import dagger.Lazy
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import java.util.concurrent.ConcurrentSkipListSet
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.cancellation.CancellationException

object Teller : AbstractTeller() {

    private const val TAG_PREFIX = "teller_"
    private const val EVENT_TAG = TAG_PREFIX + "event"
    private const val LOG_TAG = TAG_PREFIX + "log"

    private val pendingLogs =
        Channel<Pair<String, suspend LogContext.() -> Unit>>(
            Channel.UNLIMITED,
            BufferOverflow.SUSPEND
        ) {
            logRareCondition("undelivered_log", it.first)
        }

    private val logInProgress = AtomicBoolean()

    private val reportedConditionsTags = ConcurrentSkipListSet<String>()

    fun setup(
        logDAO: Lazy<LogDAO>,
        analytics: Lazy<FirebaseAnalytics>,
        crashlytics: Lazy<FirebaseCrashlytics>,
    ) {
        val logContext = LogContext(logDAO, analytics, crashlytics)
        ioLaunch {
            debug("teller initialized")
            while (true) {
                val entry = pendingLogs.receive()
                try {
                    if (logInProgress.getAndSet(true)) {
                        logUnexpectedCondition(
                            "nested_logging",
                            "a log is queued inside another log job"
                        )
                    } else {
                        entry.second(logContext)
                    }
                } finally {
                    logInProgress.set(false)
                }
            }
        }
    }

    private class LogContext(
        private val lazyLogDAO: Lazy<LogDAO>,
        private val lazyAnalytics: Lazy<FirebaseAnalytics>,
        private val lazyCrashlytics: Lazy<FirebaseCrashlytics>,
    ) {
        val dao: LogDAO get() = lazyLogDAO.get()
        val analytics: FirebaseAnalytics get() = lazyAnalytics.get()
        val crashlytics: FirebaseCrashlytics get() = lazyCrashlytics.get()
    }

    private fun queueLog(msg: String, block: suspend LogContext.() -> Unit) {
        val result = pendingLogs.trySend(Pair(msg, block))
        if (result.isFailure) {
            logUnexpectedCondition("unsent_log", "LogQueue(closed: ${result.isClosed}): $msg")
        }
    }

    private class UnexpectedConditionException(message: String) : RuntimeException(message)
    private class RareConditionException(message: String) : RuntimeException(message)

    @PublicAPI
    override fun logUnexpectedCondition(tag: String, msg: String) {
        logCondition(tag, msg) { UnexpectedConditionException(it) }
    }

    @PublicAPI
    override fun logRareCondition(tag: String, msg: String) {
        logCondition(tag, msg) { RareConditionException(it) }
    }

    private fun logCondition(tag: String, msg: String, conditionException: (String) -> Exception) {
        Log.w(tag, msg)
        try {
            val crashlytics = GlobalSingletonModule.getFirebaseCrashlytics()
            if (reportedConditionsTags.add(tag)) {
                try {
                    val fullMsg = "$tag - (with GA event) $msg"
                    crashlytics.log(fullMsg)

                    val analytics =
                        GlobalSingletonModule.getFirebaseAnalytics(AbstractApplication.INSTANCE)
                    val bundle = Bundle()
                    bundle.putString(Event.UnexpectedConditionTag.Param.TAG, tag)
                    analytics.logEvent(Event.UnexpectedConditionTag.NAME, bundle)

                    throw conditionException(fullMsg)
                } catch (e: Exception) { // not suspended
                    crashlytics.recordException(e)
                }
            } else {
                try {
                    val fullMsg = "$tag - (without GA event) $msg"
                    crashlytics.log(fullMsg)
                    throw conditionException(fullMsg)
                } catch (e: Exception) { // not suspended
                    crashlytics.recordException(e)
                }
            }
        } catch (e: Exception) { // not suspended
            Log.e("crashlytics_failure", "could not log condition: $msg")
        }
    }

    @PublicAPI
    override fun logEvent(event: String) {
        logEvent(event, null)
    }

    @PublicAPI
    fun logEvent(event: String, bundle: Bundle?) {
        queueEvent(event) { bundle }
    }

    @PublicAPI
    override fun logEvent(event: String, bundleBuilder: ParamBuilder.() -> Unit) {
        queueEvent(event) { BundleParamBuilder().apply(bundleBuilder).bundle }
    }

    private fun queueEvent(event: String, bundleSupplier: () -> Bundle?) {
        queueLog("event: $event") {
            val bundle = bundleSupplier.invoke()
            val data = bundle?.toDataString()
            val eventWithData = if (bundle == null) event else "$event data: $data"
            Log.i(EVENT_TAG, eventWithData)
            analytics.logEvent(event, bundle)
            dao.log(LoggedEvent(Chaos.timestamp(), event, data))
        }
    }

    @PublicAPI
    override fun test(msg: String) {
        val testMsg = "£££ $msg"
        queueLog("test: $testMsg") {
            Log.w(LOG_TAG, testMsg)
            crashlytics.log("TEST - $testMsg")
        }
    }

    @OptIn(ExperimentalContracts::class)
    inline fun debug(msg: () -> String) {
        contract {
            callsInPlace(msg, InvocationKind.AT_MOST_ONCE)
        }
        debug(msg())
    }

    @OptIn(ExperimentalContracts::class)
    inline fun debug(startTime: Long, msg: () -> String) {
        contract {
            callsInPlace(msg, InvocationKind.AT_MOST_ONCE)
        }
        val now = System.nanoTime()
        debug("Took ${startTime.duration(now)}: ${msg()}")
    }

    @PublicAPI
    override fun debug(nanos: Long, msg: String) {
        val t0 = System.nanoTime()
        debug("Took ${nanos.duration(t0)} to: $msg")
    }

    @PublicAPI
    override fun debug(msg: String) {
        queueLog("debug: $msg") {
            Log.d(LOG_TAG, msg)
            crashlytics.log("DEBUG - $msg")
        }
    }

    @PublicAPI
    override fun info(msg: String) {
        queueLog("info: $msg") {
            Log.i(LOG_TAG, msg)
            crashlytics.log("INFO - $msg")
        }
    }

    @PublicAPI
    override fun warn(msg: String) {
        queueLog("warn: $msg") {
            Log.w(LOG_TAG, msg)
            crashlytics.log("WARN - $msg")
        }
    }

    @PublicAPI
    override fun warn(msg: String, e: Throwable?) {
        if (e == null) {
            return warn(msg)
        }

        if (e is CancellationException) {
            throw e
        }

        queueLog("warn(e.message: ${e.message}): $msg") {
            if (isRecordable(e)) {
                Log.w(LOG_TAG, msg, e)
                crashlytics.log("WARN - (Recorded Exception): $msg")
                crashlytics.recordException(e)
            } else {
                val msgText = "(Unrecorded Exception): $msg"
                Log.w(LOG_TAG, msgText, e)
                crashlytics.log("WARN - $msgText " + getSummary(e))
            }
        }
    }

    @PublicAPI
    override fun error(msg: String, e: Throwable) {
        if (e is CancellationException) {
            throw e
        }

        queueLog("error(e.message: ${e.message}): $msg") {
            if (isRecordable(e)) {
                Log.e(LOG_TAG, msg, e)
                crashlytics.log("ERROR - (Recorded Exception): $msg")
                crashlytics.recordException(e)
            } else {
                val msgText = "(Unrecorded Exception): $msg"
                Log.e(LOG_TAG, msgText, e)
                crashlytics.log("ERROR - $msgText " + getSummary(e))
            }
        }
    }

    private fun getSummary(e: Throwable): String {
        val summary = StringBuilder(128)
            .append(e.javaClass.name)
            .append(": ")
            .append(e.message)
            .append(getFirstRelatedStackStrace(e))
            .append(' ')

        var current = e.cause
        while (current != null) {
            summary.append("Caused by: ")
                .append(current.javaClass.name)
                .append(": ")
                .append(current.message)
                .append(' ')
            current = current.cause
        }

        return summary.toString()
    }

    private fun getFirstRelatedStackStrace(e: Throwable): String {
        val stackTrace = e.stackTrace
        for (element in stackTrace) if (element != null) {
            val className: String = element.className
            if (className.startsWith("com.reporter"))
                return " at $element"
        }
        return ""
    }

    private fun isRecordable(e: Throwable) =
        !e.isCausedBy(
            AppConfig.get(REMOTE_UNRECORDED_EXCEPTIONS)
                .split(Texts.DATA_SEPARATOR)
        )

    private fun Throwable.isCausedBy(exceptionsTypes: Collection<String>): Boolean {
        if (exceptionsTypes.isNotEmpty()) {
            var current: Throwable? = this
            while (current != null) {
                val throwableType: Class<out Throwable> = current.javaClass
                for (exceptionName in exceptionsTypes)
                    if (throwableType.isSubclassOf(exceptionName))
                        return true
                current = current.cause
            }
        }
        return false
    }

    private fun Class<*>?.isSubclassOf(targetTypeName: String?): Boolean {
        if (targetTypeName.isNotNullOrEmpty()) {
            var type = this
            while (type != null) {
                if (type.name == targetTypeName)
                    return true
                type = type.superclass
            }
        }
        return false
    }
}

class BundleParamBuilder : ParamBuilder {
    private val builder = ParametersBuilder()
    val bundle get() = builder.bundle
    fun param(key: String, value: Bundle) = builder.param(key, value)
    fun param(key: String, value: Array<Bundle>) = builder.param(key, value)
    override fun param(key: String, value: Double) = builder.param(key, value)
    override fun param(key: String, value: Long) = builder.param(key, value)
    override fun param(key: String, value: String) = builder.param(key, value)
}

@OptIn(ExperimentalContracts::class)
inline fun <T> T.alsoDebug(teller: Teller, startTime: Long? = null, msg: () -> String): T {
    contract {
        callsInPlace(msg, InvocationKind.AT_MOST_ONCE)
    }
    if (startTime != null) {
        val now = System.nanoTime()
        teller.debug("Took ${startTime.duration(now)}: ${msg()}")
    } else {
        teller.debug(msg())
    }
    return this
}