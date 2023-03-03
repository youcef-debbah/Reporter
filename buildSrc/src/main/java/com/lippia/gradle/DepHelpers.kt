package com.lippia.gradle

import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.addRoomApi(transient: Boolean = false) {
    add(apiOrImpl(transient), "androidx.room:room-runtime:${Version.ROOM}")
    add(apiOrImpl(transient), "androidx.room:room-ktx:${Version.ROOM}")
    add(apiOrImpl(transient), "androidx.room:room-paging:${Version.ROOM}")
    add(apiOrImpl(transient), "androidx.room:room-guava:${Version.ROOM}")
}

fun DependencyHandler.addRoomCompiler() {
    add("kapt", "androidx.room:room-compiler:${Version.ROOM}")
}

fun DependencyHandler.addHilt() {
    addHiltDependencies()
    addHiltCompiler()
}

fun DependencyHandler.addHiltDependencies(transient: Boolean = false) {
    add(apiOrImpl(transient), "com.google.dagger:hilt-android:${Version.HILT}")
}

fun DependencyHandler.addHiltCompiler() {
    add("kapt", "com.google.dagger:hilt-android-compiler:${Version.HILT}")
}

fun DependencyHandler.addLifecycle(transient: Boolean = false) {
    add(apiOrImpl(transient), "androidx.lifecycle:lifecycle-viewmodel-ktx:${Version.LIFECYCLE}")
    add(apiOrImpl(transient), "androidx.lifecycle:lifecycle-viewmodel-compose:${Version.LIFECYCLE}")
    add(apiOrImpl(transient), "androidx.lifecycle:lifecycle-common-java8:${Version.LIFECYCLE}")
    add(apiOrImpl(transient), "androidx.lifecycle:lifecycle-service:${Version.LIFECYCLE}")
    add(apiOrImpl(transient), "androidx.lifecycle:lifecycle-process:${Version.LIFECYCLE}")
    add("debugImplementation","androidx.lifecycle:lifecycle-viewmodel-savedstate:${Version.LIFECYCLE}")
    add("debugImplementation","androidx.lifecycle:lifecycle-runtime:${Version.LIFECYCLE}")
    add("debugImplementation", "androidx.customview:customview-poolingcontainer:1.0.0")
    add("testImplementation", "androidx.arch.core:core-testing:${Version.ARCH}")
    add("testImplementation", "androidx.lifecycle:lifecycle-runtime-testing:${Version.LIFECYCLE}")
}

fun apiOrImpl(transient: Boolean) = if (transient) "api" else "implementation"

fun DependencyHandler.addCommonTestDependencies() {
//    add("testImplementation","com.google.dagger:hilt-android-testing:${Version.HILT}")
//    add("kaptTest","com.google.dagger:hilt-compiler:${Version.HILT}")
    add("testImplementation", "junit:junit:4.13.2")
    add("testImplementation", "androidx.room:room-testing:${Version.ROOM}")
    add("debugImplementation", "androidx.compose.ui:ui-test-manifest:${Version.Compose.UI}")

    add("androidTestImplementation", "com.google.truth:truth:1.1.3")
    add("androidTestImplementation", "androidx.arch.core:core-testing:2.1.0")
}

fun DependencyHandler.addCommonAndroidTestDependencies() {
//    add("androidTestImplementation","com.google.dagger:hilt-android-testing:${Version.HILT}")
//    add("kaptAndroidTest","com.google.dagger:hilt-compiler:${Version.HILT}")
    add("androidTestImplementation", "androidx.test.ext:junit:1.1.3")
    add("androidTestImplementation", "androidx.test.ext:junit-ktx:1.1.3")

    add("androidTestImplementation", "androidx.test.espresso:espresso-core:3.4.0")
    add("androidTestImplementation", "androidx.compose.ui:ui-test-junit4:${Version.Compose.UI}")

    add("androidTestImplementation", "com.google.truth:truth:1.1.3")
    add("androidTestImplementation", "androidx.test.ext:junit-ktx:1.1.3")
    add("androidTestImplementation", "androidx.arch.core:core-testing:2.1.0")
}