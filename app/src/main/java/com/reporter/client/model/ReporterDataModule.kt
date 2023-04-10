package com.reporter.client.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.annotation.WorkerThread
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.reporter.util.model.EmbeddedListConverter
import com.reporter.util.model.LogDAO
import com.reporter.util.model.LoggedEvent
import com.reporter.util.model.StandardDatabase
import com.reporter.util.model.Teller
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

        @Provides
        fun templateDAO(database: ReporterDatabase) = database.templateDAO()

        @Provides
        fun valueDAO(database: ReporterDatabase) = database.valueDAO()
    }

    class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            db.runTransaction {
                db.insert(TEMPLATE_TABLE, SQLiteDatabase.CONFLICT_ROLLBACK, ContentValues().apply {
                    put(TEMPLATE_COLUMN_NAME, "wood_bill")
                    put(TEMPLATE_COLUMN_LABEL_EN, "Wood bill")
                    put(TEMPLATE_COLUMN_LABEL_AR, "فاتورة الحطب")
                    put(TEMPLATE_COLUMN_LABEL_FR, "Facture de bois")
                    put(TEMPLATE_COLUMN_DESC_EN, "Standard wood bill for small clients")
                    put(TEMPLATE_COLUMN_DESC_AR, "فاتورة خشب قياسية للعملاء الصغار")
                    put(TEMPLATE_COLUMN_DESC_FR, "Facture de bois standard pour les petits clients")
                    put(TEMPLATE_COLUMN_LAST_UPDATE, System.currentTimeMillis())
                })
            }
            db.insert(TEMPLATE_TABLE, SQLiteDatabase.CONFLICT_ROLLBACK, ContentValues().apply {
                put(TEMPLATE_COLUMN_NAME, "water_bill")
                put(TEMPLATE_COLUMN_LABEL_EN, "Water bill")
                put(TEMPLATE_COLUMN_LABEL_AR, "فاتورة ماء")
                put(TEMPLATE_COLUMN_LABEL_FR, "Facture de l'eau")
                put(TEMPLATE_COLUMN_DESC_EN, "Standard water bill for small clients")
                put(TEMPLATE_COLUMN_DESC_AR, "فاتورة ماء قياسية للعملاء الصغار")
                put(TEMPLATE_COLUMN_DESC_FR, "Facture de l'eau standard pour les petits clients")
                put(TEMPLATE_COLUMN_LAST_UPDATE, System.currentTimeMillis())
            })
        }
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
    ],
)
@TypeConverters(EmbeddedListConverter::class)
abstract class ReporterDatabase : RoomDatabase(), StandardDatabase {

    companion object {
        const val NAME = "reporter_db"
    }

    abstract override fun logDAO(): LogDAO

    abstract fun templateDAO(): TemplateDAO

    abstract fun valueDAO(): ValueDAO
}