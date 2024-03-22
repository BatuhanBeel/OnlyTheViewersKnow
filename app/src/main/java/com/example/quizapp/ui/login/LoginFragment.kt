package com.example.quizapp.ui.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.quizapp.R
import com.example.quizapp.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "LoginFragment"

@AndroidEntryPoint
class LoginFragment: Fragment(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentLoginBinding.bind(view)

        binding.apply {
            registerLinearlayout.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
            }
            loginButton.setOnClickListener {
                viewModel.clickLoginButton(
                    email.text.toString(),
                    password.text.toString()
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                if (uiState.isSuccessfullyLogin){
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSeriesFragment())
                }
                uiState.userMessage?.let { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    viewModel.userMessageShown()
                }
            }
        }
    }
}