package com.example.safenet

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// Retrofit API interface for Marine Weather data
interface MarineWeatherApi {
    @GET("v1/marine")
    fun getMarineWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("hourly") hourly: String
    ): Call<MarineWeatherResponse>
}
