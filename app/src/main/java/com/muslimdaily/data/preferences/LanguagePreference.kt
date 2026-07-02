package com.muslimdaily.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.languageDataStore by preferencesDataStore(name = "language_settings")

enum class AppLanguage(val code: String) {
    English("en"),
    Persian("fa");

    companion object {
        fun fromCode(code: String?): AppLanguage = entries.firstOrNull { it.code == code } ?: English
    }
}

class LanguagePreference(private val context: Context) {
    private val languageKey = stringPreferencesKey("selected_language")

    val language: Flow<AppLanguage> = context.languageDataStore.data.map { preferences ->
        AppLanguage.fromCode(preferences[languageKey])
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.languageDataStore.edit { preferences ->
            preferences[languageKey] = language.code
        }
    }
}
