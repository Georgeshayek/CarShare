package com.example.project

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.cursoradapter.widget.CursorAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import java.util.*

class DriverCard (context: Context, cursor: Cursor, flags: Int,drawable: Drawable,num:Number) : CursorAdapter(context, cursor, flags) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var previousPolyline: Polyline? = null
    private var initmarker: Marker? = null
    private var marker: Marker? = null
    private var drawable: Drawable = drawable
    private var num: Number = num
    private var dbhelp=DB_helper(context)
    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
        return inflater.inflate(R.layout.card_layout, parent, false)
    }
    override fun bindView(view: View?, context: Context, cursor: Cursor?) {

        var db=dbhelp.writableDatabase
        cursor?.columnNames?.forEach { Log.d("Cursor Column", it) }
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
        val personRideCount = cursor?.getInt(cursor.getColumnIndexOrThrow("personride_count"))
        val formattedPrice = String.format("%.2f", price) // format the price with two decimal places
        val priceAndCurrency = "$formattedPrice $currency" // concatenate the price and currency
        val imageView = view?.findViewById<ImageView>(R.id.imag1)
        imageView?.setImageDrawable(drawable)
        val priceTextView = view?.findViewById<TextView>(R.id.price)
        priceTextView?.text = priceAndCurrency
        val nameTextView = view?.findViewById<TextView>(R.id.name12)
        nameTextView?.text = name

        val datetimeTextView = view?.findViewById<TextView>(R.id.name_text_view)
        datetimeTextView?.text = datetime
        val quan1 = view?.findViewById<TextView>(R.id.quan1)
      quan1?.text=personRideCount.toString()
        val quantityTextView = view?.findViewById<TextView>(R.id.quantity)
        quantityTextView?.text = quantity
        val startLocation = view?.findViewById<TextView>(R.id.startlocation)
        val geocoder1 = Geocoder(context!!, Locale.getDefault())
        val addresses1 = geocoder1.getFromLocation(startLat!!, startLong!!, 1)
        if (addresses1?.isNotEmpty() == true) {
            val address = addresses1[0]
            val city = address.locality
            val country = address.countryName
            val locationName = "From $city, $country"
            startLocation?.text = locationName
        }

        val endLocation = view?.findViewById<TextView>(R.id.location)
        val geocoder = Geocoder(context!!, Locale.getDefault())
        val addresses = geocoder.getFromLocation(destLat!!, destLong!!, 1)
        if (addresses?.isNotEmpty() == true) {
            val address = addresses[0]
            val city = address.locality
            val country = address.countryName
            val locationName = "to $city, $country"
            endLocation?.text = locationName
        }

        val layout = view?.findViewById<LinearLayout>(R.id.card_layout)
        layout?.setOnClickListener {
            if(num==0) {
                val intent = Intent(context, Driver::class.java).putExtra("id", id.toString())
                context.startActivity(intent)
            }
            else{  val intent = Intent(context, Passenger::class.java).putExtra("id", id.toString())
                context.startActivity(intent)}

        }
    }
}