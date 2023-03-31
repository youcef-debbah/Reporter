package com.reporter.client.model

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.reporter.util.model.StandardDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReporterDataModule {

    @Binds
    abstract fun getStandardDatabase(database: ReporterDatabase): StandardDatabase

    companion object {

        @Provides
        @Singleton
        fun getDatabase(@ApplicationContext context: Context): ReporterDatabase =
            Room.databaseBuilder(context, ReporterDatabase::class.java, ReporterDatabase.NAME)
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback())
                .build()
    }

    class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            //TODO
        }
    }
}