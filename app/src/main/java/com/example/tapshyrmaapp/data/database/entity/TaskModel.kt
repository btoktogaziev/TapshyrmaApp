package com.example.tapshyrmaapp.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "taskModel")
data class TaskModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo("title")
    val title: String,
    @ColumnInfo("description")
    val description: String,
    @ColumnInfo("date_time")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo("is_completed")
    var isCompleted: Boolean = false
)