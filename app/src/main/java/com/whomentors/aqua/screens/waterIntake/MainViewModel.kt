package com.whomentors.aqua.screens.waterIntake

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.whomentors.aqua.AppUtils.Thisapp
import com.whomentors.aqua.database.DailyIntake
import com.whomentors.aqua.database.StatsDatabaseDao
import kotlinx.coroutines.launch

/**
 * A view model that stores and handles operations
 * related to daily water intake.
 */
class MainViewModel(
    val database: StatsDatabaseDao,
    application: Application): AndroidViewModel(application) {

    private var todayEntry = MutableLiveData<DailyIntake>()
    init {
        initializeToday()
    }

    private fun initializeToday(){
        viewModelScope.launch {
            todayEntry.value = getTodayFromDatabase()
        }
    }

    private suspend fun getTodayFromDatabase(): DailyIntake?{
        var entry = database.getTodayIntake()
        if (entry?.date != Thisapp.getCurrentDate()){
            entry = null
        }
        return entry
    }

    /**
     * Get observable today's entry
     */
    fun getTodayEntry(): LiveData<DailyIntake>{
        return todayEntry
    }

    /**
     * Adds today's intake entry if it does not
     * already exists.
     */
    fun insertTodayIntake(totalIntake: Int){
        viewModelScope.launch {
            todayEntry.value = getTodayFromDatabase()

            // Check if an entry exists for today
            if (todayEntry.value == null){
                val todayIntake = DailyIntake(
                    date = Thisapp.getCurrentDate(),
                    intake = 0,
                    totalIntake = totalIntake)
                insert(todayIntake)
                todayEntry.value = todayIntake
            }
        }
    }

    /**
     * Adds provided intake value to today's intake.
     */
    fun addToTodayIntake(intake: Int){
        viewModelScope.launch {
            val entry = todayEntry.value!!
            if (entry.intake < entry.totalIntake){
                entry.intake += intake
                update(entry)
                todayEntry.value = entry
            }
        }
    }

    /**
     * Update total intake
     */
    fun updateTotalIntake(totalIntake: Int){
        viewModelScope.launch {
            val entry = todayEntry.value!!
            entry.totalIntake = totalIntake
            update(entry)
            todayEntry.value = entry
        }
    }

    private suspend fun insert(todayIntake: DailyIntake){
        database.insert(todayIntake)
    }

    private suspend fun update(todayIntake: DailyIntake){
        database.update(todayIntake)
    }
}