package com.example.project

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class UserCard:AppCompatActivity(){
    @SuppressLint("Range", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.userinfo_layout)
        val toolbar = findViewById<RelativeLayout>(R.id.toolbar)
        toolbar.findViewById<ImageButton>(R.id.back_button)?.setOnClickListener {
            finish()
        }
        val email= intent.getStringExtra("email")
        val picture=findViewById<ImageView>(R.id.profile_image)
        val emailText=findViewById<TextView>(R.id.email_textview)
        val phoneText=findViewById<TextView>(R.id.phone_textview)
        val usernameText=findViewById<TextView>(R.id.name_textview)
        val button=findViewById<ImageView>(R.id.phone1)
        val button1=findViewById<ImageView>(R.id.email1)
        val button2=findViewById<Button>(R.id.finish1)
        var dbhelp=DB_helper(applicationContext)
        var db=dbhelp.readableDatabase
        val query="SELECT * FROM user WHERE email='"+email+"'"
        val rs=db.rawQuery(query,null)
        rs.moveToFirst()

        val image1=rs.getString(rs.getColumnIndex("img"))
        if(!image1.isNullOrEmpty()){
            val bitmap = BitmapFactory.decodeFile(image1)
            picture.setImageBitmap(bitmap)
        }
        usernameText.text = rs.getString(rs.getColumnIndex("name"))
        emailText.text = rs.getString(rs.getColumnIndex("email"))
        phoneText.text = rs.getString(rs.getColumnIndex("phone"))
        button.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:${phoneText.text}")
            startActivity(dialIntent)
        }
        button1.setOnClickListener { val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, "Car Share")}
            startActivity(Intent.createChooser(emailIntent, "Send email..."))
        }
        button2.setOnClickListener { finish() }
    }

}