package dz.nexatech.reporter.util.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val LOGGED_EVENT_TABLE = "logged_event"
const val LOGGED_EVENT_ID = "logged_event_id"

@Entity(tableName = LOGGED_EVENT_TABLE)
data class LoggedEvent(
    @PrimaryKey
    @ColumnInfo(name = LOGGED_EVENT_ID)
    val id: Long,
    val name: String,
    val parameters: String?,
)