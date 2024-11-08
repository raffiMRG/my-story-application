package com.example.mystoryapplication.view.detail

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.mystoryapplication.R
import com.example.mystoryapplication.data.response.ListStoryItem
import com.example.mystoryapplication.databinding.ActivityDetailBinding
import com.example.mystoryapplication.databinding.ItemRowBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_SecondActivity)
        setContentView(binding.root)

        val data = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("key_story", ListStoryItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("key_story")
        }

        Glide.with(binding.root)
            .load(data?.photoUrl)
            .into(binding.ivDetailPhoto)

        binding.apply {
            tvDetailName.text = getString(R.string.from_text, data?.name ?: "Unknown")
            tvDetailDescription.text = data?.description
        }
    }
}