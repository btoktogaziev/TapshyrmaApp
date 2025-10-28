package com.example.tapshyrmaapp.domain.repository

import androidx.lifecycle.LiveData
import com.example.tapshyrmaapp.data.database.dao.TaskDao
import com.example.tapshyrmaapp.data.database.entity.TaskModel

class TaskRepository(private val taskDao: TaskDao) {
    fun getAllTasks(): LiveData<List<TaskModel>> = taskDao.getAllTasks()

    fun getTasksByStatus(isCompleted: Boolean): LiveData<List<TaskModel>> =
        taskDao.getTasksByStatus(isCompleted)

    suspend fun getTaskById(id: Int): TaskModel? = taskDao.getTaskById(id)

    suspend fun insertTask(taskModel: TaskModel) = taskDao.insertTask(taskModel)

    suspend fun updateTask(taskModel: TaskModel) = taskDao.updateTask(taskModel)

    suspend fun deleteTask(taskModel: TaskModel) = taskDao.deleteTask(taskModel)

    suspend fun deleteCompletedTasks() = taskDao.deleteCompletedTasks()
}