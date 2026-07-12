package com.drivestats.ui.format

import com.drivestats.domain.model.DistanceUnit
import com.drivestats.domain.model.DrivingEvent
import com.drivestats.domain.model.EventType
import java.util.Locale

private const val METERS_PER_KILOMETRE = 1000.0
private const val METERS_PER_MILE = 1609.344
private const val KILOMETRES_PER_MILE = 1.609344
private val speedingPattern = Regex("""^Speed: ([0-9]+(?:\.[0-9]+)?) (km/h|mph)$""")

fun formatDistance(
    distanceMeters: Double,
    distanceUnit: DistanceUnit,
    locale: Locale = Locale.getDefault(),
): String {
    val distance = when (distanceUnit) {
        DistanceUnit.KILOMETRES -> distanceMeters / METERS_PER_KILOMETRE
        DistanceUnit.MILES -> distanceMeters / METERS_PER_MILE
    }
    return String.format(locale, "%.1f %s", distance, distanceUnit.distanceSuffix)
}

fun formatDrivingEventDetails(
    event: DrivingEvent,
    distanceUnit: DistanceUnit,
    locale: Locale = Locale.getDefault(),
): String = when (event.type) {
    EventType.SPEEDING -> formatSpeedDetails(event.details, distanceUnit, locale)
    else -> event.details
}

private fun formatSpeedDetails(
    details: String,
    distanceUnit: DistanceUnit,
    locale: Locale,
): String {
    val match = speedingPattern.matchEntire(details) ?: return details
    val sourceValue = match.groupValues[1].toDoubleOrNull() ?: return details
    val speedKmh = when (match.groupValues[2]) {
        DistanceUnit.MILES.speedSuffix -> sourceValue * KILOMETRES_PER_MILE
        else -> sourceValue
    }
    val convertedValue = when (distanceUnit) {
        DistanceUnit.KILOMETRES -> speedKmh
        DistanceUnit.MILES -> speedKmh / KILOMETRES_PER_MILE
    }
    return String.format(locale, "Speed: %.0f %s", convertedValue, distanceUnit.speedSuffix)
}
