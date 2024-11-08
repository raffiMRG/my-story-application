package com.example.mystoryapplication.view.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mystoryapplication.databinding.ActivityRegisterBinding
import com.example.mystoryapplication.data.api.ApiConfig
import com.example.mystoryapplication.data.request.RegisterRequest
import com.example.mystoryapplication.data.response.BasicResponse
import com.example.mystoryapplication.view.login.LoginActivity
import retrofit2.Response
import retrofit2.Call
import retrofit2.Callback

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener{ goToLogin() }
        binding.btnRegister.setOnClickListener{ register() }
    }

    private fun goToLogin(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun register(){
        val username = binding.edRegisterName.editText?.text.toString()
        val email = binding.edRegisterEmail.editText?.text.toString()
        val password = binding.edRegisterPassword.editText?.text.toString()
        if (username == ""){
            Log.d("InputRegister", "username kosong")
        }else{
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
                        val responseBody = response.body()
                        // Tangani response sukses di sini
                        responseBody?.let { showToast(it.message) }
                    } else {
                        val responseBody = response.body()
                        // Tangani error di sini
                        responseBody?.let { showToast(it.message) }
                    }
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                    // Tangani kegagalan request di sini
                    showToast("Call Failure")
                }
            })
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}