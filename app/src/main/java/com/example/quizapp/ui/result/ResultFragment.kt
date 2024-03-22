package com.example.quizapp.ui.result

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.quizapp.R
import com.example.quizapp.databinding.FragmentResultBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultFragment:Fragment(R.layout.fragment_result) {

    private val args: ResultFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentResultBinding.bind(view)
        val trueCount = args.trueCount

        binding.apply {
            if (args.trueCount != 0){
                resultSuccessTextView.text = "Congrats!"
                resultSuccessTextView.setTextColor(ContextCompat.getColor(requireContext(),R.color.level_beginner_color))
                resultSuccessImageView.setImageResource(R.drawable.award)
                resultMessage.text = "You answered $trueCount out of 5 questions correctly."
                resultFirstStar.setImageResource(R.drawable.ic_yellow_star_filled)
                if (2 < trueCount){
                    resultSecondStar.setImageResource(R.drawable.ic_yellow_star_filled)
                }
                if (4 < trueCount){
                    resultThirdStar.setImageResource(R.drawable.ic_yellow_star_filled)
                }
            }

            retryButton.setOnClickListener {
                val action = ResultFragmentDirections.actionResultFragmentToQuizFragment(
                    args.questionList,
                    args.level,
                    args.seriesTitle,
                    args.seriesAndResultData
                )
                findNavController().navigate(action)
            }
            returnButton.setOnClickListener {
                val action = ResultFragmentDirections.actionResultFragmentToLevelFragment(
                    args.seriesAndResultData
                )
                findNavController().navigate(action)
            }
        }
    }
}
