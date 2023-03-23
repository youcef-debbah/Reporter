package com.reporter.util.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val NAME = "logged_event"
const val ID = "logged_event_id"

@Entity(tableName = NAME)
data class LoggedEvent(
    @PrimaryKey
    @ColumnInfo(name = ID)
    val id: Long, val name: String, val parameters: String?
)