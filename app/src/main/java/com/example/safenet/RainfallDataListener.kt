package com.example.safenet

interface RainfallDataListener {
    fun onRainfallDataReceived(rainfallData: Map<String, Float>)
}