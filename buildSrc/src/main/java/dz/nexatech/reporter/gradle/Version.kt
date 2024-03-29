package dz.nexatech.reporter.gradle

object Version {
    // https://github.com/nomis/slf4j-android
    const val SLF4J_ANDROID_LOGGER = "2.0.11-0"
    const val SLF4J = "2.0.11"

    // https://github.com/Kotlin/kotlinx.coroutines/releases
    const val COROUTINES = "1.7.3"

    // https://truth.dev/#gradle
    const val TRUTH = "1.3.0"

    // https://developer.android.com/jetpack/androidx/releases/core
    const val ANDROIDX_CORE = "1.12.0"

    // https://developer.android.com/jetpack/androidx/releases/preference
    const val PREFERENCE = "1.2.1"

    // https://github.com/google/guava/releases
    const val GUAVA = "33.0.0"
    const val GUAVA_JRE = "$GUAVA-jre"
    const val GUAVA_ANDROID = "$GUAVA-android"

    // https://developer.android.com/jetpack/androidx/releases/room
    const val ROOM = "2.6.1"

    // https://developer.android.com/jetpack/androidx/releases/activity
    const val ACTIVITY = "1.8.2"

    // https://developer.android.com/jetpack/compose/navigation
    const val NAVIGATION_COMPOSE = "2.7.6"

    // https://developer.android.com/jetpack/androidx/releases/constraintlayout
    const val CONSTRAINT_LAYOUT_COMPOSE = "1.0.1"
    const val CONSTRAINT_LAYOUT = "2.1.4"

    // https://developer.android.com/jetpack/androidx/releases/arch-core
    const val CORE_ARCH = "2.2.0"

    // https://developer.android.com/jetpack/androidx/releases/test
    object Test {
        const val ESPRESSO = "3.5.1"
        const val EXT_JUNIT = "1.1.5"
        const val EXT_JUNIT_KTS = "1.1.5"
    }

    // https://github.com/junit-team/junit4/releases
    const val JUNIT4 = "4.13.2"

    // https://developer.android.com/jetpack/androidx/releases/customview
    const val CUSTOM_VIEW = "1.1.0"
    const val CUSTOM_VIEW_POOLING = "1.0.0"

    // https://github.com/Tencent/MMKV
    const val MMKV = "1.3.3"

    // https://github.com/godaddy/compose-color-picker
    const val GODADDY_COLOR_PICKER = "0.7.0"

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /* the versions below should get regular updates because they are still under heavy development */
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    // https://firebase.google.com/support/release-notes/android#latest_sdk_versions
    const val FIREBASE_BOM = "32.7.1"

    // https://github.com/material-components/material-components-android/releases
    const val GOOGLE_MATERIAL = "1.12.0-alpha03"

    // https://developer.android.com/jetpack/androidx/releases/paging
    const val PAGING = "3.2.1"
    const val PAGING_COMPOSE = PAGING

    // https://developer.android.com/jetpack/androidx/releases/appcompat
    const val APPCOMPAT = "1.6.1"

    // https://developer.android.com/jetpack/androidx/releases/lifecycle
    const val LIFECYCLE = "2.7.0"
    const val LIFECYCLE_COMPOSE = LIFECYCLE

    // https://github.com/alorma/Compose-Settings
    const val COMPOSE_SETTINGS = "1.0.3"

    // https://github.com/google/accompanist
    const val COMPOSE_ACCOMPANIST = "0.32.0"

    // https://developer.android.com/develop/ui/views/launch/splash-screen#getting-started
    const val SPLASH_SCREEN = "1.0.1"

    // https://developer.android.com/jetpack/androidx/releases/compose
    object Compose {
        const val ANIMATION = "1.5.4"
        const val COMPILER = "1.5.8"
        const val FOUNDATION = "1.5.4"
        const val MATERIAL = "1.5.4"
        const val MATERIAL3 = "1.1.2"
        const val RUNTIME = "1.5.4"
        const val UI = "1.5.4"
    }
}