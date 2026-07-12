package com.drivestats.feature.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drivestats.data.repository.SettingsRepository
import com.drivestats.data.repository.TripRepository
import com.drivestats.domain.model.DistanceUnit
import com.drivestats.domain.model.TripSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TripListUiState(
    val trips: List<TripSession> = emptyList(),
    val distanceUnit: DistanceUnit = DistanceUnit.KILOMETRES,
    val isLoading: Boolean = true,
)

@HiltViewModel
class TripListViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val uiState: StateFlow<TripListUiState> =
        combine(
            tripRepository.observeAllTrips(),
            settingsRepository.observeDistanceUnit(),
        ) { trips, distanceUnit ->
            TripListUiState(
                trips = trips,
                distanceUnit = distanceUnit,
                isLoading = false,
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = TripListUiState(),
            )

    fun deleteTrip(tripId: Long) {
        viewModelScope.launch {
            tripRepository.deleteTrip(tripId)
        }
    }
}
