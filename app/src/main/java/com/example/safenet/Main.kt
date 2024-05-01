package com.example.safenet

import com.google.gson.annotations.SerializedName

data class OpenWeatherResponse(
    val main: Main,
    val rain: Rain?,
    val wind: Wind?
)

data class Main(
    @SerializedName("temp")
    val temperature: Double
)

data class Rain(
    @SerializedName("1h")
    val rainLast1Hour: Double?
)

data class Wind(
    @SerializedName("speed")
    val windspeed: Double?
)
