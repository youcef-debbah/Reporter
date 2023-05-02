package dz.nexatech.reporter.client.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dz.nexatech.reporter.client.core.AbstractValue
import dz.nexatech.reporter.client.core.AbstractValuesDAO

@Dao
interface ValuesDAO: AbstractValuesDAO {

    override suspend fun findByNamespacePrefix(namespacePrefix: String): List<AbstractValue> =
        findValuesByNamespacePrefix(namespacePrefix)

    suspend fun findValuesByNamespacePrefix(namespacePrefix: String): List<Value> =
        findValuesByNamespace("$namespacePrefix%")

    @Query("select * from value where value_namespace like :namespace")
    suspend fun findValuesByNamespace(namespace: String): List<Value>

    @Query("delete from value where value_namespace = :namespace and value_name = :name")
    override suspend fun delete(namespace: String, name: String)

    override suspend fun insert(namespace: String, name: String, newContent: String) {
        insertValue(Value(namespace, name, System.currentTimeMillis(), newContent))
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertValue(newValue: Value)
}