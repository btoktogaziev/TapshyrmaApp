package com.example.tapshyrmaapp.data.repository

import com.example.tapshyrmaapp.data.local.database.entity.TaskModel
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(): Flow<List<TaskModel>>
    fun getTasksByStatus(isCompleted: Boolean): Flow<List<TaskModel>>
    suspend fun getTaskById(id: Int): Flow<TaskModel?>
    suspend fun insertTask(taskModel: TaskModel)
    suspend fun updateTask(taskModel: TaskModel)
    suspend fun deleteTask(taskModel: TaskModel)
    suspend fun deleteCompletedTasks()
}