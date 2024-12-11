package com.capstone.plantcare.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.plantcare.data.remote.response.HistoryResponseItem
import com.capstone.plantcare.databinding.CardRecentPlantBinding

class RecentAdapter(private val onItemClickListener: (HistoryResponseItem) -> Unit) : ListAdapter<HistoryResponseItem, RecentAdapter.MyViewHolder>(DIFF_CALLBACK){
    class MyViewHolder (private val binding: CardRecentPlantBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(history: HistoryResponseItem, onItemClickListener: (HistoryResponseItem) -> Unit) {
            Glide.with(binding.root.context)
                .load(history.imageUrl)
                .into(binding.ivPhoto)
            binding.tvName.text = "${history.disease}"

            binding.root.setOnClickListener{
                onItemClickListener(history)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CardRecentPlantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val history = getItem(position)
        holder.bind(history, onItemClickListener)
    }


    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HistoryResponseItem>() {
            override fun areItemsTheSame(
                oldItem: HistoryResponseItem,
                newItem: HistoryResponseItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: HistoryResponseItem,
                newItem: HistoryResponseItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }


}