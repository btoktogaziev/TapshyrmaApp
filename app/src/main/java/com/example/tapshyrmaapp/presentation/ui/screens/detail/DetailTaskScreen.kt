package com.example.tapshyrmaapp.presentation.ui.screens.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tapshyrmaapp.data.database.entity.TaskModel
import com.example.tapshyrmaapp.extensions.toFormattedDateTime
import com.example.tapshyrmaapp.presentation.ui.screens.TaskViewModel
import com.example.tapshyrmaapp.presentation.ui.screens.home.animatedBorderBrush
import com.example.tapshyrmaapp.presentation.ui.theme.BackgroundMagenta
import com.example.tapshyrmaapp.presentation.ui.theme.Typography

@Composable
fun DetailTaskScreen(
    id: Int,
    viewModel: TaskViewModel = viewModel(),
    navController: NavController
) {
    val taskState by viewModel.currentTask.observeAsState()
    var editableTask by remember { mutableStateOf<TaskModel?>(null) }
    var isInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(id) {
        if (id > 0) {
            viewModel.getTaskById(id)
        } else {
            editableTask = TaskModel(title = "", description = "")
            isInitialized = true
        }
    }

    LaunchedEffect(taskState) {
        if (taskState != null) {
            editableTask = taskState
            isInitialized = true
        }
    }

    val onSave = {
        val taskToSave = editableTask
        if (taskToSave != null && taskToSave.title.isNotBlank()) {
            if (taskToSave.id == 0) {
                viewModel.insertTask(taskToSave)
            } else {
                viewModel.updateTask(taskToSave)
            }
            navController.popBackStack()
        }
    }

    if (isInitialized && editableTask != null) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            editableTask?.let { task ->
                TitleSection(
                    taskModel = task
                ) {
                    editableTask = it
                }
                Spacer(modifier = Modifier.height(16.dp))
                DescriptionSection(
                    taskModel = task
                ) {
                    editableTask = it
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    border = BorderStroke(width = 2.dp, brush = animatedBorderBrush()),
                    onClick = onSave,
                    enabled = task.title.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        disabledContainerColor = MaterialTheme.colorScheme.background
                    )
                ) {
                    Text(
                        text = if (task.title != "") "Save task" else "Title is empty",
                        style = Typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

            }
        }
    } else {
        Text(
            text = "Loading...", modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TitleSection(taskModel: TaskModel, onUpdate: (TaskModel) -> Unit) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = taskModel.title,
        onValueChange = { newTitle ->
            onUpdate(taskModel.copy(title = newTitle))
        },
        placeholder = { Text("Enter task title", fontSize = 28.sp) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BackgroundMagenta,
            unfocusedBorderColor = BackgroundMagenta,
            focusedContainerColor = MaterialTheme.colorScheme.background,
            unfocusedContainerColor = MaterialTheme.colorScheme.background
        ),
        textStyle = TextStyle(
            fontSize = 28.sp,
            fontFamily = FontFamily.SansSerif
        )
    )
    Spacer(modifier = Modifier.height(16.dp))
    if (taskModel.id != 0) {
        Row(horizontalArrangement = Arrangement.End) {
            Text(
                text = taskModel.createdAt.toFormattedDateTime(),
                color = Color.Gray,
                fontSize = 20.sp
            )
        }
    }
}

@Composable
fun DescriptionSection(taskModel: TaskModel, onUpdate: (TaskModel) -> Unit) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = taskModel.description,
        onValueChange = { newDescription ->
            onUpdate(taskModel.copy(description = newDescription))
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BackgroundMagenta,
            unfocusedBorderColor = BackgroundMagenta,
            focusedContainerColor = MaterialTheme.colorScheme.background,
            unfocusedContainerColor = MaterialTheme.colorScheme.background
        ),
        placeholder = { Text("Enter task description", fontSize = 24.sp) },
        minLines = 3,
        textStyle = TextStyle(
            fontSize = 24.sp,
            fontFamily = FontFamily.SansSerif
        )
    )
}
