package com.capstone.plantcare.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.plantcare.data.remote.response.NewsItem
import com.capstone.plantcare.databinding.CardNewsBinding

class NewsAdapter : ListAdapter<NewsItem, NewsAdapter.MyViewHolder>(DIFF_CALLBACK){
    class MyViewHolder(private val binding: CardNewsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(news: NewsItem) {
            Glide.with(binding.root.context)
                .load(news.image)
                .into(binding.ivPhoto)
            binding.tvName.text = "${news.source}"
            binding.tvTitle.text = "${news.title}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CardNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val news = getItem(position)
        holder.bind(news)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NewsItem>() {
            override fun areItemsTheSame(oldItem: NewsItem, newItem: NewsItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: NewsItem, newItem: NewsItem): Boolean {
                return oldItem == newItem
            }

        }
    }
}