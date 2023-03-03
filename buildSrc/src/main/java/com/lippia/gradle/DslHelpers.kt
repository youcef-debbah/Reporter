package com.lippia.gradle

import com.android.build.api.dsl.AnnotationProcessorOptions
import com.android.build.api.dsl.ApplicationBuildType
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File
import java.net.Inet4Address
import java.util.*

/**
 * the default IPv4 address of this machine in the local network or null
 * if this machine is not connected to any network
 */
val hostname: String by lazy { getLocalIPv4() ?: BuildConfig.DEFAULT_LOCALHOST }

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
            properties["build.version.kotlin"]!!.toString(),
            JavaVersion.toVersion(properties["build.version.java"]!!),
        )
    }

data class Config(
    val kotlinVersion: String,
    val javaVersion: JavaVersion,
    val javaVersionName: String = javaVersion.toString(),
    val kotlinCompatibility: String = kotlinVersion.substring(0..2),
)

fun TestedExtension.addStandardAppBuildTypes() {
    buildTypes {
        named(BuildConfig.DEBUG_BUILD_TYPE) {
            addCommonBuildConfig()
            addDynamicRes(true)
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
        }

        named(BuildConfig.RELEASE_BUILD_TYPE) {
            addCommonBuildConfig()
            addDynamicRes(false)
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
//            signingConfig = signingConfigs.getByName("release")
        }
    }
}

fun TestedExtension.addStandardLibBuildTypes() {
    buildTypes {
        named(BuildConfig.DEBUG_BUILD_TYPE) {
            addCommonBuildConfig()
            isMinifyEnabled = false
        }

        named(BuildConfig.RELEASE_BUILD_TYPE) {
            addCommonBuildConfig()
            isMinifyEnabled = true
        }
    }
}

private fun ApplicationBuildType.addCommonBuildConfig(proguardFile: String = BuildConfig.DEFAULT_PROGUARD_FILE) {
    ndk {
        debugSymbolLevel = "FULL"
    }
    proguardFiles(File(proguardFile))
}

private fun ApplicationBuildType.addDynamicRes(testVersion: Boolean) {
    resValue("bool", "is_test_version", testVersion.toString())
    resValue("string", "hostname", hostname)
    resValue("string", "build_epoch", System.currentTimeMillis().toString())
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

fun Project.standardAndroidLib(libSnamespace: String, vararg consumerProguardPaths: String) {
    standardProjectConfig()

    getConfigurations().named("androidTestImplementation") {
        exclude(
            mapOf(
                "group" to "com.google.firebase",
                "module" to "firebase-perf"
            )
        )
    }

    (this as ExtensionAware).extensions.configure(LibraryExtension::class.java) {

        namespace = libSnamespace
        compileSdk = BuildConfig.TARGET_SDK_LEVEL

        buildFeatures {
            dataBinding = false
            compose = true
        }

        standardAndroidConfig(this@standardAndroidLib, false, libSnamespace, *consumerProguardPaths)
    }
}

fun Project.standardAndroidApp(
    appNamespace: String,
    version: Int,
    vararg consumerProguardPaths: String
) {
    standardProjectConfig()

    (this as ExtensionAware).extensions.configure(BaseAppModuleExtension::class.java) {

        namespace = appNamespace
        compileSdk = BuildConfig.TARGET_SDK_LEVEL

        buildFeatures {
            dataBinding = false
            compose = true
        }

        defaultConfig {
            applicationId = appNamespace
            versionCode = version
            versionName = "0.9." + version

            javaCompileOptions {
                annotationProcessorOptions {
                    roomOptions("${projectDir.absolutePath}/schemas")
                }
            }
        }

        standardAndroidConfig(this@standardAndroidApp, true, appNamespace, *consumerProguardPaths)
    }
}

private fun TestedExtension.standardAndroidConfig(
    project: Project,
    application: Boolean,
    moduleNamespace: String,
    vararg consumerProguardPaths: String,
) {
    composeOptions {
        kotlinCompilerExtensionVersion = Version.Compose.COMPILER
    }

    defaultConfig {
        if (application)
            applicationId = moduleNamespace
        minSdk = BuildConfig.MIN_SDK_LEVEL
        targetSdk = BuildConfig.TARGET_SDK_LEVEL
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

    if (application)
        addStandardAppBuildTypes()
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