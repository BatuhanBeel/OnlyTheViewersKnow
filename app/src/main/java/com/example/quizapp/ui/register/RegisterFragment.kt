package com.example.quizapp.ui.register

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.quizapp.R
import com.example.quizapp.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "RegisterFragment"

@AndroidEntryPoint
class RegisterFragment: Fragment(R.layout.fragment_register) {

    private val viewModel: RegisterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentRegisterBinding.bind(view)

        binding.apply {
            registerButton.setOnClickListener {
                viewModel.checkUserInputs(
                    binding.email.text.toString(),
                    binding.password.text.toString()
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                if (uiState.isSuccessfullyRegistered){
                    Toast.makeText(requireContext().applicationContext,"User created successfully.",Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                else if (uiState.isTrueInputEntered){
                    viewModel.addUserToFirebase(
                        binding.email.text.toString(),
                        binding.password.text.toString(),
                    )
                }
                uiState.userMessage?.let { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    viewModel.userMessageShown()
                }
            }
        }
    }
}