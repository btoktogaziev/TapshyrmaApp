package com.example.tapshyrmaapp.data.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PreferenceHelper(context: Context) {
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    var isSignInScreenShowed: Boolean
        get() = sharedPreferences.getBoolean("sign", false)
        set(value) = sharedPreferences.edit { putBoolean("sign", value) }
}