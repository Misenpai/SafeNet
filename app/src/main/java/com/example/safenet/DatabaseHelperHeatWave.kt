package com.example.safenet

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelperHeatWave(private val context:Context) {
    private val DB_NAME = "coordinates_heatwave.db"
    val TABLE_NAME_HEATWAVE = "coordinates_heatwave"
    private val COL_ID = "id"
    private val COL_LATITUDE = "latitude"
    private val COL_LONGITUDE = "longitude"
    private val DB_VERSION = 1

    private val sqlCreateTableQuery = "CREATE TABLE IF NOT EXISTS $TABLE_NAME_HEATWAVE ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_LATITUDE REAL, $COL_LONGITUDE REAL)"

    private val dbHelper: SQLiteOpenHelper = object : SQLiteOpenHelper(context,DB_NAME,null,DB_VERSION){
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(sqlCreateTableQuery)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        }


    }

    fun initializeDatabase() {
        val coordinates = listOf(
            Pair(28.70, 77.10), // Delhi
            Pair(19.07, 72.87), // Mumbai
            Pair(22.57, 88.36), // Kolkata
            Pair(13.08, 80.27), // Chennai
            Pair(23.25, 77.41), // Bhopal
            Pair(17.37, 78.48), // Hyderabad
            Pair(18.52, 73.86), // Pune
            Pair(26.91, 75.79), // Jaipur
            Pair(25.59, 85.14), // Patna
            Pair(30.73, 76.78), // Chandigarh
            Pair(22.72, 75.86), // Indore
            Pair(28.66, 77.23), // Gurgaon
            Pair(21.17, 72.83), // Surat
            Pair(12.97, 77.59), // Bangalore
            Pair(23.03, 72.58), // Ahmedabad
            Pair(18.10, 79.52), // Vijayawada
            Pair(23.18, 79.98), // Jabalpur
            Pair(25.67, 85.31), // Gaya
            Pair(26.46, 80.35), // Kanpur
            Pair(11.00, 76.96) // Kochi
        )
        insertCoordinates(coordinates)
    }


    val db: SQLiteDatabase = dbHelper.writableDatabase
    fun insertCoordinates(coordinates: List<Pair<Double, Double>>) {
        val values = ContentValues()
        for ((lat, lon) in coordinates) {
            values.clear()
            values.put(COL_LATITUDE, lat)
            values.put(COL_LONGITUDE, lon)
            db.insert(TABLE_NAME_HEATWAVE, null, values)
        }
    }
}