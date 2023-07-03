package com.example.project

import android.R.id
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity


class EditProfileActivity: AppCompatActivity()  {
    private lateinit  var value: String
    private lateinit  var Name: EditText
    private lateinit  var password: EditText
    private lateinit  var phone: EditText
    lateinit var passwordValue: String
    lateinit var usernameValue:String
    lateinit var phoneValue:String
    private lateinit  var applyButton: Button
    private lateinit  var cancelButton: Button
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editprofile_activity)
        val toolbar = findViewById<RelativeLayout>(R.id.toolbar)
        toolbar.findViewById<ImageButton>(R.id.back_button)?.setOnClickListener {
            finish()}
        value= SharedPreferencesUtils.getEmail(this).toString()
        Name=findViewById(R.id.name2)
        phone=findViewById(R.id.phone2)
        password=findViewById(R.id.pswd2)
        applyButton=findViewById(R.id.buttonapply)
        cancelButton=findViewById(R.id.buttoncancel)
        var dbhelp=DB_helper(applicationContext)
        var db=dbhelp.readableDatabase
        val query="SELECT * FROM user WHERE email='"+value+"'"
        val rs=db.rawQuery(query,null)
        rs.moveToFirst()
        Name.setText(rs.getString(rs.getColumnIndex("name")))
        phone.setText(rs.getString(rs.getColumnIndex("phone")))
        password.setText(rs.getString(rs.getColumnIndex("pswd")))
        cancelButton.setOnClickListener { finish() }
        applyButton.setOnClickListener {
            usernameValue=Name.text.toString()
            passwordValue=password.text.toString()
            phoneValue=phone.text.toString()
            if(usernameValue.isNotEmpty() &&  passwordValue.isNotEmpty() &&phoneValue.isNotEmpty()) {
                var data = ContentValues()
                data.put("name", usernameValue)
                data.put("phone", phoneValue.toInt())
                data.put("pswd", passwordValue)
                val where = "email=?"
                val whereArgs = arrayOf<String>(java.lang.String.valueOf(value))
                try {
                    db.update("user", data, where, whereArgs)
                    Toast.makeText(this,"succesfully updated!", Toast.LENGTH_SHORT).show()

                    finish()
                } catch (e: Exception) {
                    val error = e.message.toString()
                    Toast.makeText(this,error, Toast.LENGTH_SHORT).show()
                }

            }else{
                Toast.makeText(this,"All fields required", Toast.LENGTH_SHORT).show()
            }
        }

    }


}