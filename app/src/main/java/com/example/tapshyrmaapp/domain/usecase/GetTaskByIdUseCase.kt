package com.example.tapshyrmaapp.domain.usecase

import com.example.tapshyrmaapp.data.repository.TaskRepository
import javax.inject.Inject

class GetTaskByIdUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(id: Int) = repository.getTaskById(id)
}