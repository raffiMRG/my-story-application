package com.example.mystoryapplication.data.di

import android.content.Context
import com.example.mystoryapplication.data.Repository
import com.example.mystoryapplication.data.pref.UserPreference
import com.example.mystoryapplication.data.pref.dataStore

object Injection {
    fun provideRepository(context: Context): Repository {
        val pref = UserPreference.getInstance(context.dataStore)
        return Repository.getInstance(pref)
    }
}