package com.dicoding.picodiploma.loginwithanimation.di

import android.content.Context
import com.example.mystoryapplication.data.UserRepository
import com.example.mystoryapplication.data.pref.UserPreference
import com.example.mystoryapplication.data.pref.dataStore

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}