package com.drivestats.feature.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "settings")

data class SettingsUiState(
    val autoDetectEnabled: Boolean = true,
    val cloudSyncEnabled: Boolean = false,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
) : ViewModel() {

    companion object {
        private val KEY_AUTO_DETECT = booleanPreferencesKey("auto_detect")
        private val KEY_CLOUD_SYNC = booleanPreferencesKey("cloud_sync")
    }

    val uiState: StateFlow<SettingsUiState> = context.dataStore.data
        .map { prefs ->
            SettingsUiState(
                autoDetectEnabled = prefs[KEY_AUTO_DETECT] ?: true,
                cloudSyncEnabled = prefs[KEY_CLOUD_SYNC] ?: false,
            )
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, SettingsUiState())

    fun setAutoDetect(enabled: Boolean) {
        viewModelScope.launch {
            context.dataStore.edit { it[KEY_AUTO_DETECT] = enabled }
        }
    }

    fun setCloudSync(enabled: Boolean) {
        viewModelScope.launch {
            context.dataStore.edit { it[KEY_CLOUD_SYNC] = enabled }
        }
    }
}
