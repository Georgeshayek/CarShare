package com.example.project

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Setting.newInstance] factory method to
 * create an instance of this fragment.
 */
class Setting : Fragment() {
    private lateinit var listView: ListView
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var text1:TextView
    var thiscontext: Context? = null
    private lateinit var tmp: TextView
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

        if (container != null) {
            thiscontext = container.context!!
        };

        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_setting, container, false)
        text1=rootView.findViewById(R.id.text1)
        val dbHelper = DB_helper(requireContext())
        val db = dbHelper.readableDatabase

        val email1 = SharedPreferencesUtils.getEmail(requireContext())
        val query = "SELECT ride.id AS _id, ride.*, user.name, " +
                "(SELECT COUNT(*) FROM personride WHERE personride.ride_id = ride.id) AS personride_count " +
                "FROM ride " +
                "JOIN user ON ride.email = user.email " +
                "WHERE ride.email = '${email1}' AND  ride.isdeleted=0"

        val cursor: Cursor = db.rawQuery(query, null)

         listView = rootView.findViewById<ListView>(R.id.list_view)

        val drawable = resources.getDrawable(R.drawable.steeringwheel)
        val adapter = DriverCard(requireContext(), cursor,0,drawable,0)

        listView.adapter = adapter
        if (adapter.isEmpty()) {
            text1.text="You have no rides book"
        }
        return  rootView
    }

    @SuppressLint("Range")
    override fun onResume() {
        super.onResume()
        val dbHelper = DB_helper(requireContext())
        val db = dbHelper.readableDatabase
        val email = SharedPreferencesUtils.getEmail(requireContext())
        val query = "SELECT ride.id AS _id, ride.*, user.name, " +
                "(SELECT COUNT(*) FROM personride WHERE personride.ride_id = ride.id) AS personride_count " +
                "FROM ride " +
                "JOIN user ON ride.email = user.email " +
                "WHERE ride.email = '${email}' AND ride.isdeleted=0"
        val cursor: Cursor = db.rawQuery(query, null)

        val drawable = resources.getDrawable(R.drawable.steeringwheel)
        val adapter = DriverCard(requireContext(), cursor,0,drawable,0)
        listView.adapter = adapter
        if (adapter.isEmpty()) {
            text1.text="You have no rides book"
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Settings.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Setting().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
