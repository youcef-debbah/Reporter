package dz.nexatech.reporter.client.ui

import android.content.res.Resources
import com.google.firebase.FirebaseApp
import dagger.Lazy
import dagger.hilt.android.HiltAndroidApp
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.client.common.atomicLazy
import dz.nexatech.reporter.client.common.withIO
import dz.nexatech.reporter.client.core.AbstractBinaryResource
import dz.nexatech.reporter.client.core.AbstractInputRepository
import dz.nexatech.reporter.client.core.ValueUpdate
import dz.nexatech.reporter.client.model.InputRepository
import dz.nexatech.reporter.client.model.ResourcesRepository
import dz.nexatech.reporter.util.model.AppConfig
import dz.nexatech.reporter.util.ui.AbstractApplication
import dz.nexatech.reporter.util.ui.StandardDestinations
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

    override val config: Config by atomicLazy {
        Config(resources)
    }

    @Inject
    fun initAppConfig(firebaseApp: Lazy<FirebaseApp>) {
        AppConfig.init(this, firebaseApp)
    }

    @Inject
    fun initResourcesLoader(
        resourcesRepository: ResourcesRepository,
        inputRepository: InputRepository,
    ) {
        ResourcesHandler.init(resourcesRepository)
        InputHandler.init(inputRepository)
    }
}

object ResourcesHandler {
    private lateinit var resourcesRepository: ResourcesRepository

    fun init(resourcesRepository: ResourcesRepository) {
        this.resourcesRepository = resourcesRepository
    }

    suspend fun load(path: String?): AbstractBinaryResource? = withIO {
        resourcesRepository.load(path)
    }
}

object InputHandler : AbstractInputRepository() {
    private lateinit var inputRepository: InputRepository

    fun init(inputRepository: InputRepository) {
        this.inputRepository = inputRepository
    }

    override fun execute(update: ValueUpdate) = inputRepository.execute(update)
}