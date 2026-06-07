package com.drivestats.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.drivestats.data.local.dao.DrivingEventDao
import com.drivestats.data.local.dao.LocationPointDao
import com.drivestats.data.local.dao.MotionWindowDao
import com.drivestats.data.local.dao.TripScoreDao
import com.drivestats.data.local.dao.TripSessionDao
import com.drivestats.data.local.entity.DrivingEventEntity
import com.drivestats.data.local.entity.LocationPointEntity
import com.drivestats.data.local.entity.MotionWindowEntity
import com.drivestats.data.local.entity.TripScoreEntity
import com.drivestats.data.local.entity.TripSessionEntity

@Database(
    entities = [
        TripSessionEntity::class,
        LocationPointEntity::class,
        MotionWindowEntity::class,
        DrivingEventEntity::class,
        TripScoreEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripSessionDao(): TripSessionDao
    abstract fun locationPointDao(): LocationPointDao
    abstract fun motionWindowDao(): MotionWindowDao
    abstract fun drivingEventDao(): DrivingEventDao
    abstract fun tripScoreDao(): TripScoreDao

    companion object {
        const val DATABASE_NAME = "drivestats.db"
    }
}
