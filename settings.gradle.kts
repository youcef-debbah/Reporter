pluginManagement {

    System.getenv("COMMON_LIB_LOCK")?.let {
        val lockFile = File(it)
        while (lockFile.exists()) {
            logger.warn("gradle build locked...")
            Thread.sleep(100)
        }
    }

    val properties = java.util.Properties()
    file("buildSrc/gradle.properties").reader().use { properties.load(it) }
    val kotlinVersion = properties["build.version.kotlin"]!!.toString()
    val kaptVersion = properties["build.version.kapt"]!!.toString()
//    val kspVersion = properties["build.version.ksp"]!!.toString()

    plugins {
        id("org.jetbrains.kotlin.android") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.compose") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion
        id("org.jetbrains.kotlin.kapt") version kaptVersion
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
//        id("com.google.devtools.ksp") version kspVersion
//        id("androidx.benchmark") version "1.1.0-beta04"
    }

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    @Suppress("UnstableApiUsage")
    repositories {
        maven("https://repo.itextsupport.com/releases")
        google()
        mavenCentral()
    }
}

rootProject.name = "Reporter"

include(":app")
include(":core")