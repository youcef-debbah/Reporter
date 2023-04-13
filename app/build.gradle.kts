import com.reporter.gradle.Version
import com.reporter.gradle.addCommonAndroidTestDependencies
import com.reporter.gradle.addCommonTestDependencies
import com.reporter.gradle.addHilt
import com.reporter.gradle.addLifecycle
import com.reporter.gradle.addRoomApi
import com.reporter.gradle.addRoomCompiler
import com.reporter.gradle.standardAndroidApp

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

println("app build config...")

standardAndroidApp("com.reporter.client", 4, "proguard-rules.pro")

dependencies {
    addCommonTestDependencies()
    addCommonAndroidTestDependencies()

    addHilt()
    addRoomCompiler()

    addRoomApi()
    addLifecycle()

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
    implementation("androidx.preference:preference:${Version.PREFERENCE}")
    implementation("com.google.guava:guava:${Version.GUAVA}")

    implementation("androidx.paging:paging-runtime:${Version.PAGING}")
    implementation("androidx.paging:paging-compose:${Version.PAGING_COMPOSE}")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:${Version.LIFECYCLE_COMPOSE}")
    implementation("androidx.constraintlayout:constraintlayout-compose:${Version.CONSTRAINT_LAYOUT_COMPOSE}")
    implementation("androidx.navigation:navigation-compose:${Version.NAVIGATION_COMPOSE}")

    implementation("androidx.compose.foundation:foundation:${Version.Compose.FOUNDATION}")
    implementation("androidx.compose.animation:animation:${Version.Compose.ANIMATION}")
    implementation("androidx.compose.ui:ui:${Version.Compose.UI}")
    implementation("androidx.compose.ui:ui-tooling:${Version.Compose.UI}")
    implementation("androidx.compose.runtime:runtime-livedata:${Version.Compose.RUNTIME}")
    implementation("androidx.compose.material3:material3:${Version.Compose.MATERIAL3}")
    implementation("androidx.compose.material3:material3-window-size-class:${Version.Compose.MATERIAL3}")
//    implementation("androidx.compose.material:material:${Version.Compose.MATERIAL}")

    implementation("com.google.accompanist:accompanist-flowlayout:${Version.COMPOSE_ACCOMPANIST}")
    implementation("com.google.accompanist:accompanist-systemuicontroller:${Version.COMPOSE_ACCOMPANIST}")
    implementation("com.google.accompanist:accompanist-navigation-animation:${Version.COMPOSE_ACCOMPANIST}")
    implementation("com.google.accompanist:accompanist-navigation-material:${Version.COMPOSE_ACCOMPANIST}")

    implementation("com.github.alorma:compose-settings-ui-m3:${Version.COMPOSE_SETTINGS}")
    implementation("com.tencent:mmkv:${Version.MMKV}")

    val coroutinesVersion = "1.6.4"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")

    implementation("io.pebbletemplates:pebble:3.2.0")
    implementation("com.itextpdf:itext7-core:7.2.5")
    implementation("com.itextpdf:html2pdf:4.0.5")
}