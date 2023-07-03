package com.example.project
import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.project.databinding.MapActivityBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import android.location.Location
import android.widget.AdapterView.OnItemSelectedListener;
import android.location.LocationListener
import android.location.LocationManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

class MapActivity: AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnMapClickListener{
    private var previousPolyline: Polyline? = null
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: MapActivityBinding
    private var currentLocation: LatLng = LatLng(0.0, 0.0)
    private var initmarker: Marker? = null
    private var marker: Marker? = null
    private var markerCount = 0

    private lateinit var bottomSheet: MyBottomSheetDialogFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MapActivityBinding.inflate(layoutInflater)
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
        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment1) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        val id= intent.getStringExtra("id")
        mMap = googleMap

        // Check if location permission is granted, if not request it
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                UpdateMap.REQUEST_LOCATION_PERMISSION
            )
        } else {
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = true
            requestLocationUpdates()
        }
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
        button3.setOnClickListener {
            finish()
        }
        button4.setOnClickListener {
            if (markerCount==2) {
                val email = SharedPreferencesUtils.getEmail(this)

                val latLng0 = initmarker?.position


                val latLng = marker?.position

                bottomSheet = MyBottomSheetDialogFragment(this, latLng0, latLng, email.toString())
                bottomSheet.show(supportFragmentManager, "bottom_sheet_tag")
            }
            else{
                Toast.makeText(this,"you have ${markerCount} on the map you need 2!", Toast.LENGTH_SHORT).show()
            }


        }



        mMap.setOnMapClickListener(this)
    }
    private fun requestLocationUpdates() {
        // Check if location permission is granted, if not request it
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                UpdateMap.REQUEST_LOCATION_PERMISSION
            )
        } else {
            // Permission is already granted, request location updates
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                // Retrieve the latitude and longitude from the location object
                val latitude = location?.latitude
                val longitude = location?.longitude

                // Store the current location in a LatLng object
                currentLocation = LatLng(latitude ?: 0.0, longitude ?: 0.0)

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
        if (requestCode == UpdateMap.REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                requestLocationUpdates()
            }
        }
    }
    override fun onMapClick(latLng: LatLng) {
        val id= intent.getStringExtra("id")


        if (markerCount == 0) {
            val vectorDrawable = VectorDrawableCompat.create(resources, R.drawable.ic_map, null)
            val bitmap = Bitmap.createBitmap(
                vectorDrawable!!.intrinsicWidth,
                vectorDrawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
            vectorDrawable.draw(canvas)
            initmarker = mMap.addMarker(MarkerOptions().position(latLng).title("Start Position").icon(BitmapDescriptorFactory.fromBitmap(bitmap)))
            markerCount++
        } else if (markerCount == 1) {
            val vectorDrawable1 = VectorDrawableCompat.create(resources, R.drawable.ic_maplocation2, null)
            val bitmap1 = Bitmap.createBitmap(
                vectorDrawable1!!.intrinsicWidth,
                vectorDrawable1.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas1 = Canvas(bitmap1)
            vectorDrawable1.setBounds(0, 0, canvas1.width, canvas1.height)
            vectorDrawable1.draw(canvas1)
            marker = mMap.addMarker(MarkerOptions().position(latLng).title("End Position").icon(BitmapDescriptorFactory.fromBitmap(bitmap1)))
            markerCount++
            val dotPattern = listOf(Dash(10f), Gap(10f))
            val lineOptions = PolylineOptions()
                .add(initmarker?.position, marker?.position)
                .width(5f)
                .color(Color.RED).pattern(dotPattern)
            previousPolyline = mMap.addPolyline(lineOptions)

        }

    }

companion object {
    const val REQUEST_LOCATION_PERMISSION = 1
}
}

    class MyBottomSheetDialogFragment (activity: Activity, Lang:LatLng?, Lang2:LatLng?, str:String) : BottomSheetDialogFragment() {
        private lateinit var button1: Button
        private lateinit var editText: EditText
        private lateinit var editText1: EditText
        private lateinit var hoursPicker:NumberPicker
        private lateinit var minutesPicker:NumberPicker
        private lateinit var daysPicker:NumberPicker
        private lateinit var monthPicker:NumberPicker
        private lateinit var yearPicker:NumberPicker
        private lateinit var spinner: Spinner
        private lateinit var city: String
        private lateinit var city1: String
        private val callingActivity = activity
        private var latLng:LatLng?=Lang
        private var latLng1:LatLng?=Lang2
        private var email:String=str
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view = inflater.inflate(R.layout.my_bottom_sheet_dialog, container, false)
            var dbhelp=DB_helper(requireContext())
            var db=dbhelp.writableDatabase
            spinner=view.findViewById(R.id.spinner1)
            hoursPicker = view.findViewById(R.id.hours_picker);
           minutesPicker = view.findViewById(R.id.minutes_picker);
            daysPicker = view.findViewById(R.id.day_picker);
            monthPicker = view.findViewById(R.id.month_picker);
           yearPicker = view.findViewById(R.id.year_picker);
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listOf("USD", "LBP"))
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            val currentDate = Calendar.getInstance()
            daysPicker.minValue = 1
            daysPicker.maxValue = currentDate.getActualMaximum(Calendar.DAY_OF_MONTH)
            daysPicker.value = currentDate.get(Calendar.DAY_OF_MONTH)

            monthPicker.minValue = 1
            monthPicker.maxValue = 12
            monthPicker.value = currentDate.get(Calendar.MONTH) + 1

            yearPicker.minValue = currentDate.get(Calendar.YEAR)
            yearPicker.maxValue = currentDate.get(Calendar.YEAR) + 2
            yearPicker.value = currentDate.get(Calendar.YEAR)


            hoursPicker.setMinValue(0);
            hoursPicker.setMaxValue(23);
            hoursPicker.value = currentDate.get(Calendar.HOUR_OF_DAY)

            minutesPicker.setMinValue(0);
            minutesPicker.setMaxValue(59);
            minutesPicker.value = currentDate.get(Calendar.MINUTE)

            val listener = object : NumberPicker.OnValueChangeListener {
                override fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int) {
                    val selectedDate = Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_MONTH, daysPicker.value)
                        set(Calendar.MONTH, monthPicker.value - 1)
                        set(Calendar.YEAR, yearPicker.value)
                        set(Calendar.HOUR_OF_DAY, hoursPicker.value)
                        set(Calendar.MINUTE, minutesPicker.value)
                    }

                    if (selectedDate.before(currentDate)) {

                        daysPicker.value = currentDate.get(Calendar.DAY_OF_MONTH)
                        monthPicker.value = currentDate.get(Calendar.MONTH) + 1
                        yearPicker.value = currentDate.get(Calendar.YEAR)
                        hoursPicker.value = currentDate.get(Calendar.HOUR_OF_DAY)
                        minutesPicker.value = currentDate.get(Calendar.MINUTE)
                    } else {

                        val maxDay = selectedDate.getActualMaximum(Calendar.DAY_OF_MONTH)
                        if (maxDay != daysPicker.maxValue) {
                            daysPicker.maxValue = maxDay
                        }
                    }
                }
            }

            daysPicker.setOnValueChangedListener(listener)
            monthPicker.setOnValueChangedListener(listener)
            yearPicker.setOnValueChangedListener(listener)
            hoursPicker.setOnValueChangedListener(listener)
            minutesPicker.setOnValueChangedListener(listener)

            editText=view.findViewById(R.id.input)
            editText1=view.findViewById(R.id.input1)
            button1=view.findViewById(R.id.bottom_sheet_button)
            spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                    val currentValue = editText1.text.toString().toIntOrNull() ?:0
                    val newValue = when (position) {
                        0 -> currentValue / 100000
                        1 -> currentValue * 100000
                        else -> currentValue
                    }
                    editText1.setText(newValue.toString())
                    val item = adapter.getItem(position)
                }
            }
            button1.setOnClickListener {
                val datetimeStr = String.format(
                    "%04d-%02d-%02d %02d:%02d:00",
                    yearPicker.value,
                    monthPicker.value,
                    daysPicker.value,
                    hoursPicker.value,
                    minutesPicker.value
                )
                val geocoder1 = Geocoder(requireContext(), Locale.getDefault())
                val addresses1 = geocoder1.getFromLocation(latLng!!.latitude, latLng!!.longitude, 1)

                if (addresses1?.isNotEmpty() == true) {
                    val address = addresses1[0]
                     city = address.locality

                }
                val geocoder2 = Geocoder(requireContext(), Locale.getDefault())
                val addresses2 = geocoder2.getFromLocation(latLng1!!.latitude, latLng1!!.longitude, 1)

                if (addresses2?.isNotEmpty() == true) {
                    val address = addresses2[0]
                    city1 = address.locality

                }
                val selectedValue = spinner.selectedItem.toString()
                var data = ContentValues()
                data.put("startlong", latLng!!.longitude)
                data.put("startlat", latLng!!.latitude)
                data.put("destlong", latLng1!!.longitude)
                data.put("destlat", latLng1!!.latitude)
                data.put("datetime", datetimeStr)
                data.put("quantity", editText.text.toString().toInt())
                data.put("price", editText1.text.toString().toInt())
                data.put("email", email)
                data.put("startcity", city)
                data.put("destcity", city1)
                data.put("isdeleted", 0)
                data.put("currency", selectedValue)
                var rs: Long = db.insert("ride", null, data)
                if (!rs.equals(-1)) {
                    Toast.makeText(requireContext(), "succesfully booked!", Toast.LENGTH_SHORT)
                        .show()
                    callingActivity.finish()
                } else {
                    Toast.makeText(requireContext(), "didnt work!", Toast.LENGTH_SHORT).show()

                }
            }

            return view
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }
