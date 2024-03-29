package dz.nexatech.reporter.client.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import dz.nexatech.reporter.client.common.MimeType

@Dao
interface ResourcesDAO {

    @Transaction
    @Query("select resource_path from resource where mime_type = '${MimeType.FONT_TTF}'")
    suspend fun loadAvailableDynamicFonts(): List<String>

    @Transaction
    @Query("select * from resource where resource_path = :path")
    suspend fun load(path: String): Resource?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun replaceAll(resources: List<Resource>)

    @Query("delete from resource")
    fun deleteAll()
}