package com.example.mystoryapplication.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mystoryapplication.data.UserRepository
import com.example.mystoryapplication.data.api.ApiConfig
import com.example.mystoryapplication.data.response.ListStoryItem
import com.example.mystoryapplication.data.response.LoginResult
import com.example.mystoryapplication.data.response.StoryResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListStoryViewModel(private val repository: UserRepository): ViewModel() {
    private val _storyResponse = MutableLiveData<StoryResponse>()

    private val _listStoryItem = MutableLiveData<List<ListStoryItem?>?>()
    val listEvents: LiveData<List<ListStoryItem?>?> = _listStoryItem

//    var loginResult: LiveData<LoginResult>

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getSession(): LiveData<LoginResult> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
    fun getStoryModel(token: String) {
        _isLoading.value = true
//        val oldToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXpXeFYxdU44UEhBRjc1QTEiLCJpYXQiOjE3MzA5MjcyODZ9.iBbcIlCC4U5oV7kT4BsMJ9LZnvPz2U1vMZ714yx0O2A"

        val client = ApiConfig.getApiService(token).getAllStories()
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(
                call: Call<StoryResponse>,
                response: Response<StoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    response.body()?.let {
                        _storyResponse.value = it
                        _listStoryItem.value = it.listStory
                    }
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _isLoading.value = false
            }
        })
    }
}