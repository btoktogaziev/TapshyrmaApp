package com.example.tapshyrmaapp.domain.usecase

import com.example.tapshyrmaapp.data.local.database.entity.TaskModel
import com.example.tapshyrmaapp.data.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    operator fun invoke(): Flow<List<TaskModel>> = repository.getAllTasks()
}