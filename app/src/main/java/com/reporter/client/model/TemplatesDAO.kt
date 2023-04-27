package com.reporter.client.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TemplatesDAO {
    @Query("select * from template")
    suspend fun loadTemplates(): List<Template>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAll(templates: List<Template>)
}