import com.lippia.gradle.Version
import com.lippia.gradle.addCommonAndroidTestDependencies
import com.lippia.gradle.addCommonTestDependencies
import com.lippia.gradle.addHilt
import com.lippia.gradle.addLifecycle
import com.lippia.gradle.addRoomApi
import com.lippia.gradle.addRoomCompiler
import com.lippia.gradle.standardAndroidApp

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

standardAndroidApp("com.reporter", 1, "proguard-rules.pro")

dependencies {
    addCommonTestDependencies()
    addCommonAndroidTestDependencies()

    addHilt()
    addRoomCompiler()

    addRoomApi()
    addLifecycle()

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.lippia:common:${Version.LIPPIA_COMMON}")

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
    implementation("androidx.compose.material:material:${Version.Compose.MATERIAL}")
    implementation("androidx.compose.material:material-icons-core:${Version.Compose.MATERIAL}")
    implementation("androidx.compose.material:material-icons-extended:${Version.Compose.MATERIAL}")

    implementation("com.google.accompanist:accompanist-flowlayout:${Version.COMPOSE_ACCOMPANIST}")
    implementation("com.google.accompanist:accompanist-systemuicontroller:${Version.COMPOSE_ACCOMPANIST}")
    implementation("com.google.accompanist:accompanist-navigation-animation:${Version.COMPOSE_ACCOMPANIST}")
    implementation("com.google.accompanist:accompanist-navigation-material:${Version.COMPOSE_ACCOMPANIST}")

    implementation("com.github.alorma:compose-settings-ui-m3:${Version.COMPOSE_SETTINGS}")
    implementation("com.tencent:mmkv:${Version.MMKV}")
}