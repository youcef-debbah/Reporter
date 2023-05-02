import dz.nexatech.reporter.gradle.buildConfig
import dz.nexatech.reporter.gradle.Version
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java-library")
    kotlin("jvm")
}

java {
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

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${buildConfig.kotlinVersion}")
    implementation("com.google.guava:guava:${Version.GUAVA_JRE}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    api("io.pebbletemplates:pebble:3.2.0")
    api("com.itextpdf:itext7-core:7.2.5")
    api("com.itextpdf:html2pdf:4.0.5")
    api(files("libs/typography-3.0.2.jar"))
}