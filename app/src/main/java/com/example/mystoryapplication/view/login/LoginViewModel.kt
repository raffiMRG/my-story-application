package com.example.mystoryapplication.view.login

import android.util.Log
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
//                    val responseBody = response.body()
                    // Tangani response sukses di sini
//                    token = responseBody?.loginResult?.token.toString()
//                    responseBody?.let { showToast(it.message) }
                    response.body()?.let {
                        _loginResponse.value = it
                        _loginResult.value = it.loginResult
                        Log.d("LOGIN_SUCCESS", it.loginResult.token)
                    }
                } else {
                    val responseBody = response.body()
                    // Tangani error di sini
                    Log.d("errorViewModel", "error in view model line 55")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // Tangani kegagalan request di sini
            }
        })
    }
}