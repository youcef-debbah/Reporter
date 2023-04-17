package com.reporter.client.model

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ResourcesDAO {
    @Query("select * from resource where resource_path = :path")
    suspend fun load(path: String): Resource?

    @Query("select * from resource where resource_path = :path and last_update > :update")
    suspend fun load(path: String, update: Long): Resource?
}