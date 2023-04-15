package com.reporter.client.model

import androidx.room.Dao
import androidx.room.Query

@Dao
interface TemplatesDAO {
    @Query("select * from template")
    suspend fun loadTemplates(): List<Template>
}