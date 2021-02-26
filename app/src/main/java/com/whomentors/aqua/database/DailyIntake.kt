package com.whomentors.aqua.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity for storing intake.
 */
@Entity(tableName = "daily_water_intake_table", indices=[Index(value=["date"], unique=true)])
data class DailyIntake(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    var date: String,
    var intake:Int,
    @ColumnInfo(name = "total_intake")
    var totalIntake: Int
)