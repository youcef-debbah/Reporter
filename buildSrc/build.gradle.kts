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
    kotlinOptions {
        jvmTarget = javaVersionName
        apiVersion = kotlinCompatibility
        languageVersion = kotlinCompatibility
    }
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation("com.android.tools.build:gradle:8.1.1")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    implementation("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")

    // https://github.com/google/secrets-gradle-plugin
    implementation("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")

    // https://firebase.google.com/support/release-notes/android
    // below versions are based on BOM version: 32.7.1
    // TODO these versions should be read from a config file like hilt version
    implementation("com.google.gms:google-services:4.4.0")
    implementation("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
    implementation("com.google.firebase:perf-plugin:1.4.2")
}