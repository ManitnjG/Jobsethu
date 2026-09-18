package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("jobsetu_prefs", Context.MODE_PRIVATE)

    private val _language = MutableStateFlow(loadLanguage())
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _isDataSaver = MutableStateFlow(prefs.getBoolean("key_data_saver", false))
    val isDataSaver: StateFlow<Boolean> = _isDataSaver.asStateFlow()

    private val _isDemoMode = MutableStateFlow(prefs.getBoolean("key_demo_mode", true))
    val isDemoMode: StateFlow<Boolean> = _isDemoMode.asStateFlow()

    private fun loadLanguage(): AppLanguage {
        val code = prefs.getString("key_language", "en") ?: "en"
        return AppLanguage.values().firstOrNull { it.code == code } ?: AppLanguage.ENGLISH
    }

    fun setLanguage(lang: AppLanguage) {
        prefs.edit().putString("key_language", lang.code).apply()
        _language.value = lang
    }

    fun setDataSaver(enabled: Boolean) {
        prefs.edit().putBoolean("key_data_saver", enabled).apply()
        _isDataSaver.value = enabled
    }

    fun setDemoMode(enabled: Boolean) {
        prefs.edit().putBoolean("key_demo_mode", enabled).apply()
        _isDemoMode.value = enabled
    }
}
