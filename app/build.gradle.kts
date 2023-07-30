import dz.nexatech.reporter.gradle.Version
import dz.nexatech.reporter.gradle.addCommonAndroidTestDependencies
import dz.nexatech.reporter.gradle.addCommonTestDependencies
import dz.nexatech.reporter.gradle.addCoroutines
import dz.nexatech.reporter.gradle.addHilt
import dz.nexatech.reporter.gradle.addLifecycle
import dz.nexatech.reporter.gradle.addRoomApi
import dz.nexatech.reporter.gradle.addRoomCompiler
import dz.nexatech.reporter.gradle.buildConfig
import dz.nexatech.reporter.gradle.standardAndroidApp

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    kotlin("kapt")
//    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")

    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")

    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

logger.info("app build config...")

standardAndroidApp("dz.nexatech.reporter.client", 18, "proguard-rules.pro")

dependencies {
    addCommonTestDependencies()
    addCommonAndroidTestDependencies()

    addHilt(project.buildConfig.hiltVersion)
    addRoomCompiler()

    addRoomApi()
    addLifecycle()
    addCoroutines()

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation(platform("com.google.firebase:firebase-bom:${Version.FIREBASE_BOM}"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-config-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-perf-ktx")
    implementation("com.google.firebase:firebase-inappmessaging-display")

    implementation("androidx.activity:activity-compose:${Version.ACTIVITY}")
    implementation("androidx.appcompat:appcompat:${Version.APPCOMPAT}")
    implementation("androidx.appcompat:appcompat-resources:${Version.APPCOMPAT}")
    implementation("androidx.core:core-ktx:${Version.ANDROIDX_CORE}")
    implementation("com.google.android.material:material:${Version.GOOGLE_MATERIAL}")
    implementation("androidx.preference:preference-ktx:${Version.PREFERENCE}")
    implementation("com.google.guava:guava:${Version.GUAVA_ANDROID}")

//    implementation("androidx.paging:paging-runtime-ktx:${Version.PAGING}")
//    implementation("androidx.paging:paging-compose:${Version.PAGING_COMPOSE}")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:${Version.LIFECYCLE_COMPOSE}")
    implementation("androidx.navigation:navigation-compose:${Version.NAVIGATION_COMPOSE}")
//    implementation("androidx.constraintlayout:constraintlayout-compose:${Version.CONSTRAINT_LAYOUT_COMPOSE}")

    implementation("androidx.compose.foundation:foundation:${Version.Compose.FOUNDATION}")
    implementation("androidx.compose.animation:animation:${Version.Compose.ANIMATION}")
    implementation("androidx.compose.ui:ui:${Version.Compose.UI}")
    implementation("androidx.compose.ui:ui-tooling:${Version.Compose.UI}")
    implementation("androidx.compose.runtime:runtime-livedata:${Version.Compose.RUNTIME}")
    implementation("androidx.compose.material3:material3:${Version.Compose.MATERIAL3}")
    implementation("androidx.compose.material3:material3-window-size-class:${Version.Compose.MATERIAL3}")
//    implementation("androidx.compose.material:material:${Version.Compose.MATERIAL}")
//    implementation("androidx.compose.material:material-icons-extended:${Version.Compose.UI}")

//    implementation("com.google.accompanist:accompanist-flowlayout:${Version.COMPOSE_ACCOMPANIST}")
    implementation("com.google.accompanist:accompanist-systemuicontroller:${Version.COMPOSE_ACCOMPANIST}")
    implementation("com.google.accompanist:accompanist-navigation-animation:${Version.COMPOSE_ACCOMPANIST}")
    implementation("com.google.accompanist:accompanist-navigation-material:${Version.COMPOSE_ACCOMPANIST}")
    implementation("androidx.core:core-splashscreen:${Version.SPLASH_SCREEN}")
    implementation("org.slf4j:slf4j-api:${Version.SLF4J}")
    implementation("uk.uuid.slf4j:slf4j-android:${Version.SLF4J_ANDROID_LOGGER}")

    // active 3rd party libs
    implementation("com.tencent:mmkv:${Version.MMKV}")
    implementation("com.github.alorma:compose-settings-ui-m3:${Version.COMPOSE_SETTINGS}")

    // extra 3rd party libs
    implementation("com.godaddy.android.colorpicker:compose-color-picker-android:${Version.GODADDY_COLOR_PICKER}")

    implementation(project(":core"))
}