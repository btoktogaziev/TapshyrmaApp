package com.example.tapshyrmaapp.domain.usecase

import com.example.tapshyrmaapp.data.repository.TaskRepository
import javax.inject.Inject

class DeleteCompletedTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke() = repository.deleteCompletedTasks()
}