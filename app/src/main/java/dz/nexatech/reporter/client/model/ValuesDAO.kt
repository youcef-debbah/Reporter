package dz.nexatech.reporter.client.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface ValuesDAO {

    @Transaction
    @Query("select * from value where value_namespace = :template")
    suspend fun loadSectionVariablesValues(template: String): List<Value>

    suspend fun loadRecordsVariablesValues(template: String): List<Value> =
        loadRecordsVariablesValuesByNamespace("$template${Record.NAMESPACE_SEPARATOR}%")

    @Transaction
    @Query("select * from value where value_namespace like :namespace")
    suspend fun loadRecordsVariablesValuesByNamespace(namespace: String): List<Value>

    @Query("delete from value where value_namespace = :namespace and value_index = :index and value_name = :name")
    suspend fun delete(namespace: String, index: Int, name: String)

    @Delete
    suspend fun delete(values: List<Value>)

    suspend fun updateValue(namespace: String, index: Int, name: String, newValue: String) =
        updateValue(Value(namespace, index, name, System.currentTimeMillis(), newValue))

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun updateValue(value: Value)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAll(values: List<Value>)

    @Transaction
    @Query("select * from value")
    suspend fun loadAll(): List<Value>

    @Query("delete from value")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(newValues: List<Value>, oldValues: List<Value>? = null) {
        if (oldValues == null) {
            deleteAll()
        } else {
            delete(oldValues)
        }
        saveAll(newValues)
    }
}