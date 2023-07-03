package com.example.project

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.example.project.databinding.MapActivityBinding
import com.example.project.databinding.UpdateMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.util.*

class UpdateMap: AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private var previousPolyline: Polyline? = null
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: UpdateMapBinding
    private var currentLocation: LatLng = LatLng(0.0, 0.0)
    private var initmarker: Marker? = null
    private var marker: Marker? = null
    private var markerCount = 2
    private lateinit var city: String
    private lateinit var city1: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = UpdateMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
    val toolbar = findViewById<RelativeLayout>(R.id.toolbar)
    toolbar.findViewById<ImageButton>(R.id.back_button)?.setOnClickListener {
        finish()
    }
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION_PERMISSION
        )

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment1) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        val id= intent.getStringExtra("id")
        mMap = googleMap
        val dbHelper = DB_helper(applicationContext)
        val db1 = dbHelper.readableDatabase
        val query2 = "SELECT ride.id AS _id, ride.* " +
                "FROM ride " +
                "WHERE ride.id=$id"
        val cursor2= db1.rawQuery(query2, null)
        if (cursor2.moveToFirst()) {


            val startlat: Double = cursor2.getDouble(cursor2.getColumnIndexOrThrow("startlat"))
            val startlong: Double = cursor2.getDouble(cursor2.getColumnIndexOrThrow("startlong"))
            val destlat: Double = cursor2.getDouble(cursor2.getColumnIndexOrThrow("destlat"))
            val destlong: Double = cursor2.getDouble(cursor2.getColumnIndexOrThrow("destlong"))
            val position0 = LatLng(startlat, startlong)
            val position = LatLng(destlat, destlong)


            val dotPattern = listOf(Dash(10f), Gap(10f))
            val lineOptions = PolylineOptions()
                .add(position0, position)
                .width(5f)
                .color(Color.RED).pattern(dotPattern)
            previousPolyline = mMap.addPolyline(lineOptions)
            val vectorDrawable = VectorDrawableCompat.create(resources, R.drawable.ic_map, null)
            val bitmap = Bitmap.createBitmap(
                vectorDrawable!!.intrinsicWidth,
                vectorDrawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
            vectorDrawable.draw(canvas)
            val vectorDrawable1 = VectorDrawableCompat.create(resources, R.drawable.ic_maplocation2, null)
            val bitmap1 = Bitmap.createBitmap(
                vectorDrawable1!!.intrinsicWidth,
                vectorDrawable1.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas1 = Canvas(bitmap1)
            vectorDrawable1.setBounds(0, 0, canvas1.width, canvas1.height)
            vectorDrawable1.draw(canvas1)
            marker=mMap.addMarker(MarkerOptions().position(position).title("endPos").icon(BitmapDescriptorFactory.fromBitmap(bitmap1)))
            initmarker=mMap.addMarker(MarkerOptions().position(position0).title("startpos").icon(BitmapDescriptorFactory.fromBitmap(bitmap)))
            mMap.setOnMapLoadedCallback {
                val bounds = LatLngBounds.Builder()
                    .include(position0)
                    .include(position)
                    .build()
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            }

        }
        cursor2.close()

        val button1:Button=findViewById(R.id.removeonemarker)
        val button2:Button=findViewById(R.id.removetwomarker)
        val button3:Button=findViewById(R.id.cancel)
        val button4:Button=findViewById(R.id.apply)
        button3.setOnClickListener {
            finish()
        }
        button1.setOnClickListener {
            if(markerCount!=0){

                if(markerCount==1)
                {
                    initmarker?.remove()
                    markerCount--
                }
                else if(markerCount==2)
                {
                    marker?.remove()
                    markerCount--
                    previousPolyline?.remove()
                }

            }
            else{

                val toast = Toast.makeText(applicationContext, "There is no marker on the map", Toast.LENGTH_SHORT)
                toast.show()
            }}
            button2.setOnClickListener {
                if (markerCount==2)
                {
                    initmarker?.remove()
                    marker?.remove()
                    previousPolyline?.remove()
                    markerCount=0
                }
                else if(markerCount==1){
                    initmarker?.remove()
                    markerCount--
                }
                else{
                    val toast = Toast.makeText(applicationContext, "There is no marker on the map", Toast.LENGTH_SHORT)
                    toast.show()
                }
            }

            button4.setOnClickListener {

                val builder = AlertDialog.Builder(this)
                builder.setMessage("Are you sure you want to cancel this ride?")
                builder.setPositiveButton("Yes") { dialog, which ->
                    val dbHelper = DB_helper(this)
                    val db = dbHelper.writableDatabase
                    val latLng0 = initmarker?.position
                    val latitude0 = latLng0?.latitude
                    val longitude0 = latLng0?.longitude

                    val latLng = marker?.position
                    val latitude = latLng?.latitude
                    val longitude = latLng?.longitude
                    val geocoder1 = Geocoder(this, Locale.getDefault())
                    val addresses1 = geocoder1.getFromLocation(latLng0!!.latitude, latLng0!!.longitude, 1)

                    if (addresses1?.isNotEmpty() == true) {
                        val address = addresses1[0]
                        city = address.locality

                    }
                    val geocoder2 = Geocoder(this, Locale.getDefault())
                    val addresses2 = geocoder2.getFromLocation(latLng!!.latitude, latLng!!.longitude, 1)

                    if (addresses2?.isNotEmpty() == true) {
                        val address = addresses2[0]
                        city1 = address.locality

                    }
                    var data = ContentValues()
                    data.put("startlat", latitude0)
                    data.put("startlong", longitude0)
                    data.put("destlat", latitude)
                    data.put("destlong", longitude)
                    data.put("startcity", city)
                    data.put("destcity", city1)
                    val where = "id=?"
                    val whereArgs = arrayOf<String>(java.lang.String.valueOf(id))
                    try {
                        if (markerCount==2)
                        {
                        db.update("ride", data, where, whereArgs)
                        Toast.makeText(this,"succesfully updated!", Toast.LENGTH_SHORT).show()
                        finish()}
                        else{
                            Toast.makeText(this,"you have ${markerCount} on the map you need 2!", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        val error = e.message.toString()
                        Toast.makeText(this,error, Toast.LENGTH_SHORT).show()
                    }
                }
                builder.setNegativeButton("No") { dialog, which ->

                }
                val dialog = builder.create()
                dialog.show()



            }



        mMap.setOnMapClickListener(this)
    }

    private fun requestLocationUpdates() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        } else {

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->

                val latitude = location?.latitude
                val longitude = location?.longitude

                // Store the current location in a LatLng object
                currentLocation = LatLng(latitude ?: 0.0, longitude ?: 0.0)

                // Add a marker at the current location


                // Move the camera to the current location
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, request location updates
                requestLocationUpdates()
            }
        }
    }

    companion object {
        const val REQUEST_LOCATION_PERMISSION = 1
    }

    override fun onMapClick(latLng: LatLng) {
        val id= intent.getStringExtra("id")
        // Remove the existing marker if one exists
        if (markerCount == 0) {
            // Add first marker
            val vectorDrawable = VectorDrawableCompat.create(resources, R.drawable.ic_map, null)
            val bitmap = Bitmap.createBitmap(
                vectorDrawable!!.intrinsicWidth,
                vectorDrawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
            vectorDrawable.draw(canvas)

            initmarker = mMap.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(bitmap)))
            markerCount++
        } else if (markerCount == 1) {
            // Add second marker
            val vectorDrawable1 = VectorDrawableCompat.create(resources, R.drawable.ic_maplocation2, null)
            val bitmap1 = Bitmap.createBitmap(
                vectorDrawable1!!.intrinsicWidth,
                vectorDrawable1.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas1 = Canvas(bitmap1)
            vectorDrawable1.setBounds(0, 0, canvas1.width, canvas1.height)
            vectorDrawable1.draw(canvas1)
            marker = mMap.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(bitmap1)))
            markerCount++
            val dotPattern = listOf(Dash(10f), Gap(10f))
            val lineOptions = PolylineOptions()
                .add(initmarker?.position, marker?.position)
                .width(5f)
                .color(Color.RED).pattern(dotPattern)
            previousPolyline = mMap.addPolyline(lineOptions)

        }

    }
}