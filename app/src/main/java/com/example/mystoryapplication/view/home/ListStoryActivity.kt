package com.example.mystoryapplication.view.home

import android.content.Intent
import android.media.session.MediaSession.Token
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mystoryapplication.view.adapter.Adapter
import com.example.mystoryapplication.R
import com.example.mystoryapplication.databinding.ActivityListStoryBinding
import com.example.mystoryapplication.view.ViewModelFactory
import com.example.mystoryapplication.view.add_story.AddStoryActivity
import com.example.mystoryapplication.view.login.LoginActivity
import com.example.mystoryapplication.view.main.MainActivity

class ListStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListStoryBinding
    private val viewModel by viewModels<ListStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        title = "List Story"
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_SecondActivity)
        binding = ActivityListStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this@ListStoryActivity)
        binding.rvStory.layoutManager = layoutManager

        viewModel.getSession().observe(this){ data ->
            viewModel.getStoryModel(data.token)
            viewModel.listEvents.observe(this) { story ->
                val adapter = Adapter()
                adapter.submitList(story)
                binding.rvStory.adapter = adapter
            }
        }

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                val intent = Intent(this, AddStoryActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_logout -> {
                try {
                    viewModel.logout()
                    Log.d("LOGOUT", "logout")
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                    // Menutup HomeActivity
                    finish()
                }catch (e: Exception){
                    Log.d("ErrorLogout", "onOptionsItemSelected: ")
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}