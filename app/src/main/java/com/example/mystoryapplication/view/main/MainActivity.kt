package com.example.mystoryapplication.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.mystoryapplication.R
import com.example.mystoryapplication.view.home.ListStoryActivity
import com.example.mystoryapplication.view.login.LoginActivity
import com.example.mystoryapplication.view.ViewModelFactory

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.getSession().observe(this) { user ->
            Log.d("printToken", user.token)
            if (user.token != "") {
                Log.d("tokenFounded", user.token)
                startActivity(Intent(this, ListStoryActivity::class.java))
                finish()
            }
        }

        Log.d("tokenUnFounded", "tidak ada token yang ditemukan")
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}