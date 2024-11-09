package com.example.mystoryapplication.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mystoryapplication.data.UserRepository
import com.example.mystoryapplication.data.api.ApiConfig
import com.example.mystoryapplication.data.request.LoginRequest
import com.example.mystoryapplication.data.response.LoginResponse
import com.example.mystoryapplication.data.response.LoginResult
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> = _loginResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isFailure = MutableLiveData<Boolean>()
    val isFailure: LiveData<Boolean> = _isFailure

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult


    fun saveSession(user: LoginResult) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun getSession(): LiveData<LoginResult> {
        return repository.getSession().asLiveData()
    }

    fun tryLogin(email: String, password: String){
        _isLoading.value = true
        _isFailure.value = false
        val apiService = ApiConfig.getApiService()
        val userRequest = LoginRequest(
            email = email,
            password = password
        )

        apiService.userLogin(userRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    _isFailure.value = false
                    response.body()?.let {
                        _loginResponse.value = it
                        _loginResult.value = it.loginResult
                    }
                } else {
                    _isFailure.value = true
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
            }
        })
    }
}