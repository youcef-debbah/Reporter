package com.reporter.client.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.reporter.common.Texts
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
                    put(COLUMN_TEMPLATE_NAME, "temp_1")
                    put(COLUMN_CONTENT, "${Texts.ASSETS_URL_PREFIX}wood_bill.html")
                    put(COLUMN_LABEL_EN, "Wood bill")
                    put(COLUMN_LABEL_AR, "فاتورة الحطب")
                    put(COLUMN_LABEL_FR, "Facture de bois")
                    put(COLUMN_DESC_EN, "Standard wood bill for small clients")
                    put(COLUMN_DESC_AR, "فاتورة خشب قياسية للعملاء الصغار")
                    put(COLUMN_DESC_FR, "Facture de bois standard pour les petits clients")
                    put(COLUMN_LAST_UPDATE, System.currentTimeMillis())
                })
            }
            db.insert(TEMPLATE_TABLE, SQLiteDatabase.CONFLICT_ROLLBACK, ContentValues().apply {
                put(COLUMN_TEMPLATE_NAME, "temp_2")
                put(COLUMN_CONTENT, "<p>This is my Water bill</p>")
                put(COLUMN_LABEL_EN, "Water bill")
                put(COLUMN_LABEL_AR, "فاتورة ماء")
                put(COLUMN_LABEL_FR, "Facture de l'eau")
                put(COLUMN_DESC_EN, "Standard water bill for small clients")
                put(COLUMN_DESC_AR, "فاتورة ماء قياسية للعملاء الصغار")
                put(COLUMN_DESC_FR, "Facture de l'eau standard pour les petits clients")
                put(COLUMN_LAST_UPDATE, System.currentTimeMillis())
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