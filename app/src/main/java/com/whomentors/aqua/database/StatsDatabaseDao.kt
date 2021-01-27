package com.whomentors.aqua.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface StatsDatabaseDao {
    // Insert today's entry with total intake
    // When the intake changes we update this initially updated object.
    @Insert
    suspend fun insert(dailyIntake: DailyIntake)

    // Updates daily intake object.
    @Update
    suspend fun update(dailyIntake: DailyIntake)

    // Get dailyIntake of the date provided
    @Query("SELECT * from daily_water_intake_table ORDER BY id DESC LIMIT 1")
    suspend fun getTodayIntake(): DailyIntake?

    // Get all the intakes
    @Query("SELECT * from daily_water_intake_table")
    suspend fun getAllIntakes(): LiveData<List<DailyIntake>>
}