package com.example.yema

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge

class signup_page : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val signin_btn_hyper: TextView = findViewById<TextView>(R.id.sign_in_btn)

        signin_btn_hyper.setOnClickListener{
            startActivity(Intent(this, Login_page::class.java))
        }
    }
}