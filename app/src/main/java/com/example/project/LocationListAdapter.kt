package com.example.project

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat

import androidx.cursoradapter.widget.CursorAdapter
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*


class LocationListAdapter(context: Context, cursor: Cursor, flags: Int,private val mMap: GoogleMap,str:String,activity: Activity) : CursorAdapter(context, cursor, flags) {
    private val callingActivity = activity
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var previousPolyline: Polyline? = null
    private var initmarker: Marker? = null
    private var marker: Marker? = null
    private val  email:String=str
    private val markerPairs: MutableList<Pair<Marker, Marker>>
    init {
        markerPairs = mutableListOf()
        populateMarkersFromCursor(cursor,context)
    }
    private fun populateMarkersFromCursor(cursor: Cursor?,context: Context?) {
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val startLat = cursor.getDouble(cursor.getColumnIndexOrThrow("startlat"))
                val startLong = cursor.getDouble(cursor.getColumnIndexOrThrow("startlong"))
                val destLat = cursor.getDouble(cursor.getColumnIndexOrThrow("destlat"))
                val destLong = cursor.getDouble(cursor.getColumnIndexOrThrow("destlong"))
                val vectorDrawable = ContextCompat.getDrawable(context!!, R.drawable.ic_map)
                val bitmap = Bitmap.createBitmap(
                    vectorDrawable!!.intrinsicWidth,
                    vectorDrawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap)
                vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
                vectorDrawable.draw(canvas)

                val vectorDrawable1 = ContextCompat.getDrawable(context!!, R.drawable.ic_maplocation2)
                val bitmap1 = Bitmap.createBitmap(
                    vectorDrawable1!!.intrinsicWidth,
                    vectorDrawable1.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
                val canvas1 = Canvas(bitmap1)
                vectorDrawable1.setBounds(0, 0, canvas1.width, canvas1.height)
                vectorDrawable1.draw(canvas1)
                val startMarker = mMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(startLat, startLong))
                        .title("Start Position").icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                )
                val destMarker = mMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(destLat, destLong))
                        .title("End Position").icon(BitmapDescriptorFactory.fromBitmap(bitmap1))
                )
                val dotPattern = listOf(Dash(10f), Gap(10f))
                val lineOptions = PolylineOptions()
                    .add(LatLng(startLat, startLong), LatLng(destLat, destLong))
                    .width(5f)
                    .color(Color.RED).pattern(dotPattern)
                mMap.addPolyline(lineOptions)
                markerPairs.add(Pair(startMarker!!, destMarker!!))

            } while (cursor.moveToNext())




        }
    }
    private var dbhelp=DB_helper(context)
    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {

        return inflater.inflate(R.layout.card_layout1, parent, false)
    }

    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {

        var db=dbhelp.writableDatabase

        val id = cursor?.getInt(cursor.getColumnIndexOrThrow("id"))
        val datetime = cursor?.getString(cursor.getColumnIndexOrThrow("datetime"))
        val name = cursor?.getString(cursor.getColumnIndexOrThrow("name"))
        val quantity = cursor?.getString(cursor.getColumnIndexOrThrow("quantity"))
        val price = cursor?.getDouble(cursor.getColumnIndexOrThrow("price"))
        val currency = cursor?.getString(cursor.getColumnIndexOrThrow("currency"))
        val destLat = cursor?.getDouble(cursor.getColumnIndexOrThrow("destlat"))
        val destLong = cursor?.getDouble(cursor.getColumnIndexOrThrow("destlong"))
        val startLat = cursor?.getDouble(cursor.getColumnIndexOrThrow("startlat"))
        val startLong = cursor?.getDouble(cursor.getColumnIndexOrThrow("startlong"))
        val personcounter = cursor?.getString(cursor.getColumnIndexOrThrow("personride_count"))
        val formattedPrice = String.format("%.2f", price) // format the price with two decimal places
        val priceAndCurrency = "$formattedPrice $currency" // concatenate the price and currency
        val priceTextView = view?.findViewById<TextView>(R.id.price)
        priceTextView?.text = priceAndCurrency
        val personrid = view?.findViewById<TextView>(R.id.quan1)
        personrid?.text=personcounter
        val nameTextView = view?.findViewById<TextView>(R.id.name12)
        nameTextView?.text = name

        val datetimeTextView = view?.findViewById<TextView>(R.id.datetime)
        datetimeTextView?.text = datetime

        val quantityTextView = view?.findViewById<TextView>(R.id.quantity)
        quantityTextView?.text = quantity
        val button = view?.findViewById<Button>(R.id.locbutton)
        val boundsBuilder = LatLngBounds.Builder()
        val button1 = view?.findViewById<Button>(R.id.bookbutton)
        button?.setOnClickListener {
            previousPolyline?.remove()
            marker?.remove()
            initmarker?.remove()
            val lat0 = startLat ?: 0.0
            val lng0 = startLong ?: 0.0
            val lat = destLat ?: 0.0
            val lng = destLong ?: 0.0

            val position0 = LatLng(lat0, lng0)
            val position = LatLng(lat, lng)

            boundsBuilder.include(position0)
            boundsBuilder.include(position)
            val bounds = boundsBuilder.build()
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            val vectorDrawable = ContextCompat.getDrawable(context!!, R.drawable.ic_map)
            val bitmap = Bitmap.createBitmap(
                vectorDrawable!!.intrinsicWidth,
                vectorDrawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
            vectorDrawable.draw(canvas)

            val vectorDrawable1 = ContextCompat.getDrawable(context!!, R.drawable.ic_maplocation2)
            val bitmap1 = Bitmap.createBitmap(
                vectorDrawable1!!.intrinsicWidth,
                vectorDrawable1.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas1 = Canvas(bitmap1)
            vectorDrawable1.setBounds(0, 0, canvas1.width, canvas1.height)
            vectorDrawable1.draw(canvas1)
            val dotPattern = listOf(Dash(10f), Gap(10f))
            val lineOptions = PolylineOptions()
                .add(position0, position)
                .width(5f)
                .color(Color.RED).pattern(dotPattern)

            previousPolyline = mMap.addPolyline(lineOptions)

            marker=mMap.addMarker(MarkerOptions().position(position).title("endPos").icon(BitmapDescriptorFactory.fromBitmap(bitmap1)))
            initmarker=mMap.addMarker(MarkerOptions().position(position0).title("startpos").icon(BitmapDescriptorFactory.fromBitmap(bitmap)))


        }
        button1?.setOnClickListener {
            var data = ContentValues()

            data.put("ride_id", id)

            data.put("email", email)

            var rs: Long = db.insert("personride", null, data)
            if (!rs.equals(-1)) {
                Toast.makeText(context, "succesfully booked!", Toast.LENGTH_SHORT)
                    .show()
                callingActivity.finish()
            } else {
                Toast.makeText(context, "didnt work!", Toast.LENGTH_SHORT).show()

            }

        }
    }
    fun fillMarkerPairs(): MutableList<Pair<Marker, Marker>>{
        return markerPairs

    }
}