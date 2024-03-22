package com.example.quizapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.FirebaseService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "LoginViewModel"

data class LoginUiState(
    val isSuccessfullyLogin: Boolean = false,
    val userMessage: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseService: FirebaseService
): ViewModel(){

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState

    fun clickLoginButton(email: String, password: String){
        if(email.isNotEmpty() && password.isNotEmpty()){
            viewModelScope.launch(Dispatchers.IO) {
                val result = firebaseService.firebaseUserSignIn(email, password)
                if (result.isSuccessful){
                    _uiState.update { currentUiState ->
                        currentUiState.copy(
                            isSuccessfullyLogin = true
                        )
                    }
                }
                else{
                    _uiState.update { currentUiState ->
                        currentUiState.copy(
                            isSuccessfullyLogin = false,
                            userMessage = result.userMessage
                        )
                    }
                }
            }
        }
        else{
            _uiState.update { currentUiState ->
                currentUiState.copy(
                    userMessage = "Email and password cannot be left blank."
                )
            }
        }
    }

    fun userMessageShown() {
        _uiState.update { currentUiState ->
            currentUiState.copy(userMessage = null)
        }
    }
}