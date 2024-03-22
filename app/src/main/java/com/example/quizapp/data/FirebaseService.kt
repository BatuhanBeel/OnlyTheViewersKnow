package com.example.quizapp.data

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.example.quizapp.model.Series
import com.example.quizapp.model.SeriesAndResults
import com.example.quizapp.model.SeriesLevels
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.File

private const val TAG = "FirebaseService"

data class FirebaseResult(
    val profileUri: Uri? = null,
    val seriesLevels: SeriesLevels = SeriesLevels(),
    val isSuccessful: Boolean = false,
    val userMessage: String? = null
)

class FirebaseService {

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore
    private val storage = Firebase.storage

    fun provideFirebaseAuth() = auth

    init {
        setup()
    }

    private fun setup() {
        val settings = FirebaseFirestoreSettings.Builder()
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()
        firestore.firestoreSettings = settings
    }

    suspend fun updateUserQuizResultsFirebase(userData: SeriesAndResults){
        try {
            firestore.collection("QuizResults")
                .document(userData.userUid)
                .collection(userData.series.title)
                .document("levels")
                .set(userData.userResults)
                .await()
        }catch (e: Exception){
            Log.d(TAG, "Quiz result update operation failed: ${e.message}")
        }

    }

    suspend fun getUserQuizResultsFromFirestore(userUid: String, seriesName: String): FirebaseResult {
        try {
            val job = firestore.collection("QuizResults")
                .document(userUid)
                .collection(seriesName)
                .document("levels")
                .get()
                .await()
            if (job.exists()){
                val results = job.toObject(SeriesLevels::class.java)
                results?.let {
                    return FirebaseResult(seriesLevels = it, isSuccessful = true)
                }
            }
            return FirebaseResult(isSuccessful = false)
        } catch (e: Exception) {
            Log.d(TAG, "Quiz result fetched operation failed: ${e.message}")
            return FirebaseResult(isSuccessful = false)
        }
    }

    suspend fun uploadUserImageToFirebaseStorage(bmp: Bitmap): FirebaseResult {
        return try {
            val stream = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream)
            val data = stream.toByteArray()
            storage.reference.child("images/${auth.currentUser?.uid}")
                .putBytes(data).await()
            FirebaseResult(isSuccessful = true)
        }catch (e: Exception){
            FirebaseResult(isSuccessful = false, userMessage = e.message)
        }
    }

    suspend fun getUserImageUriFromFirebaseStorage(): FirebaseResult {
        return try {
            val uri = storage.reference.child("images/${auth.currentUser?.uid}").downloadUrl.await()
            FirebaseResult(profileUri = uri, isSuccessful = true)
        } catch (e: Exception) {
            FirebaseResult(userMessage = e.message, isSuccessful = false)
        }
    }

    suspend fun saveImageToLocal(filePath: String): FirebaseResult{
        return try {
            val directory = File(filePath,"images")
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val ref = storage.reference.child("images/${auth.currentUser?.uid}")
            val localFile = File(directory, "${ref.name}.jpg")
            ref.getFile(localFile).await()
            FirebaseResult(isSuccessful = true)
        }
        catch(e: Exception) {
            FirebaseResult(userMessage = e.message, isSuccessful = false)
        }
    }

    suspend fun firebaseUserSignIn(email: String, password: String): FirebaseResult {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            FirebaseResult(isSuccessful = true)
        } catch (e: Exception) {
            FirebaseResult(isSuccessful = false, userMessage = e.message.toString())
        }
    }

    suspend fun firebaseUserRegister(email: String, password: String): FirebaseResult {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            FirebaseResult(isSuccessful = true)
        } catch (e: Exception) {
            FirebaseResult(isSuccessful = false, userMessage = e.message.toString())
        }
    }

    fun signOutFirebaseAuth() {
        auth.signOut()
    }
}