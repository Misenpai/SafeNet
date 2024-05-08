package com.example.safenet

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelperEarthquake(private val context: Context) {
    private val DB_NAME = "coordinates_earthquake.db"
    val TABLE_NAME_EARTHQUAKE = "coordinates_earthquake"
    private val COL_ID = "id"
    private val COL_LATITUDE = "latitude"
    private val COL_LONGITUDE = "longitude"
    private val DB_VERSION = 1

    private val sqlCreateTableQuery = "CREATE TABLE IF NOT EXISTS ${TABLE_NAME_EARTHQUAKE} ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_LATITUDE REAL, $COL_LONGITUDE REAL)"

    private val dbHelper: SQLiteOpenHelper = object : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(sqlCreateTableQuery)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            // Handle database upgrade if needed
        }
    }

    fun initializeDatabase() {
        // Replace this with your initial coordinate data for earthquakes
        val coordinates = listOf(
            Pair(-15.14,168.00),
            Pair(-3.31, 130.97),
            Pair(-41.19, -84.56), // Example coordinates
            Pair(32.40, -115.25),   // Example coordinates
            Pair(34.9489, -116.8519),
            Pair(-6.6718, 130.7111),
            Pair(51.5072, -0.1275),
            Pair(-20.0419, -70.1167),
            Pair(36.2318, 23.5153),
            Pair(-17.7396, -178.7833),
            Pair(-5.5247, 153.1878),
            Pair(40.7128, -74.0059),
            Pair(-23.4305, -70.4155),
            Pair(37.5412, 141.1797),
            Pair(18.1839, -66.6500),
            Pair(-8.9717, 124.5771),
            Pair(35.7226, 140.8546),
            Pair(-28.6089, -176.8689),
            Pair(-15.5678, -173.3241),
            Pair(38.8224, 141.7043),
            Pair(-7.1442, 128.1294),
            Pair(37.5944, 142.3728),
            Pair(51.3127, 16.0722),
            Pair(-10.6577, 165.1331),
            Pair(18.7650, 69.0357),
            Pair(36.5263, 128.7973),
            Pair(-17.8449, -178.4419),
            Pair(-7.3460, 128.7232),
            Pair(35.8298, 127.1480),
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
            db.insert(TABLE_NAME_EARTHQUAKE, null, values)
        }
    }

    fun getAllCoordinates(): List<Pair<Double, Double>> {
        val coordinates = mutableListOf<Pair<Double, Double>>()
        val cursor = db.query(
            TABLE_NAME_EARTHQUAKE,
            arrayOf(COL_LATITUDE, COL_LONGITUDE),
            null, null, null, null, null
        )
        with(cursor) {
            while (moveToNext()) {
                val latitude = getDouble(getColumnIndexOrThrow(COL_LATITUDE))
                val longitude = getDouble(getColumnIndexOrThrow(COL_LONGITUDE))
                coordinates.add(Pair(latitude, longitude))
            }
            close()
        }
        return coordinates
    }
}