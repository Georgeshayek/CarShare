package com.example.project

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext

class MainActivity : AppCompatActivity() {
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var emailValue: String
    lateinit var passwordValue: String
    lateinit var login: Button

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {

        var dbhelp=DB_helper(applicationContext)
        var db=dbhelp.readableDatabase

        super.onCreate(savedInstanceState)
        val checker:Boolean=SharedPreferencesUtils.checkEmail(this)

     if(checker==false){
        setContentView(R.layout.activity_main)
        val signupTextview = findViewById<TextView>(R.id.textView6)
        print(signupTextview.text)
        email=findViewById(R.id.editTextTextEmailAddress)
        password=findViewById(R.id.editTextTextPassword)
        login=findViewById(R.id.button)
        login.setOnClickListener {
            emailValue= email.text.toString()
            passwordValue=password.text.toString()
            println(emailValue)
            println(passwordValue)
            val query="SELECT * FROM user WHERE email='"+emailValue+"' AND pswd='"+passwordValue+"'"
            val rs=db.rawQuery(query,null)
            if(rs.moveToFirst()){
                val toast = Toast.makeText(applicationContext, "Logged In", Toast.LENGTH_SHORT)
                toast.show()
                val email=rs.getString(rs.getColumnIndex("email"))
                SharedPreferencesUtils.saveEmail(this, email)
                finish()
                val intent = Intent(this@MainActivity, HomeActivity::class.java)
                startActivity(intent)


            }
            else{  val toast = Toast.makeText(applicationContext, "Invalid credentials", Toast.LENGTH_SHORT)
                toast.show()}
        }

        signupTextview.setOnClickListener {
            val intent = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
       }
    else{
            finish()
            val intent = Intent(this@MainActivity, HomeActivity::class.java)
            startActivity(intent)
    }
    }
    }

