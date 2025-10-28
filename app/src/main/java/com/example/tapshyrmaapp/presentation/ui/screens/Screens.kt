package com.example.tapshyrmaapp.presentation.ui.screens

sealed class Screens(val route: String) {
    data object HomeScreen : Screens("HomeScreen")
    data object DetailTaskScreen : Screens("DetailTaskScreen")
}