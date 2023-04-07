package com.reporter.client.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ValueDAO {
    suspend fun findValuesPrefixedBy(namespacePrefix: String): List<Value> =
        findValues("$namespacePrefix%")

    @Query("select * from value where value_namespace like :namespace")
    suspend fun findValues(namespace: String): List<Value>

    @Delete
    suspend fun deleteAll(toDelete: List<Value>)

    @Query("delete from value where value_namespace = :namespace and value_name = :name")
    suspend fun delete(namespace: String, name: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(newValue: Value)

    suspend fun execute(update: ValueUpdate) {
        if (update.newValue == null) {
            delete(update.namespace, update.name)
        } else {
            insert(update.newValue)
        }
    }
}