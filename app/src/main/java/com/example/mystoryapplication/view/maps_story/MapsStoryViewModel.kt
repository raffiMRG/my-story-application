package com.example.mystoryapplication.view.maps_story

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

class MapsStoryViewModel(private val repository: UserRepository): ViewModel() {
    private val _storyResponse = MutableLiveData<StoryResponse>()

    private val _listStoryItem = MutableLiveData<List<ListStoryItem?>?>()
    val listEvents: LiveData<List<ListStoryItem?>?> = _listStoryItem

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

        val client = ApiConfig.getApiService(token).getAllStoriesAndLocation(LOCATION)
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
                        _isLoading.value = false
                    }
                }else{
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _isLoading.value = false
            }
        })
    }

    companion object{
        const val LOCATION = "1"
    }
}