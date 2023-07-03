package com.example.project
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
class DB_helper(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    companion object {
        private val DATABASE_VERSION = 4
        private val DATABASE_NAME = "tempDatabase6"
        private val TABLE_CONTACTS = "user"
        private val KEY_NAME = "name"
        private val KEY_EMAIL = "email"
        private val KEY_Phone = "phone"
        private val KEY_PSWD = "pswd"
        private val KEY_img = "img"
        private val TABLE_Ride = "ride"
        private  val RIDE_ID = "id"
        private val KEY_STARTlong = "startlong"
        private val KEY_STARTlat = "startlat"
        private val KEY_Destlong = "destlong"
        private val KEY_Destlat = "destlat"
        private val KEY_DateTime = "datetime"
        private val KEY_price = "price"
        private val KEY_quantity = "quantity"
        private val KEY_Currency = "currency"
        private val KEY_STARTCity = "startcity"
        private val KEY_DestCity = "destcity"
        private val KEY_IsDeleted="isdeleted"
    }

        override fun onCreate(db: SQLiteDatabase?) {
            val createContactsTable = "CREATE TABLE IF NOT EXISTS $TABLE_CONTACTS ($KEY_NAME TEXT,$KEY_Phone INTEGER,$KEY_EMAIL TEXT, $KEY_PSWD TEXT, $KEY_img TEXT)"
            db?.execSQL(createContactsTable)

            val createRideTable = "CREATE TABLE IF NOT EXISTS $TABLE_Ride ($RIDE_ID INTEGER PRIMARY KEY AUTOINCREMENT, $KEY_EMAIL TEXT, $KEY_STARTlong DOUBLE, $KEY_STARTlat DOUBLE, $KEY_Destlong DOUBLE, $KEY_Destlat DOUBLE, $KEY_DateTime DATETIME, $KEY_price INTEGER, $KEY_Currency TEXT,$KEY_STARTCity TEXT,$KEY_DestCity TEXT, $KEY_quantity INTEGER,$KEY_IsDeleted INTEGER, FOREIGN KEY($KEY_EMAIL) REFERENCES $TABLE_CONTACTS($KEY_EMAIL) ON DELETE CASCADE)"
            db?.execSQL(createRideTable)

            val createPersonRideTable = "CREATE TABLE IF NOT EXISTS personride (id INTEGER PRIMARY KEY AUTOINCREMENT, ride_id INTEGER, email TEXT, FOREIGN KEY (ride_id) REFERENCES ride(id)  ON DELETE CASCADE)"
            db?.execSQL(createPersonRideTable)

            db?.execSQL("PRAGMA foreign_keys=ON;")
        }
    fun deleteUserByEmail(email: String) {
        val db = writableDatabase
        val selection = "$KEY_EMAIL = ?"
        val selectionArgs = arrayOf(email)
        db.delete(TABLE_CONTACTS, selection, selectionArgs)

        db.close()
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // If upgrading from version 1 or earlier, drop all tables and recreate them
            db?.execSQL("DROP TABLE IF EXISTS personride")
            db?.execSQL("DROP TABLE IF EXISTS ride")
            db?.execSQL("DROP TABLE IF EXISTS user")
            onCreate(db)
        } else {
            // If upgrading from version 2 or later, simply recreate the ride table
            db?.execSQL("DROP TABLE IF EXISTS personride")
            db?.execSQL("DROP TABLE IF EXISTS ride")
            onCreate(db)
        }
    }

//    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
//        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS)
//        onCreate(db)
//    }

}