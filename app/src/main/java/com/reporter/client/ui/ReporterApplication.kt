package com.reporter.client.ui

//import com.reporter.client.BuildConfig
import android.content.res.Resources
import com.google.firebase.FirebaseApp
import com.reporter.client.R
import com.reporter.client.model.ALL_REMOTE_REPORTER_CONFIGS
import com.reporter.util.model.ALL_REMOTE_GLOBAL_CONFIGS
import com.reporter.util.model.AppConfig
import com.reporter.util.ui.AbstractApplication
import com.reporter.util.ui.StandardDestinations
import dagger.Lazy
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ReporterApplication : AbstractApplication() {

    class Config(res: Resources) : AbstractConfig {
        override val versionName: String = res.getString(R.string.version_name)
        override val isTestVersion: Boolean = res.getString(R.string.is_test_version).toBoolean()
        override val buildEpoch: Long = res.getString(R.string.build_epoch).toLong()
        override val applicationName: String = res.getString(R.string.app_name)
        override val standardDestinations: StandardDestinations = ReporterStandardDestinations
    }

    override val config: Config by lazy {
        Config(resources)
    }

    @Inject
    fun initAppConfig(firebaseApp: Lazy<FirebaseApp>) = AppConfig.init(
        firebaseApp,
        ALL_REMOTE_GLOBAL_CONFIGS,
        ALL_REMOTE_REPORTER_CONFIGS
    )
}