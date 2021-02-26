package com.whomentors.aqua.screens.stats

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.whomentors.aqua.database.StatsDatabaseDao
import com.whomentors.aqua.screens.waterIntake.MainViewModel

class StatsViewModelFactory(private val database: StatsDatabaseDao,
                            private val application: Application
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatsViewModel::class.java)) {
            return StatsViewModel(database, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}