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