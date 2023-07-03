package com.example.project

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cursoradapter.widget.CursorAdapter
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline
import java.util.*

class DriverCardHistory (context: Context, cursor: Cursor, flags: Int, drawable: Drawable, num:Number) : CursorAdapter(context, cursor, flags) {

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
        val formattedPrice = String.format("%.2f", price)
        val priceAndCurrency = "$formattedPrice $currency"
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




        }
    }
