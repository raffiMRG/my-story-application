package com.example.mystoryapplication.view.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mystoryapplication.view.adapter.Adapter
import com.example.mystoryapplication.R
import com.example.mystoryapplication.databinding.ActivityListStoryBinding
import com.example.mystoryapplication.view.ViewModelFactory
import com.example.mystoryapplication.view.adapter.LoadingStateAdapter
import com.example.mystoryapplication.view.adapter.QuoteListAdapter
import com.example.mystoryapplication.view.add_story.AddStoryActivity
import com.example.mystoryapplication.view.login.LoginViewModel
import com.example.mystoryapplication.view.main.MainActivity
import com.example.mystoryapplication.view.maps_story.MapsStoryActivity

class ListStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListStoryBinding
//    private val viewModel: ListStoryViewModel by viewModels {
//        ViewModelFactory(this)
//    }

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

        viewModel.isLoading.observe(this){isLoading ->
            showLoading(isLoading)
        }

//        viewModel.getSession().observe(this){
//            Log.d("ThisIsListToken", it.token)
//        }

        viewModel.getSession().observe(this){responseData ->
            getData(responseData.token)
        }

//        getData()
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
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                    finish()
                }catch (e: Exception){
                    showToast(e.message.toString())
                }
                true
            }
            R.id.action_map -> {
                val intent = Intent(this, MapsStoryActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun getData(token: String) {
        val adapter = QuoteListAdapter()
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter{
                adapter.retry()
            }
        )

        viewModel.getStoryPaginModel(token).observe(this){
//        viewModel.quote.observe(this, {
//            Log.d("jumlahItem", adapter.itemCount.toString())
            adapter.submitData(lifecycle, it)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}