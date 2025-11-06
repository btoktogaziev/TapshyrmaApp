package com.example.tapshyrmaapp.presentation.ui.screens.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tapshyrmaapp.data.local.database.entity.TaskModel
import com.example.tapshyrmaapp.domain.usecase.CreateTaskUseCase
import com.example.tapshyrmaapp.domain.usecase.GetTaskByIdUseCase
import com.example.tapshyrmaapp.domain.usecase.UpdateTaskUseCase
import com.example.tapshyrmaapp.presentation.ui.screens.home.TaskListEvent
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
class DetailTaskViewModel @Inject constructor(
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val createTaskUseCase: CreateTaskUseCase,
) : ViewModel() {
    data class DetailUiState(
        var taskId: Int = -1,
        val title: String = "",
        val description: String = "",
        val isLoading: Boolean = false,
        val createdAt: Long = System.currentTimeMillis(),
        val isEmptyTitle: Boolean = false
    )

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _event = Channel<DetailTaskEvent>()
    val event = _event.receiveAsFlow()

    private var isInitialized = false

    fun init(id: Int) {
        if (isInitialized) return
        isInitialized = true
        _uiState.update { it.copy(taskId = id) }
        if (id != -1) {
            loadTask(id)
        } else {
            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }

    private fun loadTask(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                getTaskByIdUseCase.invoke(id).collect { task ->
                    task?.let {
                        _uiState.update { state ->
                            state.copy(
                                title = it.title,
                                description = it.description,
                                createdAt = it.createdAt,
                                isLoading = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("DetailTaskVM", "onload task: ${e.message}")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onTitleChanged(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun onDescriptionChanged(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onSaveClicked() {
        val state = _uiState.value
        if (state.title.isEmpty()) {
            _uiState.update { it.copy(isEmptyTitle = true) }
            return
        }

        viewModelScope.launch {
            val task = TaskModel(
                id = if (state.taskId == -1) 0 else state.taskId,
                title = state.title.trim(),
                description = state.description.trim(),
                createdAt = if (state.taskId == -1) System.currentTimeMillis() else state.createdAt,
                isCompleted = false
            )
            try {
                if (state.taskId == -1) {
                    createTaskUseCase(task)
                } else {
                    updateTaskUseCase(task)
                }
                sendEvent(DetailTaskEvent.NavigateBack)
            } catch (e: Exception) {
                Log.e("DetailTaskVM", "onSaveClicked: ${e.message}")
            }
        }

    }

    private fun sendEvent(event: DetailTaskEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}

sealed interface DetailTaskEvent {
    object NavigateBack : DetailTaskEvent
}