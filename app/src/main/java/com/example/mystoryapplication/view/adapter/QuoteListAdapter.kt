package com.example.mystoryapplication.view.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mystoryapplication.data.response.ListStoryItem
import com.example.mystoryapplication.data.response.StoryResponse
import com.example.mystoryapplication.databinding.ItemRowBinding
import com.example.mystoryapplication.view.detail.DetailActivity

class QuoteListAdapter :
//    ListAdapter<QuoteResponseItem, QuoteListAdapter.MyViewHolder>(DIFF_CALLBACK) {
    PagingDataAdapter<ListStoryItem, QuoteListAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val review = getItem(position)
        review?.let { holder.bind(it) }
    }
    class MyViewHolder(private val binding: ItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(lisStoryItem: ListStoryItem){
            binding.tvItemName.text = lisStoryItem.name
            Glide.with(binding.root)
                .load(lisStoryItem.photoUrl)
                .into(binding.ivItemPhoto)
            this.itemView.setOnClickListener{
                val intentDetail = Intent(this.itemView.context, DetailActivity::class.java)
                intentDetail.putExtra("key_story", lisStoryItem)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.ivItemPhoto, "img_story"),
                        Pair(binding.tvItemName, "title_story")
                    )
                itemView.context.startActivity(intentDetail, optionsCompat.toBundle())
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}