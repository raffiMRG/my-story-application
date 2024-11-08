package com.example.mystoryapplication.view.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.mystoryapplication.databinding.ActivityLoginBinding
import com.example.mystoryapplication.data.response.LoginResult
import com.example.mystoryapplication.view.ViewModelFactory
import com.example.mystoryapplication.view.home.ListStoryActivity
import com.example.mystoryapplication.view.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
//    private lateinit var textjudul: TextView
private lateinit var binding: ActivityLoginBinding
private lateinit var token: String
private val viewModel by viewModels<LoginViewModel> {
    ViewModelFactory.getInstance(this)
}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener{ goToRegister() }

        binding.btnLogin.setOnClickListener{ login() }

        viewModel.loginResult.observe(this){
            Log.d("viewModelObserve", "masuk ke view model observe")
            setupAction(it)
        }

        viewModel.getSession().observe(this) { user ->
            Log.d("printToken", user.token)
            if (user.token != "") {
                Log.d("tokenFounded", user.token)
                startActivity(Intent(this, ListStoryActivity::class.java))
                finish()
            }else{
                Log.d("printToken", "tidak ada token yang ditemukan")
            }
        }

        Log.d("tokenUnFounded", "tidak ada token yang ditemukan")
    }

    private fun setupAction(loginResult : LoginResult) {
        Log.d("setupAction", loginResult.token)
        viewModel.saveSession(loginResult)
        startActivity(Intent(this, ListStoryActivity::class.java))
//        AlertDialog.Builder(this).apply {
//            setTitle("Yeah!")
//            val name = loginResult.name
//            setMessage("Akun dengan nama $name sudah jadi nih. Yuk, login dan belajar coding.")
//            setPositiveButton("Lanjut") { _, _ ->
//                finish()
//            }
//            create()
//            show()
//        }
    }

    private fun goToRegister(){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun login(){
        val email = binding.edLoginEmail.editText?.text.toString()
        val password = binding.edLoginPassword.editText?.text.toString()
        if (email == ""){
            Log.d("InputRegister", "username kosong")
        }else{
            viewModel.tryLogin(email, password)
        }
    }

//    musculin status succes / error dari model nanti
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}