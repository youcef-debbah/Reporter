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
import com.reporter.common.MIME_TYPE_SVG
import com.reporter.common.readAsBytes
import com.reporter.util.model.EmbeddedListConverter
import com.reporter.util.model.LogDAO
import com.reporter.util.model.LoggedEvent
import com.reporter.util.model.StandardDatabase
import com.reporter.util.model.Teller
import com.reporter.util.ui.AbstractApplication
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
        fun templatesDAO(database: ReporterDatabase) = database.templatesDAO()

        @Provides
        fun valuesDAO(database: ReporterDatabase) = database.valuesDAO()

        @Provides
        fun resourcesDAO(database: ReporterDatabase) = database.resourcesDAO()
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

                db.insert(RESOURCE_TABLE, SQLiteDatabase.CONFLICT_ROLLBACK, ContentValues().apply {
                    put(RESOURCE_COLUMN_PATH, "icons/loaded.svg")
                    put(RESOURCE_COLUMN_MIME_TYPE, MIME_TYPE_SVG)
                    put(RESOURCE_COLUMN_LAST_MODIFIED, System.currentTimeMillis())
                    put(RESOURCE_COLUMN_DATA, AbstractApplication.INSTANCE.assets.open("icons/test.svg").readAsBytes())
                })
            }
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