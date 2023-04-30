package dz.nexatech.reporter.client.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ResourcesDAO {

    @Query("select resource_path from resource")
    suspend fun loadIndex(): List<String>

    @Query("select * from resource where resource_path = :path")
    suspend fun load(path: String): Resource?

    @Query("select * from resource where resource_path = :path and last_update > :update")
    suspend fun load(path: String, update: Long): Resource?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAll(resources: List<Resource>)
}