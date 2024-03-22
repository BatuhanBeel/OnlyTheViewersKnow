package com.example.quizapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.quizapp.model.SeriesAndResults

@Database(entities = [SeriesAndResults::class], version = 1)
abstract class SeriesDatabase(): RoomDatabase() {
    abstract val dao: RoomDao
}