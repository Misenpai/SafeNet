package com.example.safenet

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OpenWeatherApiClient {
    private const val BASE_URL = "https://api.openweathermap.org/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: OpenWeatherApi = retrofit.create(OpenWeatherApi::class.java)
}