package com.example.mystoryapplication.view.add_story

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.mystoryapplication.data.UserRepository
import com.example.mystoryapplication.data.response.LoginResult

class AddStoryViewModel(private val repository: UserRepository) : ViewModel() {
    fun getSession(): LiveData<LoginResult> {
        return repository.getSession().asLiveData()
    }
}