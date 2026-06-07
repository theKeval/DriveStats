package com.drivestats.feature.trips

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drivestats.data.repository.TripRepository
import com.drivestats.domain.model.DrivingEvent
import com.drivestats.domain.model.TripScore
import com.drivestats.domain.model.TripSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TripDetailUiState(
    val trip: TripSession? = null,
    val events: List<DrivingEvent> = emptyList(),
    val score: TripScore? = null,
    val isLoading: Boolean = true,
)

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tripRepository: TripRepository,
) : ViewModel() {

    private val tripId: Long = checkNotNull(savedStateHandle["tripId"])

    val uiState: StateFlow<TripDetailUiState> = combine(
        tripRepository.observeTrip(tripId),
        tripRepository.observeEventsForTrip(tripId),
        tripRepository.observeScoreForTrip(tripId),
    ) { trip, events, score ->
        TripDetailUiState(
            trip = trip,
            events = events,
            score = score,
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TripDetailUiState(),
    )

    fun markAsPassenger() {
        viewModelScope.launch {
            tripRepository.markAsPassenger(tripId)
        }
    }

    fun deleteTrip(onDeleted: () -> Unit) {
        viewModelScope.launch {
            tripRepository.deleteTrip(tripId)
            onDeleted()
        }
    }
}
