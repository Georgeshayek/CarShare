package com.example.project
import android.Manifest


import android.content.pm.PackageManager

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

import androidx.core.content.ContextCompat
import com.example.project.databinding.MapActivity2Binding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.appbar.MaterialToolbar

class MapActivity2  : AppCompatActivity(), OnMapReadyCallback {
    private var previousPolyline: Polyline? = null
    private lateinit var binding: MapActivity2Binding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private var currentLocation: LatLng = LatLng(0.0, 0.0)
    private var initmarker: Marker? = null
    private var marker: Marker? = null
    private lateinit var text1:TextView
    private lateinit var spinner: Spinner
    override fun onBackPressed() {
        super.onBackPressed()
     finish()

    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = MapActivity2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<RelativeLayout>(R.id.toolbar)
        toolbar.findViewById<ImageButton>(R.id.back_button)?.setOnClickListener {
            finish()
        }

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            MapActivity.REQUEST_LOCATION_PERMISSION
        )

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment1) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {

        val email= intent.getStringExtra("email").toString()
        mMap = googleMap
        text1=findViewById(R.id.text1)
        val dbHelper = DB_helper(applicationContext)
        val db = dbHelper.readableDatabase
        spinner=findViewById(R.id.spinner)
        val adapter122 = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("None","Zahlé", "Ferzol","Sidon","Beirut","Nabatieh","Tyre","Antelias"))
        adapter122.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter122
        var markerPairs= mutableListOf<Pair<Marker, Marker>>()
        val listView = findViewById<ListView>(R.id.location_list)
        val email1 = SharedPreferencesUtils.getEmail(this)
        val query = "SELECT ride.id AS _id, ride.*, user.name, " +
                "(SELECT COUNT(*) FROM personride WHERE personride.ride_id = ride.id) AS personride_count " +
                "FROM ride " +
                "JOIN user ON ride.email = user.email " +
                "WHERE ride.email != '${email1}' AND  ride.isdeleted=0 AND personride_count < ride.quantity AND NOT EXISTS (SELECT * FROM personride WHERE personride.ride_id = ride.id AND personride.email = '${email1}')"

        val cursor = db.rawQuery(query, null)
        val adapter = LocationListAdapter(applicationContext, cursor,0,mMap,email,this)

        if (adapter.isEmpty()) {
            text1.text="No rides are available"
        }
        else{ text1.text=""}
        val count: Int = adapter.count

        Log.d("TAG", "Your $count")

        markerPairs=adapter.fillMarkerPairs()
        listView.adapter = adapter
        var initialLoad = true
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        if (!initialLoad) {
        val selectedItem = parent.getItemAtPosition(position) as String
        if (selectedItem == "None") {

            val query = "SELECT ride.id AS _id, ride.*, user.name, " +
                    "(SELECT COUNT(*) FROM personride WHERE personride.ride_id = ride.id) AS personride_count " +
                    "FROM ride " +
                    "JOIN user ON ride.email = user.email " +
                    "WHERE ride.email != '${email1}' AND  ride.isdeleted=0 AND personride_count < ride.quantity AND NOT EXISTS (SELECT * FROM personride WHERE personride.ride_id = ride.id AND personride.email = '${email1}')"
            val cursor = db.rawQuery(query, null)
            mMap.clear()
            val adapter = LocationListAdapter(applicationContext, cursor,0,mMap,email,this@MapActivity2)
            val count: Int = adapter.count
            if (adapter.isEmpty()) {
                text1.text="No rides are available"
            }
            else{ text1.text=""}
            Log.d("TAG", "Your $count")
            markerPairs=adapter.fillMarkerPairs()
            listView.adapter = adapter

        } else {

            val query = "SELECT ride.id AS _id, ride.*, user.name, " +
                    "(SELECT COUNT(*) FROM personride WHERE personride.ride_id = ride.id) AS personride_count " +
                    "FROM ride " +
                    "JOIN user ON ride.email = user.email " +
                    "WHERE ride.email != '${email1}' AND  ride.isdeleted=0 AND personride_count < ride.quantity AND (ride.StartCity = '${selectedItem}' OR ride.DestCity = '${selectedItem}')  AND NOT EXISTS (SELECT * FROM personride WHERE personride.ride_id = ride.id AND personride.email = '${email1}')"

            val cursor = db.rawQuery(query, null)
            mMap.clear()
            val adapter = LocationListAdapter(applicationContext, cursor,0,mMap,email,this@MapActivity2)
            val count: Int = adapter.count
            if (adapter.isEmpty()) {
                text1.text="No rides are available"
            }
            else{ text1.text=""}
            Log.d("TAG", "Your $count")
            markerPairs=adapter.fillMarkerPairs()
            listView.adapter = adapter
        }

        }
        initialLoad = false
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
    }
}

        mMap.setOnMarkerClickListener { marker ->
            val position =findMarkerIndex(marker, markerPairs)
            if (position != -1) {
                listView.smoothScrollToPosition(position)
                Log.d("TAG", "Your log message here")
            }
            Log.d("TAG", position.toString())
            false
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MapActivity.REQUEST_LOCATION_PERMISSION
            )
        } else {
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = true
            requestLocationUpdates()
        }

    }
    fun findMarkerIndex(marker: Marker, markerPairs: MutableList<Pair<Marker, Marker>>): Int {
        return markerPairs.indexOfFirst { it.first == marker || it.second == marker }
    }
    private fun requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MapActivity.REQUEST_LOCATION_PERMISSION
            )
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                val latitude = location?.latitude
                val longitude = location?.longitude

                currentLocation = LatLng(latitude ?: 0.0, longitude ?: 0.0)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 8f))
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MapActivity.REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates()
            }
        }
    }
    companion object {
        const val REQUEST_LOCATION_PERMISSION = 1

    }
}



