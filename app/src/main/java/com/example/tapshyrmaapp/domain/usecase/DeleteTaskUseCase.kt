package com.example.tapshyrmaapp.domain.usecase

import com.example.tapshyrmaapp.data.local.database.entity.TaskModel
import com.example.tapshyrmaapp.data.repository.TaskRepository
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: TaskModel) = repository.deleteTask(task)
}