package com.example.tapshyrmaapp.presentation.ui.floatingbutton

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.tapshyrmaapp.presentation.ui.theme.BackgroundMagenta

@Composable
fun AddTaskFloatingButton(onClick: () -> Unit) {
    FloatingActionButton(
        containerColor = BackgroundMagenta,
        onClick = onClick
    ) {
        Icon(Icons.Filled.Add, contentDescription = "Add", tint = Color.White)
    }
}

@Composable
fun DeleteTaskFloatingButton(onClick: () -> Unit) {
    FloatingActionButton(
        containerColor = BackgroundMagenta,
        onClick = onClick
    ) {
        Icon(Icons.Filled.Delete, contentDescription = "Delete completed", tint = Color.White)
    }
}
