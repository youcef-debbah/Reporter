package dz.nexatech.reporter.client.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dz.nexatech.reporter.client.core.AbstractValue

@Dao
interface ValuesDAO {

    suspend fun findByNamespacePrefix(namespacePrefix: String): List<AbstractValue> =
        findByNamespace("$namespacePrefix%")

    @Query("select * from value where value_namespace like :namespace")
    suspend fun findByNamespace(namespace: String): List<Value>

    @Query("delete from value where value_namespace = :namespace and value_name = :name")
    suspend fun delete(namespace: String, name: String)

    suspend fun insert(namespace: String, name: String, newContent: String) {
        insert(Value(namespace, name, System.currentTimeMillis(), newContent))
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(newValue: Value)
}