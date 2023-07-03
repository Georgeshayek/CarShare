package com.example.project

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity: AppCompatActivity() {
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var phone: EditText
    lateinit var name:EditText
    lateinit var emailValue: String
    lateinit var phonevalue: String
    lateinit var passwordValue: String
    lateinit var usernameValue:String
    lateinit var register: Button
//    lateinit : TextView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        // val SignupTextview = findViewById<TextView>(R.id.textView6)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_layout)
        var dbhelp=DB_helper(applicationContext)
        var db=dbhelp.writableDatabase
    var signinTextView = findViewById<TextView>(R.id.Signin1)
        name=findViewById(R.id.editTextTextPersonName)
        email=findViewById(R.id.editTextTextEmailAddress2)
        password=findViewById(R.id.editTextTextPassword2)
        phone=findViewById(R.id.editTextphonenumber)
        register=findViewById(R.id.button1)
        register.setOnClickListener {
            usernameValue=name.text.toString()
            emailValue=email.text.toString()
            passwordValue=password.text.toString()
            phonevalue=phone.text.toString()
            if(usernameValue.isNotEmpty() && emailValue.isNotEmpty() && passwordValue.isNotEmpty()&& phonevalue.isNotEmpty()) {
                if (isEmailExists(emailValue)) {
                    Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show()
                }
                else {
                    var data = ContentValues()
                    data.put("name", usernameValue)
                    data.put("email", emailValue)
                    data.put("pswd", passwordValue)
                    data.put("phone", phonevalue.toInt())
                    var rs: Long = db.insert("user", null, data)
                    if (!rs.equals(-1)) {
                        Toast.makeText(this, "succesfully signed up!", Toast.LENGTH_SHORT).show()
                        finish()
                        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "didnt work!", Toast.LENGTH_SHORT).show()
                        name.text.clear()
                        email.text.clear()
                        password.text.clear()
                        phone.text.clear()
                    }
                }
            }else{
                Toast.makeText(this,"All fields required", Toast.LENGTH_SHORT).show()
            }

        }
        signinTextView.setOnClickListener {
            finish()
        }
        }
    private fun isEmailExists(email: String): Boolean {
        var dbhelp=DB_helper(applicationContext)
        val db = dbhelp.readableDatabase
        val query = "SELECT * FROM user WHERE email = ?"
        val cursor = db.rawQuery(query, arrayOf(email))
        val emailExists = cursor.count > 0
        cursor.close()
        return emailExists
    }
}