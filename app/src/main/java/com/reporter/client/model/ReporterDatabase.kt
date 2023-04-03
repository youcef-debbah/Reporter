package com.reporter.client.model

import androidx.annotation.WorkerThread
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.reporter.util.model.EmbeddedListConverter
import com.reporter.util.model.LoggedEvent
import com.reporter.util.model.LogDAO
import com.reporter.util.model.StandardDatabase

@WorkerThread
@Database(
    version = 1,
    entities = [
        Template::class,
        LoggedEvent::class,
    ],
)
@TypeConverters(EmbeddedListConverter::class)
abstract class ReporterDatabase : RoomDatabase(), StandardDatabase {

    companion object {
        const val NAME = "reporter_db"
    }

    abstract override fun logDAO(): LogDAO
}