package com.drivestats.ui.format

import com.drivestats.domain.model.DistanceUnit
import com.drivestats.domain.model.DrivingEvent
import com.drivestats.domain.model.EventType
import com.google.common.truth.Truth.assertThat
import java.util.Locale
import org.junit.Test

class MeasurementFormatterTest {

    @Test
    fun formatDistance_formatsKilometresToOneDecimalPlace() {
        assertThat(formatDistance(1_250.0, DistanceUnit.KILOMETERS, Locale.US))
            .isEqualTo("1.3 km")
    }

    @Test
    fun formatDistance_formatsMilesToOneDecimalPlace() {
        assertThat(formatDistance(1_609.344, DistanceUnit.MILES, Locale.US))
            .isEqualTo("1.0 mi")
    }

    @Test
    fun formatDrivingEventDetails_convertsStoredKilometresPerHourToMilesPerHour() {
        val event = DrivingEvent(
            tripId = 1L,
            type = EventType.SPEEDING,
            timestampMs = 0L,
            details = "Speed: 120 km/h",
        )

        assertThat(formatDrivingEventDetails(event, DistanceUnit.MILES, Locale.US))
            .isEqualTo("Speed: 75 mph")
    }

    @Test
    fun formatDrivingEventDetails_returnsOriginalTextWhenSpeedingFormatIsUnknown() {
        val event = DrivingEvent(
            tripId = 1L,
            type = EventType.SPEEDING,
            timestampMs = 0L,
            details = "Exceeded limit by 20 km/h",
        )

        assertThat(formatDrivingEventDetails(event, DistanceUnit.MILES, Locale.US))
            .isEqualTo("Exceeded limit by 20 km/h")
    }

    @Test
    fun formatDrivingEventDetails_leavesNonSpeedingEventsUnchanged() {
        val event = DrivingEvent(
            tripId = 1L,
            type = EventType.HARD_BRAKING,
            timestampMs = 0L,
            details = "Deceleration: 6.0 m/s²",
        )

        assertThat(formatDrivingEventDetails(event, DistanceUnit.MILES, Locale.US))
            .isEqualTo("Deceleration: 6.0 m/s²")
    }
}
