package com.example.mystoryapplication.view.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mystoryapplication.data.UserRepository
import com.example.mystoryapplication.data.api.ApiConfig
import com.example.mystoryapplication.data.request.RegisterRequest
import com.example.mystoryapplication.data.response.BasicResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(private val repository: UserRepository): ViewModel() {
    private val _regisResponse = MutableLiveData<BasicResponse?>()
    val regisResponse: LiveData<BasicResponse?> = _regisResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isFailure = MutableLiveData<Boolean>()
    val isFailure: LiveData<Boolean> = _isFailure

    fun tryRegis(username: String, email: String, password: String){
        _isLoading.value = true
        _isFailure.value = false
        val apiService = ApiConfig.getApiService()
        val userRequest = RegisterRequest(
            name = username,
            email = email,
            password = password
        )

        apiService.createUser(userRequest).enqueue(object : Callback<BasicResponse> {
            override fun onResponse(
                call: Call<BasicResponse>,
                response: Response<BasicResponse>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    _isFailure.value = false
                    response.body()?.let {
                        _regisResponse.value = it
                    }
                } else {
                    _isFailure.value = true
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
            }
        })
    }

}