package com.reporter.util.model

import com.reporter.util.ui.AbstractApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GlobalDataModule {

    @Provides
    @Singleton
    fun getLogDAO(database: StandardDatabase): LogDAO = database.logDAO()

}