package com.example.mystoryapplication.view.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coba1submission.ui.adapter.Adapter
import com.example.mystoryapplication.R
import com.example.mystoryapplication.databinding.ActivityListStoryBinding
import com.example.mystoryapplication.view.ViewModelFactory
import com.example.mystoryapplication.view.add_story.AddStoryActivity

class ListStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListStoryBinding
    private val viewModel by viewModels<ListStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_SecondActivity)
        binding = ActivityListStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this@ListStoryActivity)
        binding.rvStory.layoutManager = layoutManager

        viewModel.getSession().observe(this){ token ->
            viewModel.getStoryModel(token.token)
            viewModel.listEvents.observe(this) { data ->
                val adapter = Adapter()
                adapter.submitList(data)
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
            R.id.action_search -> {
                val intent = Intent(this, AddStoryActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}