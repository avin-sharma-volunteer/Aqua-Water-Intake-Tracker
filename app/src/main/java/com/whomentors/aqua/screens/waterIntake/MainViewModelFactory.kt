package com.whomentors.aqua.screens.waterIntake

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.whomentors.aqua.database.DailyIntake
import com.whomentors.aqua.database.StatsDatabaseDao

class MainViewModelFactory(private val database: StatsDatabaseDao,
                           private val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(database, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}