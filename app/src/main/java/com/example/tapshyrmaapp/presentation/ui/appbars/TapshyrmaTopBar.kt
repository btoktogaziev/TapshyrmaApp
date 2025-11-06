package com.example.tapshyrmaapp.presentation.ui.appbars

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.tapshyrmaapp.presentation.ui.screens.home.TaskFilter
import com.example.tapshyrmaapp.presentation.ui.theme.BackgroundBurgundy
import com.example.tapshyrmaapp.presentation.ui.theme.BackgroundMagenta
import com.example.tapshyrmaapp.presentation.ui.theme.BackgroundRed

@Composable
fun TapshyrmaTopBar(
    onFilterSelected: (TaskFilter) -> Unit = {},
) {
    var menuExpanded by remember { mutableStateOf(false) }
    @OptIn(ExperimentalMaterial3Api::class)
    TopAppBar(
        modifier = Modifier.background(appBarGradientColor()),
        title = { Text("Tapshyrma") },
        actions = {
            IconButton(onClick = {
                menuExpanded = true
            }) {
                Icon(Icons.TwoTone.Menu, contentDescription = "Menu")
            }
            DropMenu(
                menuExpanded,
                onDismiss = { menuExpanded = false },
                onFilterSelected = onFilterSelected
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

@Composable
fun appBarGradientColor(): Brush {
    val gradientColor = listOf(
        BackgroundMagenta, BackgroundBurgundy, BackgroundRed, BackgroundMagenta
    )
    val infiniteTransition = rememberInfiniteTransition("gradient_appbar_animation")
    val animatedOffSet = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient_offset"
    )
    val animatedBrush = Brush.linearGradient(
        colors = gradientColor,
        start = Offset(0f, 0f),
        end = Offset(1000f * animatedOffSet.value, 0f)
    )
    return animatedBrush
}



@Composable
fun DropMenu(expanded: Boolean, onFilterSelected: (TaskFilter) -> Unit, onDismiss: () -> Unit) {
    DropdownMenu(
        containerColor = MaterialTheme.colorScheme.background,
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        DropdownMenuItem(
            text = {
                Text("All tasks", color = MaterialTheme.colorScheme.onBackground)
            },
            onClick = {
                onFilterSelected(TaskFilter.ALL)
                onDismiss()
            }
        )
        DropdownMenuItem(
            text = {
                Text("Active tasks", color = MaterialTheme.colorScheme.onBackground)
            },
            onClick = {
                onFilterSelected(TaskFilter.ACTIVE)
                onDismiss()
            }
        )
        DropdownMenuItem(
            text = {
                Text(
                    "Completed tasks",
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            onClick = {
                onFilterSelected(TaskFilter.COMPLETED)
                onDismiss()
            }
        )
    }
}

