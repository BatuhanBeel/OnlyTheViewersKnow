package com.example.quizapp.data

import com.example.quizapp.model.Series
import com.example.quizapp.util.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OmdbAPI {
    @GET("?apikey=${API_KEY}")
    suspend fun getSeries(
        @Query("t") query: String,
    ): Response<Series>
}