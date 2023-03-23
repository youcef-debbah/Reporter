package com.reporter.util.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
abstract class LogDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun log(log: LoggedEvent)

    @Delete
    abstract suspend fun deleteAll(events: List<LoggedEvent>)

    @Query("select * from logged_event order by logged_event_id ASC")
    abstract fun getAll(): Flow<List<LoggedEvent>>
}
