package com.example.quizapp.ui.quiz

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.quizapp.R
import com.example.quizapp.databinding.FragmentQuizBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuizFragment: Fragment(R.layout.fragment_quiz) {

    private val viewModel: QuizViewModel by viewModels()
    private lateinit var binding: FragmentQuizBinding
    private val args: QuizFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentQuizBinding.bind(view)
        viewModel.questionList = args.questionList.toList()
        setQuestion()

        binding.apply {
            quizLevel.text = "Level ${args.level}"
            quizOptionA.setOnClickListener {
                val btn = it as Button
                setClickableOptions(false)
                optionSelected(btn)
            }
            quizOptionB.setOnClickListener {
                val btn = it as Button
                setClickableOptions(false)
                optionSelected(btn)
            }
            quizOptionC.setOnClickListener {
                val btn = it as Button
                setClickableOptions(false)
                optionSelected(btn)
            }
            quizOptionD.setOnClickListener {
                val btn = it as Button
                setClickableOptions(false)
                optionSelected(btn)
            }
            quizNextButton.setOnClickListener {
                resetUi()
                viewModel.increaseCurrentQuestion()
                setQuestion()
            }
            quizFinishButton.setOnClickListener {
                viewModel.finishButtonClicked(args.level, args.seriesAndResultData)
                val action = QuizFragmentDirections.actionQuizFragmentToResultFragment(
                    viewModel.trueCount,
                    args.questionList,
                    args.level,
                    args.seriesTitle,
                    args.seriesAndResultData
                )
                findNavController().navigate(action)
            }
        }
    }

    private fun setQuestion(){
        val levelQuestion = viewModel.questionList[viewModel.currentQuestion-1]
        binding.apply {
            quizQuestion.text = levelQuestion.question
            quizOptionA.text = levelQuestion.choiceA
            quizOptionB.text = levelQuestion.choiceB
            quizOptionC.text = levelQuestion.choiceC
            quizOptionD.text = levelQuestion.choiceD
            quizQuestionCount.text = viewModel.currentQuestion.toString()
            quizProgressBar.setProgress(viewModel.currentQuestion, true)
        }
    }

    private fun resetUi(){
        binding.apply {
            quizOptionA.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.colorBackground))
            quizOptionB.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.colorBackground))
            quizOptionC.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.colorBackground))
            quizOptionD.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.colorBackground))
            quizNextButton.visibility = View.INVISIBLE
            setClickableOptions(true)
        }
    }

    private fun optionSelected(selectedButton: Button){
        if (viewModel.currentQuestion != viewModel.questionList.size){
            binding.quizNextButton.visibility = View.VISIBLE
        }
        else{
            binding.quizFinishButton.visibility = View.VISIBLE
        }
        val isTrue = viewModel.checkAnswer(selectedButton.text.toString())
        if (isTrue){
            selectedButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.true_answer))
        }
        else{
            selectedButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.false_answer))
            when(viewModel.questionList[viewModel.currentQuestion-1].trueChoice){
                binding.quizOptionA.text.toString() ->{
                    binding.quizOptionA.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.true_answer))
                }
                binding.quizOptionB.text.toString() ->{
                    binding.quizOptionB.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.true_answer))
                }
                binding.quizOptionC.text.toString() ->{
                    binding.quizOptionC.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.true_answer))
                }
                binding.quizOptionD.text.toString() ->{
                    binding.quizOptionD.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.true_answer))
                }
            }
        }
    }

    private fun setClickableOptions(clickable: Boolean){
        binding.apply {
            quizOptionA.isClickable = clickable
            quizOptionB.isClickable = clickable
            quizOptionC.isClickable = clickable
            quizOptionD.isClickable = clickable
        }
    }
}