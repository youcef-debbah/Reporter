package com.reporter.util.ui

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.reporter.util.model.LogDAO
import com.reporter.util.model.Teller
import dagger.Lazy
import javax.inject.Inject

@Suppress("LeakingThis")
abstract class AbstractApplication : Application() {

    interface AbstractConfig {
        val versionName: String
        val isTestVersion: Boolean
        val buildEpoch: Long
        val applicationName: String
        val standardDestinations: StandardDestinations
    }

    companion object {
        lateinit var INSTANCE: AbstractApplication
    }

    init {
        INSTANCE = this
    }

    abstract val config: AbstractConfig

    @Inject
    fun initTeller(
        logDAO: Lazy<LogDAO>,
        analytics: Lazy<FirebaseAnalytics>,
        crashlytics: Lazy<FirebaseCrashlytics>,
    ) = Teller.setup(logDAO, analytics, crashlytics)
}