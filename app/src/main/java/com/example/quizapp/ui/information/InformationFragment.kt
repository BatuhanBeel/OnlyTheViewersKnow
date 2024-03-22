package com.example.quizapp.ui.information

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.quizapp.R
import com.example.quizapp.databinding.FragmentInformationBinding
import com.example.quizapp.util.downloadFromUrl
import com.example.quizapp.util.placeholderProgressBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InformationFragment() : Fragment(R.layout.fragment_information) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: InformationFragmentArgs by navArgs()
        val seriesData = args.seriesAndResultData
        val binding = FragmentInformationBinding.bind(view)

        binding.apply {
            titleTextView.text = seriesData.series.title
            imdbInputTextView.text = seriesData.series.imdbRating
            yearTextView.text = seriesData.series.year
            posterImageView.downloadFromUrl(seriesData.series.poster, placeholderProgressBar(requireContext()))
            genreTextView.text = seriesData.series.genre
            plotTextView.text = seriesData.series.plot
            if (seriesData.series.totalSeasons != null){
                runTimeTextView.text = seriesData.series.totalSeasons
            }
            else{
                runTimeTextView.text = seriesData.series.runtime
            }
            if (seriesData.userResults.maxLevel != 0 ){
                startQuizButton.text = "Continue Quiz"
            }
            startQuizButton.setOnClickListener {
                val action =
                    InformationFragmentDirections.actionInformationFragmentToLevelFragment(
                        seriesData
                    )
                findNavController().navigate(action)
            }
        }
    }
}