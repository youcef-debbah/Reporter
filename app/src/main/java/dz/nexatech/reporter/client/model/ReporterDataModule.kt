package dz.nexatech.reporter.client.model

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import dz.nexatech.reporter.util.model.EmbeddedListConverter
import dz.nexatech.reporter.util.model.LogDAO
import dz.nexatech.reporter.util.model.LoggedEvent
import dz.nexatech.reporter.util.model.StandardDatabase
import dz.nexatech.reporter.util.model.Teller
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dz.nexatech.reporter.client.core.AbstractValuesDAO
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
                .build()

        @Provides
        fun templatesDAO(database: ReporterDatabase): TemplatesDAO = database.templatesDAO()

        @Provides
        fun resourcesDAO(database: ReporterDatabase): ResourcesDAO = database.resourcesDAO()

        @Provides
        fun valuesDAO(database: ReporterDatabase): AbstractValuesDAO = database.valuesDAO()
    }
}

private fun SupportSQLiteDatabase.runTransaction(transaction: SupportSQLiteDatabase.() -> Unit) {
    beginTransaction()
    try {
        transaction()
        setTransactionSuccessful()
    } catch (e: Exception) {
        Teller.warn("manual db call error", e)
    } finally {
        endTransaction()
    }
}

@WorkerThread
@Database(
    version = 1,
    entities = [
        Template::class,
        Value::class,
        LoggedEvent::class,
        Resource::class,
    ],
)
@TypeConverters(EmbeddedListConverter::class)
abstract class ReporterDatabase : RoomDatabase(), StandardDatabase {

    companion object {
        const val NAME = "reporter_db"
    }

    abstract override fun logDAO(): LogDAO

    abstract fun templatesDAO(): TemplatesDAO

    abstract fun valuesDAO(): ValuesDAO

    abstract fun resourcesDAO(): ResourcesDAO
}