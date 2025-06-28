import dz.nexatech.reporter.gradle.buildConfig
import dz.nexatech.reporter.gradle.Version
import dz.nexatech.reporter.gradle.addCommonTestDependencies
import dz.nexatech.reporter.gradle.addCoroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java-library")
    kotlin("jvm")
    `maven-publish`
}

logger.info("core build config...")

description = "a shared library that encapsulates pdf reports generation capabilities."
group = "dz.nexatech"
version = "0.9.7-" + System.currentTimeMillis()

val assembleSources by tasks.registering(Jar::class) {
    from(sourceSets.main.get().allSource)
    archiveClassifier.set("sources")
}

val assembleBinaries by tasks.registering(Jar::class) {
    from(sourceSets.main.get().output)
}

publishing {
    publications {
        register<MavenPublication>("publishReporterCore") {
            artifactId = "reporter-core"
            artifact(assembleBinaries.get())
            artifact(assembleSources.get()) {
                this.classifier = "sources"
            }
        }
    }
}

java {
    sourceCompatibility = buildConfig.javaVersion
    targetCompatibility = buildConfig.javaVersion
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(buildConfig.jvmTarget)
        apiVersion.set(buildConfig.kotlinTarget)
        languageVersion.set(buildConfig.kotlinTarget)
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${buildConfig.kotlinVersion}")
    implementation("com.google.guava:guava:${Version.GUAVA_JRE}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Version.COROUTINES}")

    addCoroutines()
    addCommonTestDependencies(false)

    api("io.pebbletemplates:pebble:3.2.4")

    implementation("com.itextpdf.android:itext-core-android:9.2.0")
    implementation("com.itextpdf:html2pdf:6.2.0") {
        exclude(group = "com.itextpdf", module = "forms")
        exclude(group = "com.itextpdf", module = "layout")
        exclude(group = "com.itextpdf", module = "svg")
        exclude(group = "com.itextpdf", module = "pdfua")
        exclude(group = "com.itextpdf", module = "pdfa")
        exclude(group = "com.itextpdf", module = "kernel")
    }
}