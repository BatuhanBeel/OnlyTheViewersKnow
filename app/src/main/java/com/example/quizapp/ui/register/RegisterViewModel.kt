package com.example.quizapp.ui.register

import androidx.core.util.PatternsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.FirebaseService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "RegisterViewModel"

data class RegisterUiState(
    val isTrueInputEntered: Boolean = false,
    val isSuccessfullyRegistered: Boolean = false,
    val userMessage: String? = null
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseService: FirebaseService
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState

    fun checkUserInputs(email: String, password: String) {
        if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()){
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        isTrueInputEntered = false,
                        userMessage = "Invalid e-mail format."
                    )
                }
            return
            }
        if(password.length < 6 || password.length > 15){
            _uiState.update { currentUiState ->
                currentUiState.copy(
                    isTrueInputEntered = false,
                    userMessage = "Password length must be between 6-15."
                )
            }
            return
        }
        _uiState.update { currentUiState ->
            currentUiState.copy(
                isTrueInputEntered = true
            )
        }
    }

    fun addUserToFirebase(email: String, password: String) {
        viewModelScope.launch {
            val result = firebaseService.firebaseUserRegister(email, password)
            if (result.isSuccessful){
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        isSuccessfullyRegistered = true
                    )
                }
            }
            else{
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        isSuccessfullyRegistered = false,
                        userMessage = result.userMessage
                    )
                }
            }
        }
    }

    fun userMessageShown() {
        _uiState.update { currentUiState ->
            currentUiState.copy(userMessage = null)
        }
    }
}