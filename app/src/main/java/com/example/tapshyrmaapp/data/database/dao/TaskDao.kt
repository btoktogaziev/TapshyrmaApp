package com.example.tapshyrmaapp.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.tapshyrmaapp.data.database.entity.TaskModel

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(taskModel: TaskModel)

    @Query("SELECT * FROM taskModel ")
    fun getAllTasks(): LiveData<List<TaskModel>>

    @Query("SELECT * FROM taskModel WHERE is_completed = :isCompleted")
    fun getTasksByStatus(isCompleted: Boolean): LiveData<List<TaskModel>>

    @Query("SELECT * FROM taskModel WHERE id =:id")
    suspend fun getTaskById(id: Int): TaskModel?

    @Update
    suspend fun updateTask(taskModel: TaskModel)

    @Delete
    suspend fun deleteTask(taskModel: TaskModel)

    @Query("DELETE FROM taskModel WHERE is_completed = 1")
    suspend fun deleteCompletedTasks()
}