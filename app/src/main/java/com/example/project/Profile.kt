package com.example.project


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.app.AlertDialog
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class Profile : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var filepath: String? = null
    private lateinit var Picture: ImageView

    private lateinit var Name: TextView
    private lateinit var email: TextView
    private lateinit var password: TextView
    private lateinit var editprof: Button
    private lateinit var changePhoto: Button
    private lateinit var logout: Button
    private lateinit var history: Button
    private val pickImage = 100

    private var imageUri: Uri? = null
    var thiscontext: Context? = null
    private val REQUEST_IMAGE_CAPTURE = 1

    private val REQUEST_CODE_SELECT_IMAGE = 1001
    private val REQUEST_CODE_READ_EXTERNAL_STORAGE = 1002
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("Range", "MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView =inflater.inflate(R.layout.fragment_profile, container, false)
        val listView = rootView.findViewById<ListView>(R.id.list_view)
        val items = arrayOf("Profile", "History", "About Us", "Logout")

        val adapter = SettingAdapter(requireContext(), R.layout.setting_listitem, items)
        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->
            // Handle item click based on position
            when (position) {
                0 -> {
                    val intent = Intent (requireContext(), ProfileSet::class.java)
                    startActivity(intent)
                }
                1 -> {
                    val intent = Intent (requireContext(), History::class.java)
                    startActivity(intent)
                }
                2 -> {
                    val intent = Intent (requireContext(), AboutUs::class.java)
                    startActivity(intent)
                }
                3 -> {

                        val builder = AlertDialog.Builder(requireContext())
                        builder.setMessage("Are you sure you want to logout?")
                        builder.setPositiveButton("Yes") { dialog, which ->
                            SharedPreferencesUtils.clearEmail(requireContext())
                            val activity = activity as HomeActivity
                            activity.finish()
                            val intent = Intent (requireContext(), MainActivity::class.java)
                            startActivity(intent)
                        }
                        builder.setNegativeButton("No") { dialog, which ->

                        }
                        val dialog = builder.create()
                        dialog.show()



                }
            }
        }

        return rootView
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Profile.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Profile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}