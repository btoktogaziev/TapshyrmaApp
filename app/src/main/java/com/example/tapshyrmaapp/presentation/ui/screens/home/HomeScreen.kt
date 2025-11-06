package com.example.tapshyrmaapp.presentation.ui.screens.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tapshyrmaapp.data.local.database.entity.TaskModel
import com.example.tapshyrmaapp.extensions.toFormattedDateTime
import com.example.tapshyrmaapp.presentation.ui.appbars.TapshyrmaTopBar
import com.example.tapshyrmaapp.presentation.ui.floatingbutton.AddTaskFloatingButton
import com.example.tapshyrmaapp.presentation.ui.floatingbutton.DeleteTaskFloatingButton
import com.example.tapshyrmaapp.presentation.ui.theme.BackgroundBurgundy
import com.example.tapshyrmaapp.presentation.ui.theme.BackgroundMagenta
import com.example.tapshyrmaapp.presentation.ui.theme.BackgroundRed
import com.example.tapshyrmaapp.presentation.ui.theme.Typography

@Composable
fun HomeScreen(
    toDetailScreen: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    onFloatingButtonClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsStateWithLifecycle(null)

    LaunchedEffect(event) {
        when (val e = event) {
            is TaskListEvent.NavigateToDetail -> {
                if (e.id == -1) {
                    onFloatingButtonClick()
                } else {
                    toDetailScreen(e.id)
                }
            }

            null -> Unit
        }
    }

    Scaffold(
        topBar = {
            TapshyrmaTopBar(
                onFilterSelected = viewModel::setFilter

            )
        },
        floatingActionButton = {
            Column {
                if (uiState.hasCompleted) {
                    DeleteTaskFloatingButton(onClick = viewModel::showDeleteCompletedTasksDialog)
                }
                Spacer(Modifier.height(12.dp))
                AddTaskFloatingButton(viewModel::onAddTaskClicked)
            }
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding), Alignment.Center
                ) {
                    Text("Loading", color = Color.Gray)
                }
            }

            uiState.tasks.isEmpty() -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding), Alignment.Center
                ) {
                    Text("No tasks", color = Color.Gray)
                }
            }

            else -> {
                TaskList(
                    modifier = Modifier.padding(innerPadding),
                    tasks = uiState.tasks,
                    onClick = viewModel::onTaskClicked,
                    onUpdate = viewModel::updateTaskCompletedState,
                    onLongPress = viewModel::showDeleteTaskDialog,
                    onSwipeRight = viewModel::showDeleteTaskDialog
                )
            }
        }
    }
    uiState.showDeleteTaskDialog?.let {
        DeleteTaskAlertDialog(
            taskTitle = it.title,
            onDismiss = viewModel::cancelDeleteTask,
            onConfirm = viewModel::confirmDeleteTask
        )
    }
    if (uiState.showDeleteCompletedDialog) {
        DeleteCompletedTasksAlertDialog(
            onDismiss = viewModel::cancelDeleteCompletedTasks,
            onConfirm = viewModel::confirmDeleteCompletedTasks
        )
    }
}

@Composable
fun TaskList(
    modifier: Modifier,
    tasks: List<TaskModel>,
    onClick: (Int) -> Unit,
    onUpdate: (TaskModel) -> Unit,
    onLongPress: (TaskModel) -> Unit,
    onSwipeRight: (TaskModel) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 140.dp)
    ) {
        items(tasks) {
            it.let { task ->
                val dismissBoxState = rememberSwipeToDismissBoxState(
                    confirmValueChange = {
                        onSwipeRight(task)
                        false
                    },
                    positionalThreshold = { fullWidth ->
                        fullWidth * 0.5f
                    }
                )
                val direction = dismissBoxState.dismissDirection
                SwipeToDismissBox(
                    state = dismissBoxState,
                    backgroundContent = {
                        val alignment = when (direction) {
                            SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                            SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                            else -> Alignment.Center
                        }
                        Box(
                            modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                            contentAlignment = alignment

                        ) {
                            Icon(
                                Icons.Default.Delete, "icon delete",
                                tint = BackgroundMagenta
                            )
                        }
                    }
                ) {
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
                            .border(
                                border = BorderStroke(2.dp, animatedBorderBrush()),
                                RoundedCornerShape(8.dp)
                            ),
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
                                onUpdate(task.copy(isCompleted = isChecked))
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
                "Delete task\n\"${taskTitle}\"?",
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