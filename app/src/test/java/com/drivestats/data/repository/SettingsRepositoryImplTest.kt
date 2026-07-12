package com.drivestats.data.repository

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.drivestats.domain.model.DistanceUnit
import com.google.common.truth.Truth.assertThat
import java.io.File
import java.nio.file.Files
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SettingsRepositoryImplTest {

    @Test
    fun observeSettings_defaultsToKilometres() = runTest {
        val file = createTempPreferencesFile()
        val (repository, scope) = createRepository(file, testScheduler)

        try {
            val settings = repository.observeSettings().first()

            assertThat(settings.autoDetectEnabled).isTrue()
            assertThat(settings.cloudSyncEnabled).isFalse()
            assertThat(settings.distanceUnit).isEqualTo(DistanceUnit.KILOMETERS)
        } finally {
            scope.cancel()
            file.delete()
        }
    }

    @Test
    fun setDistanceUnit_persistsAcrossRepositoryInstances() = runTest {
        val file = createTempPreferencesFile()
        val (firstRepository, firstScope) = createRepository(file, testScheduler)

        try {
            firstRepository.setDistanceUnit(DistanceUnit.MILES)
            advanceUntilIdle()
        } finally {
            firstScope.cancel()
        }

        val (secondRepository, secondScope) = createRepository(file, testScheduler)
        try {
            val settings = secondRepository.observeSettings().first()

            assertThat(settings.distanceUnit).isEqualTo(DistanceUnit.MILES)
        } finally {
            secondScope.cancel()
            file.delete()
        }
    }

    private fun createRepository(
        file: File,
        scheduler: TestCoroutineScheduler,
    ): Pair<SettingsRepositoryImpl, CoroutineScope> {
        val scope = CoroutineScope(StandardTestDispatcher(scheduler) + SupervisorJob())
        val dataStore = PreferenceDataStoreFactory.create(
            scope = scope,
            produceFile = { file },
        )
        return SettingsRepositoryImpl(dataStore) to scope
    }

    private fun createTempPreferencesFile(): File =
        Files.createTempFile("settings", ".preferences_pb").toFile()
}
