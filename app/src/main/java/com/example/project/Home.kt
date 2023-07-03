package com.example.project
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.smarteist.autoimageslider.SliderView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home : Fragment() {
    // on below line we are creating a variable
    // for our array list for storing our images.
    lateinit var imageUrl: ArrayList<String>

    // on below line we are creating
    // a variable for our slider view.




    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var Name: TextView
    private lateinit var Book:Button
    private lateinit var Book1:Button
    var thiscontext: Context? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView =  inflater.inflate(R.layout.fragment_home, container, false)

        val message = SharedPreferencesUtils.getEmail(requireContext())
        Book=rootView.findViewById(R.id.book1)
        Book1=rootView.findViewById(R.id.book2)
        if (container != null) {
            thiscontext = container.context!!
        };
        val image = rootView.findViewById<ImageSlider>(R.id.carousel)
        val slide: MutableList<SlideModel> = java.util.ArrayList()
        slide.add(SlideModel(R.drawable.slider1, ScaleTypes.FIT))
        slide.add(SlideModel(R.drawable.taxi02, ScaleTypes.FIT))
        slide.add(SlideModel(R.drawable.slider2, ScaleTypes.FIT))
        image.setImageList(slide)

        Book.setOnClickListener {
            val intent = Intent(thiscontext, MapActivity::class.java).putExtra("email",message)
            startActivity(intent)}
        Book1.setOnClickListener {
            val intent = Intent(thiscontext, MapActivity2::class.java).putExtra("email",message)
            startActivity(intent)}
        return rootView
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    override fun onResume() {
        super.onResume()
        val currentDateTime = getCurrentDateTime()
        val dbHelper = DB_helper(requireContext())
        val db = dbHelper.writableDatabase
        val query = "SELECT * FROM ride"
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val dateTime = cursor.getString(cursor.getColumnIndexOrThrow("datetime"))

                // Compare the current datetime with the stored datetime
                val isDateTimePassed = compareDateTime(currentDateTime, dateTime)

                if (isDateTimePassed) {
                    var data = ContentValues()

                    data.put("isdeleted", 1)
                    // Delete the row from the "ride" table where datetime matches the given value
                    val deleteResult = db.update("ride", data,"datetime = ?", arrayOf(dateTime))

//                    if (deleteResult > 0) {
//                        // Row deleted successfully
//                        Toast.makeText(context, "Row deleted", Toast.LENGTH_SHORT).show()
//                    } else {
//                        // Deletion failed or no row matched the condition
//                        Toast.makeText(context, "Deletion failed", Toast.LENGTH_SHORT).show()
//                    }
                }
            } while (cursor.moveToNext())
        }


        // Function to get the current date and time in the format "yyyy-MM-dd HH:mm:ss"

    }
    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    // Function to compare two datetime values
    private fun compareDateTime(dateTime1: String, dateTime2: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date1 = dateFormat.parse(dateTime1)
        val date2 = dateFormat.parse(dateTime2)
        return date1.after(date2)
    }
}
