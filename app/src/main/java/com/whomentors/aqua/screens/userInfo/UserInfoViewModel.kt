package com.whomentors.aqua.screens.userInfo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.whomentors.aqua.AppUtils.Thisapp
import com.whomentors.aqua.database.DailyIntake
import com.whomentors.aqua.database.StatsDatabaseDao
import kotlinx.coroutines.launch

/**
 * A view model to save info about users.
 */
class UserInfoViewModel(
    val database: StatsDatabaseDao,
    application: Application
): AndroidViewModel(application) {
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

    private suspend fun update(todayIntake: DailyIntake){
        database.update(todayIntake)
    }
}