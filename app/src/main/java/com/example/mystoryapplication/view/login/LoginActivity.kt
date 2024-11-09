package com.example.mystoryapplication.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.mystoryapplication.databinding.ActivityLoginBinding
import com.example.mystoryapplication.data.response.LoginResult
import com.example.mystoryapplication.view.ViewModelFactory
import com.example.mystoryapplication.view.home.ListStoryActivity
import com.example.mystoryapplication.view.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener{ login() }

        binding.btnRegister.setOnClickListener{ goToRegister() }

        viewModel.isLoading.observe(this@LoginActivity){
            showLoading(it)
        }

        viewModel.getSession().observe(this) { user ->
            Log.d("printToken", user.token)
            if (user.token != "") {
                Log.d("tokenFounded", user.token)

                val intent = Intent(this, ListStoryActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

                finish()
            }else{
                Log.d("printToken", "tidak ada token yang ditemukan")
            }
        }

        Log.d("tokenUnFounded", "tidak ada token yang ditemukan")
        playAnimation()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditText =
            ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(200)
        val passwordEditText =
            ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(200)
        val btnSignIn =
            ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(200)
        val btnSignUp =
            ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(200)

        AnimatorSet().apply {
            playSequentially(
                title,
                emailEditText,
                passwordEditText,
                btnSignIn,
                btnSignUp
            )
            startDelay = 200
        }.start()
    }

    private fun setupAction(loginResult : LoginResult) {
        Log.d("setupAction", loginResult.token)
        viewModel.saveSession(loginResult)
        startActivity(Intent(this, ListStoryActivity::class.java))
    }

    private fun goToRegister(){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun login(){
        showLoading(true)
        val email = binding.edLoginEmail.editText?.text.toString()
        val password = binding.edLoginPassword.editText?.text.toString()
        if (email == "" || password == ""){
            AlertDialog.Builder(this).apply {
            setTitle("Alert!!")
            setMessage("Username Atau Password Kosong")
            setPositiveButton("Ok") { _, _ ->
                showLoading(false)
            }
            create()
            show()
        }
        }else{
            viewModel.tryLogin(email, password)
            viewModel.loginResponse.observe(this){ response ->
                if (!response.error){
//                    Log.d("ErrorResponse", "this is error!!!!")
                    viewModel.loginResult.observe(this){
                        Log.d("viewModelObserve", "masuk ke view model observe")
                        setupAction(it)
                    }
                }
            }
            viewModel.isFailure.observe(this){
                if (it){
//                Log.d("ErrorResponse", "its fine ghencana")
                    AlertDialog.Builder(this).apply {
                        setTitle("Alert!!")
                        setMessage("Username Atau Password Tidak Valid")
                        setPositiveButton("Ok") { _, _ ->
                            showLoading(false)
                        }
                        create()
                        show()
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean){
        if (isLoading){
            binding.loading.visibility = View.VISIBLE
        }else{
            binding.loading.visibility = View.INVISIBLE
        }
    }
}