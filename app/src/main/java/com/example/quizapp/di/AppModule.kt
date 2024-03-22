package com.example.quizapp.di

import android.content.Context
import androidx.room.Room
import com.example.quizapp.data.FirebaseService
import com.example.quizapp.data.RoomDao
import com.example.quizapp.data.SeriesDatabase
import com.example.quizapp.data.SeriesRepository
import com.example.quizapp.data.UserPreferencesRepository
import com.example.quizapp.dataStore
import com.example.quizapp.repository.InternalStorageRepository
import com.example.quizapp.data.OmdbAPI
import com.example.quizapp.util.BASE_URL
import com.example.quizapp.util.Connectivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesInternalStorageRepository(@ApplicationContext context: Context) =
        InternalStorageRepository(context)

    @Provides
    @Singleton
    fun providesConnectivity(@ApplicationContext context: Context) = Connectivity(context)

    @Provides
    @Singleton
    fun providesFirebaseService() = FirebaseService()

    @Provides
    @Singleton
    fun providesTvdbRetrofit(): OmdbAPI = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OmdbAPI::class.java)

    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        SeriesDatabase::class.java,
        "series_table.db"
    )
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideRoomDao(seriesDatabase: SeriesDatabase) = seriesDatabase.dao

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(@ApplicationContext context: Context) =
        UserPreferencesRepository(context.dataStore)

    @Provides
    @Singleton
    fun provideSeriesRepository(
        firebaseService: FirebaseService,
        omdbAPI: OmdbAPI,
        roomDao: RoomDao
    ) = SeriesRepository(firebaseService, roomDao, omdbAPI)
}