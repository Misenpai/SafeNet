package com.example.safenet

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelperFlood(private val context: Context) {

    private val DB_NAME = "coordinates_flood.db"
    val TABLE_NAME_FLOOD = "coordinates_flood"
    private val COL_ID = "id"
    private val COL_LATITUDE = "latitude"
    private val COL_LONGITUDE = "longitude"
    private val DB_VERSION = 1

    private val sqlCreateTableQuery = "CREATE TABLE IF NOT EXISTS $TABLE_NAME_FLOOD ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_LATITUDE REAL, $COL_LONGITUDE REAL)"

    private val dbHelper: SQLiteOpenHelper = object : SQLiteOpenHelper(context,DB_NAME,null,DB_VERSION){
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(sqlCreateTableQuery)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        }


    }

    fun initializeDatabase() {
        val coordinates = listOf(
            Pair(53.30,-6.28),
            Pair(30.06, 31.24),
        Pair(23.68, 90.35),
        Pair(4.57, -74.30),
        Pair(10.76, 106.70),
        Pair(18.11, -77.30),
        Pair(33.94, 67.71),
        Pair(26.82, 30.80),
        Pair(35.69, 139.69),
        Pair(23.63, 120.99),
        Pair(41.90, 12.45),
        Pair(47.16, 9.55),
        Pair(51.92, 4.47),
        Pair(56.13, -106.35),
        Pair(-6.17, 106.83),
        Pair(14.10, -87.22),
        Pair(18.97, 72.82),
        Pair(27.72, 85.32),
        Pair(33.85, 35.86),
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
            db.insert(TABLE_NAME_FLOOD, null, values)
        }
    }
}