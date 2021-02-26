package com.whomentors.aqua.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DailyIntake::class], version = 1, exportSchema = false)
abstract class StatsDatabase: RoomDatabase() {
    abstract val statsDatabaseDao: StatsDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: StatsDatabase? = null

        fun getInstance(context: Context): StatsDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        StatsDatabase::class.java,
                        "water_intake_history_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}