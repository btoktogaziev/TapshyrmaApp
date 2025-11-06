package com.example.tapshyrmaapp.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.tapshyrmaapp.data.local.database.entity.TaskModel
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(taskModel: TaskModel)

    @Query("SELECT * FROM taskModel ")
    fun getAllTasks(): Flow<List<TaskModel>>

    @Query("SELECT * FROM taskModel WHERE is_completed = :isCompleted")
    fun getTasksByStatus(isCompleted: Boolean): Flow<List<TaskModel>>

    @Query("SELECT * FROM taskModel WHERE id =:id")
    fun getTaskById(id: Int): Flow<TaskModel?>

    @Update
    suspend fun updateTask(taskModel: TaskModel)

    @Delete
    suspend fun deleteTask(taskModel: TaskModel)

    @Query("DELETE FROM taskModel WHERE is_completed = 1")
    suspend fun deleteCompletedTasks()
}