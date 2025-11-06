package com.example.tapshyrmaapp.presentation.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tapshyrmaapp.data.local.database.entity.TaskModel
import com.example.tapshyrmaapp.domain.usecase.DeleteCompletedTasksUseCase
import com.example.tapshyrmaapp.domain.usecase.DeleteTaskUseCase
import com.example.tapshyrmaapp.domain.usecase.GetTasksUseCase
import com.example.tapshyrmaapp.domain.usecase.UpdateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val deleteCompletedTasksUseCase: DeleteCompletedTasksUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase
) : ViewModel() {
    data class ListUiState(
        val tasks: List<TaskModel> = emptyList(),
        val hasCompleted: Boolean = false,
        val isLoading: Boolean = false,
        val filter: TaskFilter = TaskFilter.ALL,
        val showDeleteTaskDialog: TaskModel? = null,
        val showDeleteCompletedDialog: Boolean = false
    )

    private val _uiState = MutableStateFlow(ListUiState())
    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()

    private val _event = Channel<TaskListEvent>()
    val event = _event.receiveAsFlow()

    init {
        observeNotes()
    }

    private fun observeNotes() {
        viewModelScope.launch {
            getTasksUseCase.invoke().collect { allTasks ->
                val filtered = when (_uiState.value.filter) {
                    TaskFilter.ALL -> allTasks
                    TaskFilter.ACTIVE -> allTasks.filter { !it.isCompleted }
                    TaskFilter.COMPLETED -> allTasks.filter { it.isCompleted }
                }
                val hasCompleted = allTasks.any { it.isCompleted }
                _uiState.update {
                    it.copy(
                        tasks = filtered,
                        hasCompleted = hasCompleted,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun sendEvent(event: TaskListEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }

    fun setFilter(filter: TaskFilter) {
        _uiState.update { it.copy(filter = filter) }
        observeNotes()
    }

    fun onTaskClicked(taskId: Int) = sendEvent(TaskListEvent.NavigateToDetail(taskId))

    fun onAddTaskClicked() = sendEvent(TaskListEvent.NavigateToDetail(-1))

    fun updateTaskCompletedState(taskModel: TaskModel) {
        viewModelScope.launch {
            updateTaskUseCase(taskModel)
        }
    }

    fun showDeleteTaskDialog(task: TaskModel) {
        _uiState.update { it.copy(showDeleteTaskDialog = task) }
    }

    fun showDeleteCompletedTasksDialog() {
        _uiState.update { it.copy(showDeleteCompletedDialog = true) }
    }

    fun confirmDeleteTask() {
        val task = _uiState.value.showDeleteTaskDialog ?: return
        viewModelScope.launch {
            deleteTaskUseCase(task)
            _uiState.update { it.copy(showDeleteTaskDialog = null) }
        }
    }

    fun cancelDeleteTask() {
        _uiState.update { it.copy(showDeleteTaskDialog = null) }
    }

    fun confirmDeleteCompletedTasks() {
        viewModelScope.launch {
            deleteCompletedTasksUseCase()
            _uiState.update { it.copy(showDeleteCompletedDialog = false) }
        }
    }

    fun cancelDeleteCompletedTasks() {
        _uiState.update { it.copy(showDeleteCompletedDialog = false) }
    }
}

enum class TaskFilter {
    ALL, ACTIVE, COMPLETED
}

sealed interface TaskListEvent {
    data class NavigateToDetail(val id: Int) : TaskListEvent
}