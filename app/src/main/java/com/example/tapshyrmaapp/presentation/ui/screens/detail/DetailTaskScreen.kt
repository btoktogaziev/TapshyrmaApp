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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tapshyrmaapp.extensions.toFormattedDateTime
import com.example.tapshyrmaapp.presentation.ui.screens.home.animatedBorderBrush
import com.example.tapshyrmaapp.presentation.ui.theme.BackgroundMagenta
import com.example.tapshyrmaapp.presentation.ui.theme.Typography

@Composable
fun DetailTaskScreen(
    id: Int,
    viewModel: DetailTaskViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsStateWithLifecycle(null)
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.init(id)
    }
    LaunchedEffect(event) {
        when (event) {
            DetailTaskEvent.NavigateBack -> onBack()
            null -> Unit
        }
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        EditTextSection(
            text = uiState.title,
            onUpdate = viewModel::onTitleChanged,
            hintFontSize = 28,
            hintTaskText = "title",
            isError = uiState.isEmptyTitle,
            supportingText = if (uiState.isEmptyTitle) {
                {
                    Text("Title cannot be empty", color = MaterialTheme.colorScheme.error)
                }
            } else null,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions { focusManager.moveFocus(FocusDirection.Down) }
        )
        if (uiState.taskId != -1) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.End) {
                Text(
                    text = uiState.createdAt.toFormattedDateTime(),
                    color = Color.Gray,
                    fontSize = 20.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        EditTextSection(
            text = uiState.description,
            onUpdate = viewModel::onDescriptionChanged,
            hintFontSize = 24,
            hintTaskText = "description",
            linesCount = 3,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions { focusManager.clearFocus() }
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp),
            border = BorderStroke(width = 2.dp, brush = animatedBorderBrush()),
            onClick = viewModel::onSaveClicked,
            enabled = !uiState.isLoading && uiState.title.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.background,
                disabledContainerColor = MaterialTheme.colorScheme.background
            )
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onBackground,
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = "Save task",
                style = Typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

    }
}

@Composable
fun EditTextSection(
    text: String,
    onUpdate: (String) -> Unit,
    hintTaskText: String,
    hintFontSize: Int,
    isError: Boolean = false,
    linesCount: Int = 1,
    supportingText: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    OutlinedTextField(
        value = text,
        onValueChange = onUpdate,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "Enter task $hintTaskText",
                fontSize = hintFontSize.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BackgroundMagenta,
            unfocusedBorderColor = BackgroundMagenta,
            focusedContainerColor = MaterialTheme.colorScheme.background,
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            errorBorderColor = MaterialTheme.colorScheme.error,
            errorContainerColor = MaterialTheme.colorScheme.background
        ),
        isError = isError,
        minLines = linesCount,
        supportingText = supportingText,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        textStyle = (TextStyle(
            fontSize = 28.sp,
            fontFamily = FontFamily.SansSerif
        ))
    )
}