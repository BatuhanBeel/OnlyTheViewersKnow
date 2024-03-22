package com.example.quizapp.ui.series

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quizapp.R
import com.example.quizapp.databinding.FragmentSeriesItemBinding
import com.example.quizapp.model.SeriesAndResults
import com.example.quizapp.util.downloadFromUrl
import com.example.quizapp.util.placeholderProgressBar

class SeriesAdapter(private val listener: OnItemClickListener) :
    ListAdapter<SeriesAndResults, SeriesAdapter.ViewHolder>(DiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FragmentSeriesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class ViewHolder(private val binding: FragmentSeriesItemBinding): RecyclerView.ViewHolder(binding.root){

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    val item = getItem(position)
                    listener.onItemClick(item)
                }
            }
        }

        fun bind(item: SeriesAndResults){
            binding.apply {
                seriesImage.downloadFromUrl(item.series.poster, placeholderProgressBar(binding.root.context))
                levelBackground.visibility = View.GONE
                levelText.visibility = View.GONE
                val level = item.userResults.maxLevel
                if (level != 0){
                    levelText.text = level.toString()
                    levelBackground.visibility = View.VISIBLE
                    levelText.visibility = View.VISIBLE
                }
                when(level){
                    in 1..2 ->{
                        levelBackground.setCardBackgroundColor(ContextCompat.getColor(root.context,R.color.level_beginner_color))
                    }
                    in 3..4 ->{
                        levelBackground.setCardBackgroundColor(ContextCompat.getColor(root.context,R.color.level_intermediate_color))
                    }
                    in 5..10 ->{
                        levelBackground.setCardBackgroundColor(ContextCompat.getColor(root.context,R.color.level_hard_color))
                    }
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: SeriesAndResults)
    }

    private class DiffCallback : DiffUtil.ItemCallback<SeriesAndResults>() {
        override fun areItemsTheSame(oldItem: SeriesAndResults, newItem: SeriesAndResults): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: SeriesAndResults, newItem: SeriesAndResults): Boolean {
            return oldItem == newItem
        }
    }
}

