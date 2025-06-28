import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

val hiltVersion: String = property("build.version.hilt")!!.toString()
val javaVersion: JavaVersion = JavaVersion.toVersion(property("build.version.java")!!)
val javaVersionName: String = javaVersion.toString()
val kotlinVersion: String = property("build.version.kotlin")!!.toString()
val kotlinCompatibility: String = kotlinVersion.substring(0..2)

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        // TODO these versions should be read from a config file
        jvmTarget.set(JvmTarget.JVM_17)
        apiVersion.set(KotlinVersion.KOTLIN_2_2)
        languageVersion.set(KotlinVersion.KOTLIN_2_2)
    }
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation("com.android.tools.build:gradle:8.11.0")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    implementation("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")

    // https://github.com/google/secrets-gradle-plugin
    implementation("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")

    // https://firebase.google.com/support/release-notes/android
    // below versions are based on BOM Version.FIREBASE_BOM: 33.16.0
    // TODO these versions should be read from a config file like hilt version
    implementation("com.google.gms:google-services:4.4.3")
    implementation("com.google.firebase:firebase-crashlytics-gradle:3.0.4")
    implementation("com.google.firebase:perf-plugin:1.4.2")
}