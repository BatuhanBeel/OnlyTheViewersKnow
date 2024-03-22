package com.example.quizapp.ui.level

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.quizapp.R
import com.example.quizapp.databinding.FragmentLevelsItemBinding
import com.example.quizapp.model.SeriesLevels
import kotlin.math.max

class LevelAdapter(private val listener: OnItemClickListener, private val item: SeriesLevels) :
    RecyclerView.Adapter<LevelAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            FragmentLevelsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(item, position + 1)
    }

    override fun getItemCount(): Int {
        return 9
    }

    inner class ItemViewHolder(private val binding: FragmentLevelsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                levelLayout.setOnClickListener {
                    Log.d("deneme", "tıklandı1")
                }
                root.setOnClickListener {
                    Log.d("deneme", "tıklandı")
                    val position = adapterPosition
                    listener.onItemClick(position)
                }
            }
        }

        fun bind(item: SeriesLevels, level: Int) {
            val maxLevel = item.maxLevel
            when (level) {
                1 -> if (0 < maxLevel + 1) setItemSettings(item.level1, 1)
                2 -> if (1 < maxLevel + 1) setItemSettings(item.level2, 2)
                3 -> if (2 < maxLevel + 1) setItemSettings(item.level3, 3)
                4 -> if (3 < maxLevel + 1) setItemSettings(item.level4, 4)
                5 -> if (4 < maxLevel + 1) setItemSettings(item.level5, 5)
                6 -> if (5 < maxLevel + 1) setItemSettings(item.level6, 6)
                7 -> if (6 < maxLevel + 1) setItemSettings(item.level7, 7)
                8 -> if (7 < maxLevel + 1) setItemSettings(item.level8, 8)
                9 -> if (8 < maxLevel + 1) setItemSettings(item.level9, 9)
            }
        }

        private fun setItemSettings(trueCount: Int, level: Int) {
            binding.starsLinearLayout.visibility = View.VISIBLE
            binding.levelItemButton.isClickable = false
            binding.levelItemButton.foreground = null
            binding.levelItemButton.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.primary
                )
            )
            binding.levelItemButton.text = "$level"
            if (trueCount > 0) {
                binding.firstStarImageView.setImageResource(R.drawable.ic_yellow_star_filled)
            }
            if (trueCount > 2) {
                binding.secondStarImageView.setImageResource(R.drawable.ic_yellow_star_filled)
            }
            if (trueCount > 4) {
                binding.thirdStarImageView.setImageResource(R.drawable.ic_yellow_star_filled)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}