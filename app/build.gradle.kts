import com.lippia.gradle.*

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    kotlin("kapt")
//    id("com.google.devtools.ksp")
//    id("dagger.hilt.android.plugin")

//    id("com.google.gms.google-services")
//    id("com.google.firebase.crashlytics")
//    id("com.google.firebase.firebase-perf")
//    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

println("app build config...")

standardAndroidApp("com.lippia.clinic", 11, "clinic-proguard-rules.pro")

dependencies {
    addCommonTestDependencies()
    addCommonAndroidTestDependencies()

//    addHilt()
//    addRoomCompiler()

//    addRoomApi()
    addLifecycle()

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
//    implementation("org.jetbrains.kotlin:kotlin-reflect:${buildConfig.kotlinVersion}")
    implementation("com.lippia:common:${Version.LIPPIA_COMMON}")

//    implementation(platform("com.google.firebase:firebase-bom:31.2.3))
//    implementation("com.google.firebase:firebase-analytics-ktx")
//    implementation("com.google.firebase:firebase-config-ktx")
//    implementation("com.google.firebase:firebase-crashlytics-ktx")
//    implementation("com.google.firebase:firebase-perf-ktx")
//    implementation("com.google.firebase:firebase-inappmessaging-display")

    implementation("com.google.android.material:material:1.9.0-alpha02")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:${Version.APPCOMPAT}")
    implementation("androidx.appcompat:appcompat-resources:${Version.APPCOMPAT}")
    implementation("androidx.preference:preference:1.2.0")
    implementation("com.google.guava:guava:31.1-android")

    implementation("androidx.paging:paging-runtime:3.2.0-alpha04")

    implementation("androidx.compose.foundation:foundation:${Version.Compose.FOUNDATION}")
    implementation("androidx.compose.animation:animation:${Version.Compose.ANIMATION}")
    implementation("androidx.compose.ui:ui:${Version.Compose.UI}")
    implementation("androidx.compose.ui:ui-tooling:${Version.Compose.UI}")
    implementation("androidx.activity:activity-compose:${Version.Compose.ACTIVITY}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:${Version.Compose.VIEWMODEL}")
    implementation("androidx.compose.runtime:runtime-livedata:${Version.Compose.RUNTIME}")

    implementation("androidx.paging:paging-compose:${Version.Compose.PAGING}")
    implementation("androidx.constraintlayout:constraintlayout-compose:${Version.Compose.CONSTRAINT_LAYOUT}")
    implementation("androidx.compose.material3:material3:${Version.Compose.MATERIAL3}")
    implementation("androidx.compose.material3:material3-window-size-class:${Version.Compose.MATERIAL3}")
    implementation("androidx.compose.material:material:${Version.Compose.MATERIAL}")
    implementation("androidx.compose.material:material-icons-core:${Version.Compose.MATERIAL}")
    implementation("androidx.compose.material:material-icons-extended:${Version.Compose.MATERIAL}")

    implementation("com.google.accompanist:accompanist-flowlayout:0.29.1-alpha")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.29.1-alpha")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.29.1-alpha")
    implementation("com.google.accompanist:accompanist-navigation-material:0.29.1-alpha")

    implementation("com.github.alorma:compose-settings-ui-m3:0.22.0")
    implementation("com.tencent:mmkv:1.2.15")

    implementation("androidx.navigation:navigation-compose:2.5.3")
}