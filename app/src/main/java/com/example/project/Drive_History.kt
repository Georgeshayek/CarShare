package com.example.project

import android.database.Cursor
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Drive_History.newInstance] factory method to
 * create an instance of this fragment.
 */
class Drive_History : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var text1: TextView
    private lateinit var listView: ListView
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

        val rootView=inflater.inflate(R.layout.fragment_drive__history, container, false)
        text1=rootView.findViewById(R.id.text1)
        val dbHelper = DB_helper(requireContext())
        val db = dbHelper.readableDatabase

        val email1 = SharedPreferencesUtils.getEmail(requireContext())
        val query = "SELECT ride.id AS _id, ride.*, user.name, " +
                "(SELECT COUNT(*) FROM personride WHERE personride.ride_id = ride.id) AS personride_count " +
                "FROM ride " +
                "JOIN user ON ride.email = user.email " +
                "WHERE ride.email = '${email1}' AND  ride.isdeleted=1"

        val cursor: Cursor = db.rawQuery(query, null)
        cursor?.columnNames?.forEach { Log.d("Cursor Column", it) }
        listView = rootView.findViewById<ListView>(R.id.list_view)
        Log.d("ListFragment", "onCreateView: Setting adapter to ListView...")
        val drawable = resources.getDrawable(R.drawable.steeringwheel)
        val adapter = DriverCardHistory(requireContext(), cursor,0,drawable,0)

        listView.adapter = adapter
        if (adapter.isEmpty()) {
            text1.text="You have no rides book"
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
         * @return A new instance of fragment Drive_History.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Drive_History().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}