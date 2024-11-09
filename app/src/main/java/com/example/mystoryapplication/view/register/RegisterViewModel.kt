package com.example.mystoryapplication.view.register

import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mystoryapplication.data.UserRepository
import com.example.mystoryapplication.data.api.ApiConfig
import com.example.mystoryapplication.data.request.LoginRequest
import com.example.mystoryapplication.data.request.RegisterRequest
import com.example.mystoryapplication.data.response.BasicResponse
import com.example.mystoryapplication.data.response.LoginResponse
import com.example.mystoryapplication.data.response.LoginResult
import com.example.mystoryapplication.view.login.LoginActivity
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
//                        Log.d("LOGIN_SUCCESS", it.loginResult.token)

//                    val responseBody = response.body()
//                    // Tangani response sukses di sini
//                    responseBody?.let {
//                        AlertDialog.Builder(this@RegisterActivity).apply {
//                            setTitle("Success!")
//                            setMessage("Selamat Akun Anda Sudah Berhasil Di Buat, Silahkan Login Dengan Akun Tersebut")
//                            setPositiveButton("Lanjut") { _, _ ->
//                                val intent = Intent(context, LoginActivity::class.java)
//                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                                startActivity(intent)
//                                finish()
//                            }
//                            create()
//                            show()
//                        }
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