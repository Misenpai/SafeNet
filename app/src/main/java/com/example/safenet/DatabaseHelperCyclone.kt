package com.example.safenet

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelperCyclone(private val context: Context) {
    private val DB_NAME = "coordinates.db"
    val TABLE_NAME = "coordinates"
    private val COL_ID = "id"
    private val COL_LATITUDE = "latitude"
    private val COL_LONGITUDE = "longitude"
    private val DB_VERSION = 1

    private val sqlCreateTableQuery = "CREATE TABLE IF NOT EXISTS $TABLE_NAME ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_LATITUDE REAL, $COL_LONGITUDE REAL)"

    private val dbHelper: SQLiteOpenHelper = object : SQLiteOpenHelper(context,DB_NAME,null,DB_VERSION){
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(sqlCreateTableQuery)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        }


    }

    fun initializeDatabase() {
        val coordinates = listOf(
            Pair(26.2124, 127.6809),
            Pair(33.5904, 130.4017),
            Pair(26.2124, 127.6809),
            Pair(33.5904, 130.4017),
            Pair(35.6895, 139.6917),
            Pair(26.2124, 127.6809),
            Pair(16.5662, 121.2620),
            Pair(10.3157, 123.8854),
            Pair(7.1907, 125.4553),
            Pair(26.6340, 78.3617),
            Pair(26.5361, 77.0616),
            Pair(33.4996, 126.5312),
            Pair(36.5263, 128.7973),
            Pair(35.8298, 127.1480),
            Pair(23.4241, 113.3622),
            Pair(26.0789, 117.9874),
            Pair(29.1832, 120.0934),
            Pair(19.6959, 109.7453),
            Pair(20.1497, 75.2066),
            Pair(20.0169, 75.8200),
            Pair(20.8880, 76.2591),
            Pair(18.7742, 68.3937),
            Pair(18.7650, 69.0357),
            Pair(21.0466, 107.0448),
            Pair(19.8075, 105.7851),
            Pair(18.8890, 105.6810),
            Pair(25.5000, 90.5000),
            Pair(19.1738, 96.1342),
            Pair(23.7416, 98.0734),
            Pair(20.3165, 87.1086),
            Pair(31.9686, 99.9018),
            Pair(30.9843, 91.9623),
            Pair(32.3547, 89.3985),
            Pair(32.3182, 86.9023),
            Pair(27.9944, 81.7603),
            Pair(35.7596, 79.0193),
            Pair(45.2538, 69.4455),
            Pair(-18.7669, 46.8691),
            Pair(17.1899, 88.4976),
            Pair(18.9712, 72.2852),
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
            db.insert(TABLE_NAME, null, values)
        }
    }
}