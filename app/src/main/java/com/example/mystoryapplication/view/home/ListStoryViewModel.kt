package com.example.mystoryapplication.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.mystoryapplication.data.UserRepository
import com.example.mystoryapplication.data.api.ApiConfig
import com.example.mystoryapplication.data.response.ListStoryItem
import com.example.mystoryapplication.data.response.LoginResult
import com.example.mystoryapplication.data.response.StoryResponse
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

    fun getStoryModel(token: String) {
        _isLoading.value = true

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
