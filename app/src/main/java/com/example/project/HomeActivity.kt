package com.example.project
import android.annotation.SuppressLint
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.project.databinding.HomeActivityBinding
import android.provider.Settings
import android.widget.Toast

class HomeActivity : AppCompatActivity() {
    private lateinit var binding :HomeActivityBinding
    private lateinit  var value: String
    private lateinit var locationManager: LocationManager

    override fun onResume() {
        super.onResume()
        checkLocationSettings()
    }

    private fun checkLocationSettings() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val builder = AlertDialog.Builder(this)
            builder.apply {
                setTitle("Enable Location")
                setMessage("Please enable location to use this app.")
                setPositiveButton("Settings") { _, _ ->
                    // Open the location settings screen
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
                setNegativeButton("Cancel") { _, _ ->
                    val toast = Toast.makeText(applicationContext, "cannot access up without enabling location", Toast.LENGTH_SHORT)
                    toast.show()
                    finish()
                }
            }
            builder.create().show()
        }
    }
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
         value= intent.getStringExtra("email").toString()

        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
        supportActionBar?.setCustomView(R.layout.custom_action_bar)
        binding= HomeActivityBinding.inflate(layoutInflater)
//        val inflater = LayoutInflater.from(this)
//        val view = inflater.inflate(R.layout.home_activity, null)
             setContentView(binding.root)

        replaceFragment(Home())
     binding.BottomNav.setOnItemSelectedListener{
            when(it.itemId){
                R.id.home->replaceFragment(Home())
                R.id.profile->replaceFragment(Profile())
                R.id.ride->replaceFragment(Ride())
                R.id.settings->replaceFragment(Setting())
                else ->{}
            }
            true
        }
    }
    @SuppressLint("CommitTransaction")
    private fun replaceFragment(fragment: Fragment){
        val mBundle = Bundle()
        mBundle.putString("email",value)
        val fragmentManager=supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()
        fragment.arguments=mBundle
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.addToBackStack(null).commit()
    }
}