package com.drivestats.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.drivestats.domain.model.AppSettings
import com.drivestats.domain.model.DistanceUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {

    override fun observeSettings(): Flow<AppSettings> = dataStore.data.map { prefs ->
        AppSettings(
            autoDetectEnabled = prefs[KEY_AUTO_DETECT] ?: true,
            cloudSyncEnabled = prefs[KEY_CLOUD_SYNC] ?: false,
            distanceUnit = prefs[KEY_DISTANCE_UNIT].toDistanceUnit(),
        )
    }

    override fun observeDistanceUnit(): Flow<DistanceUnit> =
        observeSettings()
            .map { it.distanceUnit }
            .distinctUntilChanged()

    override suspend fun setAutoDetect(enabled: Boolean) {
        dataStore.edit { it[KEY_AUTO_DETECT] = enabled }
    }

    override suspend fun setCloudSync(enabled: Boolean) {
        dataStore.edit { it[KEY_CLOUD_SYNC] = enabled }
    }

    override suspend fun setDistanceUnit(distanceUnit: DistanceUnit) {
        dataStore.edit { it[KEY_DISTANCE_UNIT] = distanceUnit.name }
    }

    private fun String?.toDistanceUnit(): DistanceUnit = try {
        this?.let(DistanceUnit::valueOf) ?: DistanceUnit.KILOMETRES
    } catch (_: IllegalArgumentException) {
        DistanceUnit.KILOMETRES
    }

    companion object {
        private val KEY_AUTO_DETECT = booleanPreferencesKey("auto_detect")
        private val KEY_CLOUD_SYNC = booleanPreferencesKey("cloud_sync")
        private val KEY_DISTANCE_UNIT = stringPreferencesKey("distance_unit")
    }
}
