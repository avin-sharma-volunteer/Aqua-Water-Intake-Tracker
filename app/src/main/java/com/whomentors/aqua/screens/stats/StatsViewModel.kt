package com.whomentors.aqua.screens.stats

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.whomentors.aqua.database.DailyIntake
import com.whomentors.aqua.database.StatsDatabaseDao
import kotlinx.coroutines.launch

/**
 * ViewModel that stores daily water intake
 * entries
 */
class StatsViewModel(
    val database: StatsDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private lateinit var allEntries: LiveData<List<DailyIntake>>
    init {
        initializeAllEntries()
    }

    private fun initializeAllEntries(){
        viewModelScope.launch {
            allEntries = database.getAllIntakes()
        }
    }

    fun getAllEntries(): LiveData<List<DailyIntake>> {
        return allEntries
    }
}