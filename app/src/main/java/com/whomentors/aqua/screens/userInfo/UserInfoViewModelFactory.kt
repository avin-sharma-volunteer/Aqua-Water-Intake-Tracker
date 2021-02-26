package com.whomentors.aqua.screens.userInfo

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.whomentors.aqua.database.StatsDatabaseDao

class UserInfoViewModelFactory(private val database: StatsDatabaseDao,
                           private val application: Application
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserInfoViewModel::class.java)) {
            return UserInfoViewModel(database, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}