package com.example.mystoryapplication.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mystoryapplication.data.Repository
import com.example.mystoryapplication.data.api.ApiConfig
import com.example.mystoryapplication.data.response.ListStoryItem
import com.example.mystoryapplication.data.response.LoginResult
import com.example.mystoryapplication.data.response.StoryResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class ListStoryViewModel(private val repository: Repository): ViewModel() {
    private val _storyResponse = MutableLiveData<StoryResponse>()

    private val _listStoryItem = MutableLiveData<List<ListStoryItem?>?>()
    val listEvents: LiveData<List<ListStoryItem?>?> = _listStoryItem

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> = _token

    fun getSession(): LiveData<LoginResult> {
        return repository.getSession().asLiveData()
    }

//    init {
//        _token.value = getSession().toString()
//    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    private val _quote = MutableLiveData<List<ListStoryItem>>()
    //    var quote: LiveData<List<QuoteResponseItem>> = _quote

//    val quote: LiveData<PagingData<ListStoryItem>> =
//        repository.getQuote("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXpXeFYxdU44UEhBRjc1QTEiLCJpYXQiOjE3MzA4MDc0ODl9.kD2FyCfu11tOawMF4IL3E8RyYQEob56GxL0GN24335M").cachedIn(viewModelScope)

    fun getStoryPaginModel(token: String): LiveData<PagingData<ListStoryItem>>{
        return repository.getQuote(token).cachedIn(viewModelScope)
    }

//    fun quote(token: String): LiveData<PagingData<StoryResponse>> {
//        return repository.getQuote(token).cachedIn(viewModelScope)
//    }

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
}
