package com.example.mystoryapplication.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.mystoryapplication.data.UserRepository
import com.example.mystoryapplication.data.response.LoginResult

class MainViewModel(private val repository: UserRepository) : ViewModel() {
    fun getSession(): LiveData<LoginResult> {
        return repository.getSession().asLiveData()
    }

}