package com.example.tapshyrmaapp.presentation.ui.screens.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tapshyrmaapp.data.database.entity.TaskModel
import com.example.tapshyrmaapp.extensions.toFormattedDateTime
import com.example.tapshyrmaapp.presentation.ui.appbars.TapshyrmaTopBar
import com.example.tapshyrmaapp.presentation.ui.appbars.TaskFilter
import com.example.tapshyrmaapp.presentation.ui.floatingbutton.AddTaskFloatingButton
import com.example.tapshyrmaapp.presentation.ui.floatingbutton.DeleteTaskFloatingButton
import com.example.tapshyrmaapp.presentation.ui.screens.TaskViewModel
import com.example.tapshyrmaapp.presentation.ui.theme.BackgroundBurgundy
import com.example.tapshyrmaapp.presentation.ui.theme.BackgroundMagenta
import com.example.tapshyrmaapp.presentation.ui.theme.BackgroundRed
import com.example.tapshyrmaapp.presentation.ui.theme.Typography

@Composable
fun HomeScreen(
    toDetailScreen: (Int) -> Unit,
    taskViewModel: TaskViewModel = viewModel(),
    onFloatingButtonClick: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf(TaskFilter.ALL) }
    var taskToDelete by remember { mutableStateOf<TaskModel?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val tasks by when (selectedFilter) {
        TaskFilter.ALL -> taskViewModel.allTasks.observeAsState(emptyList())
        TaskFilter.ACTIVE -> taskViewModel.getTasksByStatus(isCompleted = false)
            .observeAsState(emptyList())

        TaskFilter.COMPLETED -> taskViewModel.getTasksByStatus(isCompleted = true)
            .observeAsState(emptyList())
    }
    val areCompletedTasks =
        taskViewModel.getTasksByStatus(isCompleted = true).observeAsState(emptyList())

    Scaffold(
        topBar = {
            TapshyrmaTopBar(
                onFilterSelected = {
                    selectedFilter = it
                }
            )
        },
        floatingActionButton = {
            Column {
                if (areCompletedTasks.value.isNotEmpty()) {
                    DeleteTaskFloatingButton(onClick = {
                        showDeleteDialog = true
                    })
                }
                Spacer(Modifier.height(12.dp))
                AddTaskFloatingButton(onFloatingButtonClick)
            }
        }
    ) { innerPadding ->
        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No tasks",
                    style = TextStyle(
                        color = Color.Gray,
                        fontWeight = FontWeight.W400
                    )
                )
            }
        } else {
            TaskList(
                modifier = Modifier.padding(innerPadding),
                onClick = toDetailScreen,
                taskModels = tasks,
                onToggle = { taskViewModel.updateTask(it) },
                onLongPress = { task ->
                    taskToDelete = task
                }
            )
        }
    }
    taskToDelete?.let {
        DeleteTaskAlertDialog(
            taskTitle = it.title,
            onDismiss = { taskToDelete = null },
            onConfirm = {
                taskViewModel.deleteTask(it)
                taskToDelete = null
            }
        )
    }
    if (showDeleteDialog) {
        DeleteCompletedTasksAlertDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                taskViewModel.deleteCompletedTasks()
                showDeleteDialog = false
            }
        )
    }
}

@Composable
fun TaskList(
    modifier: Modifier,
    onClick: (Int) -> Unit,
    taskModels: List<TaskModel>,
    onToggle: (TaskModel) -> Unit,
    onLongPress: (TaskModel) -> Unit
) {
    val animatedBrush = animatedBorderBrush()
    LazyColumn(
        modifier = modifier
            .fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 140.dp)
    ) {
        items(taskModels) { task ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .combinedClickable(
                        onClick = { onClick(task.id) },
                        onLongClick = {
                            onLongPress(task)
                        }
                    )
                    .border(border = BorderStroke(2.dp, animatedBrush), RoundedCornerShape(8.dp)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(Modifier.width(8.dp))
                Text(
                    modifier = Modifier.width(200.dp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    text = task.title,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.W400
                    ),
                )
                Text(
                    modifier = Modifier.width(100.dp),
                    text = task.createdAt.toFormattedDateTime(),
                    style = Typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { isChecked ->
                        onToggle(task.copy(isCompleted = isChecked))
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = BackgroundMagenta,
                        uncheckedColor = BackgroundMagenta
                    )
                )
            }
        }
    }
}

@Composable
fun animatedBorderBrush(): Brush {
    val gradientColor = listOf(
        BackgroundMagenta,
        BackgroundBurgundy,
        BackgroundRed
    )
    val infiniteTransition = rememberInfiniteTransition(label = "task_border_animation")
    val animatedOffSet by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "gradient_offset"
    )

    return Brush.linearGradient(
        colors = gradientColor,
        start = Offset(0f, 0f),
        end = Offset(1000f * animatedOffSet, 0f)
    )
}

@Composable
fun DeleteTaskAlertDialog(
    taskTitle: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.background,
        icon = { Icon(Icons.Default.Delete, "Delete icon", tint = BackgroundMagenta) },
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                "Delete ${taskTitle}?",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                }
            ) {
                Text("Yes", fontSize = 16.sp, color = BackgroundMagenta)
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss() }
            ) {
                Text("Cancel", fontSize = 16.sp, color = BackgroundMagenta)
            }
        }
    )
}

@Composable
fun DeleteCompletedTasksAlertDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.background,
        icon = { Icon(Icons.Default.Delete, "Delete icon", tint = BackgroundMagenta) },
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                "Delete completed tasks?",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                }
            ) {
                Text("Yes", fontSize = 16.sp, color = BackgroundMagenta)
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss() }
            ) {
                Text("Cancel", fontSize = 16.sp, color = BackgroundMagenta)
            }
        }
    )
}