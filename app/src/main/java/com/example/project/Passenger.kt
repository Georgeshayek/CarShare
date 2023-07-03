package com.example.project

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.example.project.databinding.DriverLayoutBinding
import com.example.project.databinding.PassenLayoutBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class Passenger : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: PassenLayoutBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private var previousPolyline: Polyline? = null
    private var initmarker: Marker? = null
    private var marker: Marker? = null
    @SuppressLint("Range", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PassenLayoutBinding.inflate(layoutInflater)
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
    @SuppressLint("Range", "MissingInflatedId")
    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        val dbHelper = DB_helper(applicationContext)
        val db = dbHelper.readableDatabase
        val id= intent.getStringExtra("id")
        val linearLayout = findViewById<LinearLayout>(R.id.linear_layout)
        val linearLayout1 = findViewById<LinearLayout>(R.id.linear_layout1)
        val query = "SELECT ride.id AS _id, ride.*, user.name,user.img " +
                "FROM ride " +
                "JOIN user ON ride.email = user.email " +
                "WHERE ride.id=$id"

        val query1 = "SELECT user.*,personride.id FROM user " +
                "JOIN personride ON user.email = personride.email " +
                "WHERE personride.ride_id = $id"
        val cursor: Cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val listItem = layoutInflater.inflate(R.layout.passenger1_layout, linearLayout, false)

            val textView1 = listItem.findViewById<TextView>(R.id.text_view_1)
            textView1.text = cursor.getString(cursor.getColumnIndex("name"))


            val tmp= cursor.getString(cursor.getColumnIndex("email"))
            val image=listItem.findViewById<ImageView>(R.id.profile_image)
            val image1=cursor.getString(cursor.getColumnIndex("img"))
            if(!image1.isNullOrEmpty()){
                val bitmap = BitmapFactory.decodeFile(image1)
                image.setImageBitmap(bitmap)
            }
            // Add other views as needed for your data
            val button=listItem.findViewById<ImageView>(R.id.imag2)
            button.setOnClickListener {
                val intent = Intent(this@Passenger, UserCard::class.java).putExtra("email",tmp)
                startActivity(intent)
            }


            linearLayout.addView(listItem)
        }
        cursor.close()
        val cursor1: Cursor = db.rawQuery(query1, null)
        while (cursor1.moveToNext()) {
            val listItem = layoutInflater.inflate(R.layout.passenger1_layout, linearLayout1, false)
            val image=listItem.findViewById<ImageView>(R.id.profile_image)
            val image1=cursor1.getString(cursor1.getColumnIndex("img"))
            if(!image1.isNullOrEmpty()){
                val bitmap = BitmapFactory.decodeFile(image1)
                image.setImageBitmap(bitmap)
            }
            val textView1 = listItem.findViewById<TextView>(R.id.text_view_1)
            val id1=cursor1.getInt(cursor1.getColumnIndex("id"))
            textView1.text = cursor1.getString(cursor1.getColumnIndex("name"))


            val tmp= cursor1.getString(cursor1.getColumnIndex("email"))

            val button=listItem.findViewById<ImageView>(R.id.imag2)
            button.setOnClickListener {
                val intent = Intent(this@Passenger, UserCard::class.java).putExtra("email",tmp)
                startActivity(intent)
            }
            if(tmp==SharedPreferencesUtils.getEmail(this).toString()){


                textView1.text="You"
                button.isEnabled = false
                button.visibility = View.INVISIBLE
            }

            linearLayout1.addView(listItem)
        }
        cursor1.close()




        val query2 = "SELECT ride.id AS _id, ride.* " +
                "FROM ride " +
                "WHERE ride.id=$id"
        val cursor2= db.rawQuery(query2, null)
        if (cursor2.moveToFirst()) {
            val boundsBuilder = LatLngBounds.Builder()

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

        val button: Button =findViewById(R.id.deleteitem)
        button.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to cancel this ride?")
            builder.setPositiveButton("Yes") { dialog, which ->
                val dbHelper = DB_helper(this)
                val db = dbHelper.writableDatabase
                val email = SharedPreferencesUtils.getEmail(this)
                val tableName = "personride"
                db.delete(tableName, "ride_id = ? AND email = ?", arrayOf(id.toString(),email.toString()))

                val toast = Toast.makeText(this,"deleted", Toast.LENGTH_SHORT)
                toast.show()
                finish()
            }
            builder.setNegativeButton("No") { dialog, which ->

            }
            val dialog = builder.create()
            dialog.show()

        }


    }
}