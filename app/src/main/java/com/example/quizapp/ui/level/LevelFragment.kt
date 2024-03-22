package com.example.quizapp.ui.level

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.quizapp.R
import com.example.quizapp.databinding.FragmentLevelsBinding
import com.example.quizapp.model.SeriesAndResults
import com.example.quizapp.repository.allQuestions
import com.example.quizapp.util.downloadFromUrl
import com.example.quizapp.util.placeholderProgressBar
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "LevelFragment"

@AndroidEntryPoint
class LevelFragment(): Fragment(R.layout.fragment_levels), LevelAdapter.OnItemClickListener {

    private lateinit var seriesData: SeriesAndResults
    private val args: LevelFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        seriesData = args.seriesAndResultData
        val binding = FragmentLevelsBinding.bind(view)
        val adapter = LevelAdapter(this, seriesData.userResults)

        binding.apply {
            levelRecyclerView.adapter = adapter
            levelsTitleTextView.text = seriesData.series.title
            levelPosterImageView.downloadFromUrl(
                seriesData.series.poster,
                placeholderProgressBar(requireContext())
            )
        }
    }

    override fun onItemClick(position: Int) {
        val hashMap = allQuestions[seriesData.series.title]
        if (!hashMap.isNullOrEmpty()){
            val list = hashMap.values.toList()[position]
            val action = LevelFragmentDirections.actionLevelFragmentToQuizFragment(list.toTypedArray(),position+1, seriesData.series.title, seriesData)
            findNavController().navigate(action)
        }
    }
}