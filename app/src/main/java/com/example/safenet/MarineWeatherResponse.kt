package com.example.safenet

import com.google.gson.annotations.SerializedName

data class MarineWeatherResponse(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("generationtime_ms") val generationTimeMs: Double,
    @SerializedName("utc_offset_seconds") val utcOffsetSeconds: Int,
    @SerializedName("timezone") val timezone: String,
    @SerializedName("hourly_units") val hourlyUnits: HourlyUnits,
    @SerializedName("hourly") val hourly: Hourly
)

data class Hourly(
    @SerializedName("wave_height") val waveHeight: List<Double>,
    @SerializedName("time") val time: List<String>
)

data class HourlyUnits(
    @SerializedName("wave_height") val waveHeightUnit: String,
    @SerializedName("time") val timeUnit: String
)