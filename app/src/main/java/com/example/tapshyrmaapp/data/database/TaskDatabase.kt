package com.example.tapshyrmaapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tapshyrmaapp.data.database.dao.TaskDao
import com.example.tapshyrmaapp.data.database.entity.TaskModel

@Database(entities = [TaskModel::class], version = 1, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var DATABASE: TaskDatabase? = null
        fun getDatabase(context: Context): TaskDatabase {
            val tempInstance = DATABASE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_database"
                ).build()
                DATABASE = instance
                return instance
            }
        }
    }
}