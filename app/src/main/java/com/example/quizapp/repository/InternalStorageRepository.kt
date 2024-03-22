package com.example.quizapp.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

private const val TAG = "InternalStorageRepository"

class InternalStorageRepository(private val context: Context) {

    fun isImageSavedToInternalStorage(filename: String, bmp: Bitmap): Boolean {
        return try {
            val directory = File(context.filesDir, "images")
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val file = File(directory, "$filename.jpg")
            val outputStream = FileOutputStream(file)
            if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)) {
                Log.d(TAG, "savePhotoToInternalStorage failed}")
                throw IOException("Couldn't save bitmap.")
            }
            outputStream.close()
            Log.d(TAG, "savePhotoToInternalStorage success.")
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun loadImageFromInternalStorage(filename: String): List<Bitmap> {
        val files = File(context.filesDir, "images").listFiles()
        val bitmapList =
            files?.filter { it.canRead() && it.isFile && it.name == "$filename.jpg" }?.map {
                val bytes = it?.readBytes()
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes!!.size)
                bmp
            }
        return bitmapList ?: listOf()
    }
}