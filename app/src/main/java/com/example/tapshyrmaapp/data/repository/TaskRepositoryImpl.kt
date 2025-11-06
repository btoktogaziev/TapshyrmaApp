package com.example.tapshyrmaapp.data.repository

import com.example.tapshyrmaapp.data.local.database.dao.TaskDao
import com.example.tapshyrmaapp.data.local.database.entity.TaskModel
import com.example.tapshyrmaapp.data.repository.TaskRepository
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {
    override fun getAllTasks() = taskDao.getAllTasks()
    override fun getTasksByStatus(isCompleted: Boolean) =
        taskDao.getTasksByStatus(isCompleted)
    override suspend fun getTaskById(id: Int) = taskDao.getTaskById(id)
    override suspend fun insertTask(taskModel: TaskModel) = taskDao.insertTask(taskModel)
    override suspend fun updateTask(taskModel: TaskModel) = taskDao.updateTask(taskModel)
    override suspend fun deleteTask(taskModel: TaskModel) = taskDao.deleteTask(taskModel)
    override suspend fun deleteCompletedTasks() = taskDao.deleteCompletedTasks()
}