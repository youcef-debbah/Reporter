package com.reporter.client.model

import androidx.room.Dao
import androidx.room.Query

@Dao
interface TemplateDAO {
    @Query("select * from template")
    suspend fun loadTemplates(): List<Template>
}