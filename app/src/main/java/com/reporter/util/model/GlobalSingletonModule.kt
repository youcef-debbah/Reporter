package com.reporter.util.model

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.reporter.util.ui.AbstractApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object GlobalSingletonModule {

    @Provides
    fun getApplication(@ApplicationContext context: Context): AbstractApplication =
        context as AbstractApplication

    @Provides
    fun getFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics =
        FirebaseAnalytics.getInstance(context)

    @Provides
    fun getFirebaseCrashlytics(): FirebaseCrashlytics = FirebaseCrashlytics.getInstance()
}