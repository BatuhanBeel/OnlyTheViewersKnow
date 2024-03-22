package com.example.quizapp.ui.profile

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.repository.InternalStorageRepository
import com.example.quizapp.data.FirebaseService
import com.example.quizapp.util.Connectivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ProfileViewModel"

data class ProfileUiState(
    val imageBitmap: Bitmap? = null,
    val imageUri: Uri? = null,
    val isLoading: Boolean = true,
    val userMessage: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firebaseService: FirebaseService,
    private val internalStorageRepository: InternalStorageRepository,
    private val connectivity: Connectivity
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    private val authUser = firebaseService.provideFirebaseAuth().currentUser

    val visiblePermissionDialogQueue = MutableLiveData<MutableList<String>>(mutableListOf())

    fun dismissDialog() {
        visiblePermissionDialogQueue.value?.removeFirst()
    }


    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if (!isGranted && !visiblePermissionDialogQueue.value?.contains(permission)!!) {
            val permissionList = visiblePermissionDialogQueue.value!!
            permissionList.add(permission)
            visiblePermissionDialogQueue.postValue(permissionList)
        }
    }

    init {
        loadProfileImage()
    }

    private fun loadProfileImage() {
        viewModelScope.launch {
            if (connectivity.isNetworkAvailable()) {
                uploadImageFromFirebase()
            } else {
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        userMessage = "Profile image could not be updated due to lack of internet."
                    )
                }
                uploadImageFromInternalStorage()
            }
        }
    }

    private fun uploadImageFromFirebase() {
        viewModelScope.launch(Dispatchers.IO) {
            val uriResult = firebaseService.getUserImageUriFromFirebaseStorage()
            if (uriResult.isSuccessful) {
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        imageBitmap = null,
                        imageUri = uriResult.profileUri,
                        isLoading = false
                    )
                }
            }
            else{
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        imageBitmap = null,
                        imageUri = null,
                        userMessage = uriResult.userMessage,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun uploadImageFromInternalStorage() {
        viewModelScope.launch(Dispatchers.IO) {
            if(authUser != null){
                val fileName = authUser.uid
                val imagesList = internalStorageRepository.loadImageFromInternalStorage(fileName)
                if (imagesList.isNotEmpty()) {
                    _uiState.update { currentUiState ->
                        currentUiState.copy(
                            imageBitmap = imagesList[0],
                            imageUri = null,
                            isLoading = false,
                        )
                    }
                }
            }
        }
    }

    fun savePhotoToInternalStorageFromFirebaseUri(filePath: String){
        viewModelScope.launch(Dispatchers.IO) {
            val result = firebaseService.saveImageToLocal(filePath)
            if (!result.isSuccessful){
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        userMessage = result.userMessage
                    )
                }
            }
        }
    }

    private fun uploadImageToFirebaseStorage(bmp: Bitmap){
        viewModelScope.launch(Dispatchers.IO) {
            val result = firebaseService.uploadUserImageToFirebaseStorage(bmp)
            if (!result.isSuccessful){
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        userMessage = result.userMessage
                    )
                }
            }
        }
    }

    fun saveImageToInternalStorage(bmp: Bitmap) {
        if (authUser != null){
            val fileName = authUser.uid
            val isSuccessful =internalStorageRepository.isImageSavedToInternalStorage(fileName, bmp)
            if (isSuccessful){
                uploadImageFromInternalStorage()
                uploadImageToFirebaseStorage(bmp)
            }
        }
    }

    fun signOutUser(){
        firebaseService.signOutFirebaseAuth()
    }

    fun userMessageShown() {
        _uiState.update { currentUiState ->
            currentUiState.copy(userMessage = null)
        }
    }

    fun updateLoadingState() {
        _uiState.update { currentUiState ->
            currentUiState.copy(isLoading = true)
        }
    }

    fun pageRefreshed(){
        loadProfileImage()
    }
}
