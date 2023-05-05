import dz.nexatech.reporter.gradle.buildConfig
import dz.nexatech.reporter.gradle.Version
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java-library")
    kotlin("jvm")
    `maven-publish`
}

description = "a shared library that encapsulates pdf reports generation capabilities."
group = "dz.nexatech"
version = "0.9.0-" + System.currentTimeMillis()

val assembleSources by tasks.registering(Jar::class) {
    from(sourceSets.main.get().allSource)
    archiveClassifier.set("sources")
}

val assembleBinaries by tasks.registering(Jar::class) {
    from(sourceSets.main.get().output)
}

publishing {
    val artifact = "reporter-core"
    publications {
        register<MavenPublication>("binaries") {
            artifactId = artifact
            artifact(assembleBinaries.get())
        }
        register<MavenPublication>("sources") {
            artifactId = artifact
            artifact(assembleSources.get())
        }
    }
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

    implementation("io.pebbletemplates:pebble:3.2.0")
    implementation("com.itextpdf:itext7-core:7.2.5")
    implementation("com.itextpdf:html2pdf:4.0.5")
    implementation(files("libs/typography-3.0.2.jar"))
}