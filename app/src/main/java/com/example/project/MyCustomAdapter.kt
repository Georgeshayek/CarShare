package com.example.project

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.location.Geocoder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CursorAdapter
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import java.util.*

class MyCustomAdapter(
    context: Context, cursor1: Cursor?, cursor2: Cursor?
) : CursorAdapter(context, null, 0) {

private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var mCursor1: Cursor? = null
    private var mCursor2: Cursor? = null

    init {
        mCursor1 = cursor1
        mCursor2 = cursor2
    }

    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
        // Determine which layout to inflate based on the cursor being used
        val viewType = getItemViewType1(cursor)
        val layoutResId = if (viewType == 0) R.layout.card_layout else R.layout.car_layout2
        val view = inflater.inflate(layoutResId, parent, false)
        return view
    }

    override fun bindView(view: View, context: Context?, cursor: Cursor?) {
        // Bind data to views based on layout
        Log.d(TAG, "bindView: cursor count = ${cursor?.count}")
        val datetime = view.findViewById<TextView>(R.id.name_text_view)
        val name12 = view.findViewById<TextView>(R.id.name12)
        val quantity = view.findViewById<TextView>(R.id.quantity)
        val pricetext = view.findViewById<TextView>(R.id.price)
        val startLocation = view.findViewById<TextView>(R.id.startlocation)
        val endLocation = view.findViewById<TextView>(R.id.location)

        if (cursor == mCursor1) {



                // Retrieve data from cursor1
                Log.d("Cursor Debug", "Cursor position: ${cursor?.position}, Cursor count: ${cursor?.count}")
                val text1 = cursor?.getString(cursor.getColumnIndexOrThrow("datetime"))
                val text2 = cursor?.getString(cursor.getColumnIndexOrThrow("name"))
                val quantity1 = cursor?.getInt(cursor.getColumnIndexOrThrow("quantity"))
                val currency = cursor?.getString(cursor.getColumnIndexOrThrow("currency"))
                val price = cursor?.getDouble(cursor.getColumnIndexOrThrow("price"))
                val formattedPrice = String.format("%.2f", price) // format the price with two decimal places
                val priceAndCurrency = "$formattedPrice $currency" // concatenate the price and currency
                val lat = cursor?.getDouble(cursor.getColumnIndexOrThrow("destlat"))
                val lng = cursor?.getDouble(cursor.getColumnIndexOrThrow("destlong"))
                val geocoder = Geocoder(context!!, Locale.getDefault())
                val addresses = geocoder.getFromLocation(lat!!, lng!!, 1)
                if (addresses != null) {
                    if (addresses.isNotEmpty()) {
                        val address = addresses[0]
                        val city = address.locality
                        val country = address.countryName
                        val locationName = "to $city, $country"
                        endLocation.text = locationName
                    }
                }
                val lat1 = cursor?.getDouble(cursor.getColumnIndexOrThrow("startlat"))
                val lng1 = cursor?.getDouble(cursor.getColumnIndexOrThrow("startlong"))
                val geocoder1 = Geocoder(context!!, Locale.getDefault())
                val addresses1 = geocoder1.getFromLocation(lat1!!, lng1!!, 1)
                if (addresses1 != null) {
                    if (addresses1.isNotEmpty()) {
                        val address1 = addresses1[0]
                        val city1 = address1.locality
                        val country1 = address1.countryName
                        val locationName1 = "from $city1, $country1"
                        startLocation.text = locationName1
                    }
                }

                // Bind data to views
                datetime.text = text1 ?: ""
                name12.text = text2 ?: ""
                quantity.text = quantity1?.toString() ?: ""
                pricetext.text = priceAndCurrency

            }
        else if (cursor == mCursor2) {
            val text1 = cursor?.getString(cursor.getColumnIndexOrThrow("datetime"))
            val text2 = cursor?.getString(cursor.getColumnIndexOrThrow("email"))
            val quantity1 = cursor?.getInt(cursor.getColumnIndexOrThrow("quantity"))
            val currency = cursor?.getString(cursor.getColumnIndexOrThrow("currency"))
            val price = cursor?.getDouble(cursor.getColumnIndexOrThrow("price"))
            val formattedPrice = String.format("%.2f", price) // format the price with two decimal places
            val priceAndCurrency = "$formattedPrice $currency" // concatenate the price and currency
            val lat = cursor?.getDouble(cursor.getColumnIndexOrThrow("destlat"))
            val lng = cursor?.getDouble(cursor.getColumnIndexOrThrow("destlong"))
            val geocoder = Geocoder(context!!, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat!!, lng!!, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val city = address.locality
                    val country = address.countryName
                    val locationName = "to $city, $country"
                    endLocation.text = locationName
                }
            }
            val lat1 = cursor?.getDouble(cursor.getColumnIndexOrThrow("startlat"))
            val lng1 = cursor?.getDouble(cursor.getColumnIndexOrThrow("startlong"))
            val geocoder1 = Geocoder(context!!, Locale.getDefault())
            val addresses1 = geocoder1.getFromLocation(lat1!!, lng1!!, 1)
            if (addresses1 != null) {
                if (addresses1.isNotEmpty()) {
                    val address1 = addresses1[0]
                    val city1 = address1.locality
                    val country1 = address1.countryName
                    val locationName1 = "from $city1, $country1"
                    startLocation.text = locationName1
                }
            }

            // Bind data to views
            datetime.text = text1 ?: ""
            name12.text = text2 ?: ""
            quantity.text = quantity1?.toString() ?: ""
            pricetext.text = priceAndCurrency
                    }


    }

    override fun getItemViewType(position: Int): Int {

        return if (position < mCursor1?.count ?: 0) {
            0
        } else {
            1
        }
    }
    fun getItemViewType1(cursor: Cursor?): Int {

        return if (cursor == mCursor1) 0 else 1
    }

    override fun getViewTypeCount(): Int {

        return 2
    }

    fun swapCursors(cursor1: Cursor?, cursor2: Cursor?) {
        mCursor1?.close()
        mCursor2?.close()
        mCursor1 = cursor1
        mCursor2 = cursor2
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        // Return the total number of items in both cursors
        return (mCursor1 ?.count ?: 0) + (mCursor2 ?.count ?: 0)
    }}