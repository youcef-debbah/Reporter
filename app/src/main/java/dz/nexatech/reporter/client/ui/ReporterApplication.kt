package dz.nexatech.reporter.client.ui

//import dz.nexatech.reporter.client.BuildConfig
import android.content.res.Resources
import com.google.firebase.FirebaseApp
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.util.model.AppConfig
import dz.nexatech.reporter.util.ui.AbstractApplication
import dz.nexatech.reporter.util.ui.StandardDestinations
import dagger.Lazy
import dagger.hilt.android.HiltAndroidApp
import dz.nexatech.reporter.client.model.ResourcesHandler
import dz.nexatech.reporter.client.model.ResourcesRepository
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
    fun initAppConfig(firebaseApp: Lazy<FirebaseApp>) {
        AppConfig.init(this, firebaseApp)
    }

    @Inject
    fun initResourcesLoader(resourcesRepository: ResourcesRepository) {
        ResourcesHandler.init(resourcesRepository)
    }
}