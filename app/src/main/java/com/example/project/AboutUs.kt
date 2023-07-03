package com.example.project

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageButton
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity

class AboutUs: AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aboutus_layout)
        val toolbar = findViewById<RelativeLayout>(R.id.toolbar)
        toolbar.findViewById<ImageButton>(R.id.back_button)?.setOnClickListener {
            finish()
        }
    }

}