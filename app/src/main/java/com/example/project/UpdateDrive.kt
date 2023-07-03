package com.example.project

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.util.*

class UpdateDrive: AppCompatActivity()  {


    private lateinit var button1: Button
    private lateinit var editText: EditText
    private lateinit var editText1: EditText
    private lateinit var hoursPicker: NumberPicker
    private lateinit var minutesPicker: NumberPicker
    private lateinit var daysPicker: NumberPicker
    private lateinit var monthPicker: NumberPicker
    private lateinit var yearPicker: NumberPicker
    private lateinit var spinner: Spinner
    // Set up the hours picker



    @SuppressLint("MissingInflatedId", "Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update_ride)
        val toolbar = findViewById<RelativeLayout>(R.id.toolbar)
        toolbar.findViewById<ImageButton>(R.id.back_button)?.setOnClickListener {
            finish()}
        val id= intent.getStringExtra("id").toString()
        var dbhelp=DB_helper(this)
        var db=dbhelp.writableDatabase

        var db1=dbhelp.readableDatabase
        val query="SELECT * ,(SELECT COUNT(*) FROM personride WHERE personride.ride_id = $id) AS personride_count FROM ride WHERE id='"+id+"'"
        val rs=db1.rawQuery(query,null)
        rs.moveToFirst()
        spinner=findViewById(R.id.spinner1)
        hoursPicker = findViewById(R.id.hours_picker);
        minutesPicker = findViewById(R.id.minutes_picker);
        daysPicker = findViewById(R.id.day_picker);
        monthPicker = findViewById(R.id.month_picker);
        yearPicker = findViewById(R.id.year_picker);
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("USD", "LBP"))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        val position = adapter.getPosition(rs.getString(rs.getColumnIndex("currency")))
        spinner.setSelection(position)
        val spots=rs.getInt(rs.getColumnIndex("personride_count"))

        val dateString:String=rs.getString(rs.getColumnIndex("datetime"))
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = format.parse(dateString)

        val calendar = Calendar.getInstance()
        calendar.time = date

        val minutes = calendar.get(Calendar.MINUTE)
        val hours = calendar.get(Calendar.HOUR_OF_DAY)
        val days = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1 // add 1 because Calendar.MONTH is zero-based
        val year = calendar.get(Calendar.YEAR)
        val currentDate = Calendar.getInstance()
        daysPicker.setMinValue(1)
        daysPicker.setMaxValue(31)
        daysPicker.value = days

        monthPicker.setMinValue(1)
        monthPicker.setMaxValue(12)
        monthPicker.value=month

        yearPicker.setMinValue(2023)
        yearPicker.setMaxValue(2025)
        yearPicker.value=year




        hoursPicker.setMinValue(0);
        hoursPicker.setMaxValue(23);
        hoursPicker.value=hours

// Set up the minutes picker
        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(59);
        minutesPicker.value=minutes
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


        editText=findViewById(R.id.input)
        editText1=findViewById(R.id.input1)
        editText.setText(rs.getString(rs.getColumnIndex("quantity")))
        editText1.setText(rs.getString(rs.getColumnIndex("price")))

        button1=findViewById(R.id.bottom_sheet_button)
        var initialLoad = true
        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                if (!initialLoad) {
                    val currentValue = editText1.text.toString().toIntOrNull()
                        ?: 0
                    val newValue = when (position) {
                        0 -> currentValue / 100000
                        1 -> currentValue * 100000
                        else -> currentValue
                    }
                    editText1.setText(newValue.toString())
                }
                initialLoad = false
                val item = adapter.getItem(position)
            }
        }

        button1.setOnClickListener {
            if(editText.text.toString().toInt()<spots)
            {
                Toast.makeText(this,"You cant do that you already have $spots occupied!", Toast.LENGTH_SHORT).show()
            }
            else{
            val datetimeStr = String.format(
                "%04d-%02d-%02d %02d:%02d:00",
                yearPicker.value,
                monthPicker.value,
                daysPicker.value,
                hoursPicker.value,
                minutesPicker.value
            )
            val selectedValue = spinner.selectedItem.toString()
            var data = ContentValues()

            data.put("datetime", datetimeStr)
            data.put("quantity", editText.text.toString().toInt())
            data.put("price", editText1.text.toString().toInt())
            data.put("currency", selectedValue)
            val where = "id=?"
            val whereArgs = arrayOf<String>(java.lang.String.valueOf(id))
            try {
                db.update("ride", data, where, whereArgs)
                Toast.makeText(this,"succesfully updated up!", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                val error = e.message.toString()
                Toast.makeText(this,error, Toast.LENGTH_SHORT).show()
            }}
        }

    }

}