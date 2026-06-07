package com.drivestats.data.repository

import com.drivestats.data.local.dao.DrivingEventDao
import com.drivestats.data.local.dao.LocationPointDao
import com.drivestats.data.local.dao.MotionWindowDao
import com.drivestats.data.local.dao.TripScoreDao
import com.drivestats.data.local.dao.TripSessionDao
import com.drivestats.data.local.entity.DrivingEventEntity
import com.drivestats.data.local.entity.TripScoreEntity
import com.drivestats.data.local.entity.TripSessionEntity
import com.drivestats.domain.model.DrivingEvent
import com.drivestats.domain.model.EventType
import com.drivestats.domain.model.TripScore
import com.drivestats.domain.model.TripSession
import com.drivestats.domain.model.TripState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripRepositoryImpl @Inject constructor(
    private val tripSessionDao: TripSessionDao,
    private val locationPointDao: LocationPointDao,
    private val motionWindowDao: MotionWindowDao,
    private val drivingEventDao: DrivingEventDao,
    private val tripScoreDao: TripScoreDao,
) : TripRepository {

    // ── Trip sessions ──────────────────────────────────────────────────────

    override suspend fun startTrip(startTimeMs: Long): Long =
        tripSessionDao.insert(TripSessionEntity(startTimeMs = startTimeMs))

    override suspend fun activateTrip(tripId: Long) =
        tripSessionDao.updateState(tripId, TripState.ACTIVE.name)

    override suspend fun endTrip(tripId: Long, endTimeMs: Long, distanceMeters: Double) {
        tripSessionDao.updateEndAndDistance(tripId, endTimeMs, distanceMeters)
        tripSessionDao.updateState(tripId, TripState.COMPLETED.name)
    }

    override suspend fun markAsPassenger(tripId: Long) {
        tripSessionDao.updatePassengerFlag(tripId, true)
        tripSessionDao.updateState(tripId, TripState.DISCARDED.name)
    }

    override suspend fun deleteTrip(tripId: Long) =
        tripSessionDao.deleteById(tripId)

    override fun observeAllTrips(): Flow<List<TripSession>> =
        tripSessionDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun observeTrip(tripId: Long): Flow<TripSession?> =
        tripSessionDao.observeById(tripId).map { it?.toDomain() }

    override suspend fun getActiveTrip(): TripSession? =
        tripSessionDao.getActiveTrip()?.toDomain()

    // ── Driving events ─────────────────────────────────────────────────────

    override suspend fun saveEvent(event: DrivingEvent) {
        drivingEventDao.insert(event.toEntity())
    }

    override suspend fun saveEvents(events: List<DrivingEvent>) {
        drivingEventDao.insertAll(events.map { it.toEntity() })
    }

    override fun observeEventsForTrip(tripId: Long): Flow<List<DrivingEvent>> =
        drivingEventDao.observeByTripId(tripId).map { entities ->
            entities.map { it.toDomain() }
        }

    // ── Scores ─────────────────────────────────────────────────────────────

    override suspend fun saveScore(score: TripScore) {
        tripScoreDao.insert(score.toEntity())
    }

    override fun observeScoreForTrip(tripId: Long): Flow<TripScore?> =
        tripScoreDao.observeByTripId(tripId).map { it?.toDomain() }

    // ── Insights ───────────────────────────────────────────────────────────

    override fun observeAverageSafetyScore(fromMs: Long): Flow<Float?> =
        tripScoreDao.observeAverageSafetyScoreSince(fromMs)

    override fun observeAverageQualityScore(fromMs: Long): Flow<Float?> =
        tripScoreDao.observeAverageQualityScoreSince(fromMs)

    override fun observeCompletedTripCount(): Flow<Int> =
        tripSessionDao.observeCompletedTripCount()

    // ── Mappers ───────────────────────────────────────────────────────────

    private fun TripSessionEntity.toDomain() = TripSession(
        id = id,
        startTimeMs = startTimeMs,
        endTimeMs = endTimeMs,
        distanceMeters = distanceMeters,
        state = TripState.valueOf(state),
        isPassenger = isPassenger,
    )

    private fun DrivingEvent.toEntity() = DrivingEventEntity(
        id = id,
        tripId = tripId,
        type = type.name,
        timestampMs = timestampMs,
        durationMs = durationMs,
        severity = severity,
        details = details,
        latitude = latitude,
        longitude = longitude,
    )

    private fun DrivingEventEntity.toDomain() = DrivingEvent(
        id = id,
        tripId = tripId,
        type = EventType.valueOf(type),
        timestampMs = timestampMs,
        durationMs = durationMs,
        severity = severity,
        details = details,
        latitude = latitude,
        longitude = longitude,
    )

    private fun TripScore.toEntity() = TripScoreEntity(
        tripId = tripId,
        safetyScore = safetyScore,
        smoothnessScore = smoothnessScore,
        efficiencyScore = efficiencyScore,
        tripQualityScore = tripQualityScore,
        starRating = starRating,
        breakdownJson = breakdown.entries.joinToString(",", "{", "}") { (k, v) ->
            "\"${k.replace("\"", "\\\"")}\":\"${v.replace("\"", "\\\"")}\""
        },
        signalConfidence = signalConfidence,
    )

    private fun TripScoreEntity.toDomain() = TripScore(
        tripId = tripId,
        safetyScore = safetyScore,
        smoothnessScore = smoothnessScore,
        efficiencyScore = efficiencyScore,
        tripQualityScore = tripQualityScore,
        starRating = starRating,
        breakdown = parseBreakdownJson(breakdownJson),
        signalConfidence = signalConfidence,
    )

    /** Simple JSON key-value parser (avoids adding a JSON library dependency). */
    private fun parseBreakdownJson(json: String): Map<String, String> {
        if (json == "{}" || json.isBlank()) return emptyMap()
        return try {
            val inner = json.removeSurrounding("{", "}")
            // Split on ","" (end of value, comma, start of next key quote)
            val pairs = inner.split("\",\"")
            pairs.associate { pair ->
                val colonIdx = pair.indexOf("\":\"")
                if (colonIdx < 0) return@associate "" to ""
                val key = pair.substring(0, colonIdx).removePrefix("\"")
                val value = pair.substring(colonIdx + 3).removeSuffix("\"")
                key to value
            }.filterKeys { it.isNotEmpty() }
        } catch (_: Exception) {
            emptyMap()
        }
    }
}
