package dz.nexatech.reporter.gradle

import com.android.build.api.dsl.AnnotationProcessorOptions
import com.android.build.api.dsl.ApplicationBuildType
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File
import java.net.Inet4Address
import java.util.Properties

/**
 * the default IPv4 address of this machine in the local network or null
 * if this machine is not connected to any network
 */
val hostname: String by lazy { getLocalIPv4() ?: BuildSettings.DEFAULT_LOCALHOST }

private fun getLocalIPv4(): String? {
    for (network in java.net.NetworkInterface.getNetworkInterfaces())
        if (network.isUp && !network.isLoopback && !network.isVirtual)
            for (address in network.inetAddresses)
                if (!address.isLoopbackAddress && address is Inet4Address)
                    return address.hostAddress
    return null
}

/**
 * global project configs
 */
private val _buildConfig: Config? = null
val Project.buildConfig
    get() = _buildConfig ?: run {
        val properties = Properties()
        this.rootProject.file("buildSrc/gradle.properties").reader().use { properties.load(it) }

        Config(
            properties["build.version.hilt"]!!.toString(),
            properties["build.version.kotlin"]!!.toString(),
            JavaVersion.toVersion(properties["build.version.java"]!!),
        )
    }

data class Config(
    val hiltVersion: String,
    val kotlinVersion: String,
    val javaVersion: JavaVersion,
    val javaVersionName: String = javaVersion.toString(),
    val javaLanguageVersion: JavaLanguageVersion = JavaLanguageVersion.of(javaVersionName),
    val kotlinCompatibility: String = kotlinVersion.substring(0..2),
)

fun TestedExtension.addStandardAppBuildTypes(versionName: String) {
    buildTypes {
        named(BuildSettings.DEBUG_BUILD_TYPE) {
            addCommonBuildConfig()
            addDynamicConstants(versionName, true)
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
        }

        named(BuildSettings.RELEASE_BUILD_TYPE) {
            addCommonBuildConfig()
            addDynamicConstants(versionName, false)
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
//            signingConfig = signingConfigs.getByName("release")
        }
    }
}

fun TestedExtension.addStandardLibBuildTypes() {
    buildTypes {
        named(BuildSettings.DEBUG_BUILD_TYPE) {
            addCommonBuildConfig()
            isMinifyEnabled = false
        }

        named(BuildSettings.RELEASE_BUILD_TYPE) {
            addCommonBuildConfig()
            isMinifyEnabled = true
        }
    }
}

private fun ApplicationBuildType.addCommonBuildConfig(proguardFile: String = BuildSettings.DEFAULT_PROGUARD_FILE) {
    ndk {
        debugSymbolLevel = "FULL"
    }
    proguardFiles(File(proguardFile))
}

private fun ApplicationBuildType.addDynamicConstants(versionName: String, testVersion: Boolean) {
    resValue("string", "build_epoch", System.currentTimeMillis().toString())
    resValue("string", "is_test_version", testVersion.toString())
    resValue("string", "version_name", versionName)
}

fun AnnotationProcessorOptions.roomOptions(
    schemasPath: String,
//    incremental: Boolean = true,
//    expandProjection: Boolean = true,
) {
//    argument("room.incremental", incremental.toString())
//    argument("room.expandProjection", expandProjection.toString())
    compilerArgumentProvider(RoomCompilerArgumentProvider(schemasPath))
}

fun Project.standardAndroidLib(libNameSpace: String, vararg consumerProguardPaths: String) {
    standardProjectConfig()

    configurations.named("androidTestImplementation") {
        exclude(
            mapOf(
                "group" to "com.google.firebase",
                "module" to "firebase-perf"
            )
        )
    }

    (this as ExtensionAware).extensions.configure(LibraryExtension::class.java) {

        namespace = libNameSpace
        compileSdk = BuildSettings.TARGET_SDK_LEVEL

        buildFeatures {
            compose = true
            resValues = true
            buildConfig = false
            dataBinding = false
            viewBinding = false
        }

        standardAndroidConfig(
            this@standardAndroidLib,
            libNameSpace,
            null,
            *consumerProguardPaths
        )
    }
}

fun Project.standardAndroidApp(
    appNamespace: String,
    appVersionCode: Int,
    vararg consumerProguardPaths: String
) {
    standardProjectConfig()

    (this as ExtensionAware).extensions.configure(BaseAppModuleExtension::class.java) {

        namespace = appNamespace
        compileSdk = BuildSettings.TARGET_SDK_LEVEL

        buildFeatures {
            dataBinding = false
            compose = true
        }

        val appVersionName = "0.9.$appVersionCode"
        defaultConfig {
            applicationId = appNamespace
            versionCode = appVersionCode
            versionName = appVersionName

            javaCompileOptions {
                annotationProcessorOptions {
                    roomOptions("${projectDir.absolutePath}/schemas")
                }
            }
        }

        standardAndroidConfig(
            this@standardAndroidApp,
            appNamespace,
            appVersionName,
            *consumerProguardPaths
        )
    }
}

private fun TestedExtension.standardAndroidConfig(
    project: Project,
    moduleNamespace: String,
    appVersionName: String?,
    vararg consumerProguardPaths: String,
) {
    @Suppress("UnstableApiUsage")
    composeOptions {
        kotlinCompilerExtensionVersion = Version.Compose.COMPILER
    }

    defaultConfig {
        if (appVersionName != null)
            applicationId = moduleNamespace
        minSdk = BuildSettings.MIN_SDK_LEVEL
        targetSdk = BuildSettings.TARGET_SDK_LEVEL
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
        if (consumerProguardPaths.isEmpty())
            consumerProguardFiles.add(project.file(consumerProguardPaths))
        else
            consumerProguardFiles.addAll(project.files(consumerProguardPaths))
    }

    compileOptions {
        sourceCompatibility = project.buildConfig.javaVersion
        targetCompatibility = project.buildConfig.javaVersion
    }

    (this as ExtensionAware).extensions.configure(KotlinJvmOptions::class.java) {
        jvmTarget = project.buildConfig.javaVersionName
        apiVersion = project.buildConfig.kotlinCompatibility
        languageVersion = project.buildConfig.kotlinCompatibility
        //freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    }

    if (appVersionName != null)
        addStandardAppBuildTypes(appVersionName)
    else
        addStandardLibBuildTypes()
}

private fun Project.standardProjectConfig() {
    (this as ExtensionAware).extensions.configure(JavaPluginExtension::class.java) {
        sourceCompatibility = buildConfig.javaVersion
        targetCompatibility = buildConfig.javaVersion
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = buildConfig.javaVersionName
            apiVersion = buildConfig.kotlinCompatibility
            languageVersion = buildConfig.kotlinCompatibility
        }
    }
}