package com.example.safenet

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface RainfallService {
    @POST("predict/rainfall")  // Correct endpoint
    fun predictRainfall(@Body request: RainfallRequest): Call<RainfallResponse>
}
