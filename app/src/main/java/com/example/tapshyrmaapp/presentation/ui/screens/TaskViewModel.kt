package com.example.tapshyrmaapp.presentation.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.tapshyrmaapp.data.database.TaskDatabase
import com.example.tapshyrmaapp.data.database.entity.TaskModel
import com.example.tapshyrmaapp.domain.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository =
        TaskRepository(TaskDatabase.getDatabase(application).taskDao())

    var allTasks: LiveData<List<TaskModel>> = repository.getAllTasks()

    private val _currentTask = MutableLiveData<TaskModel?>()
    val currentTask: LiveData<TaskModel?> = _currentTask

    fun insertTask(taskModel: TaskModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTask(taskModel)
        }
    }

    fun updateTask(taskModel: TaskModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTask(taskModel)
        }
    }

    fun getTaskById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val task = repository.getTaskById(id)
            _currentTask.postValue(task)
        }
    }

    fun getTasksByStatus(isCompleted: Boolean): LiveData<List<TaskModel>> =
        repository.getTasksByStatus(isCompleted)

    fun deleteTask(taskModel: TaskModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTask(taskModel)
        }
    }

    fun deleteCompletedTasks() {
        viewModelScope.launch {
            repository.deleteCompletedTasks()
        }
    }
}