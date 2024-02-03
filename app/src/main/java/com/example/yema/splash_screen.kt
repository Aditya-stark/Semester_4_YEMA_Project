package com.example.yema

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.core.os.postDelayed

class splash_screen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        enableEdgeToEdge()
        Handler(Looper.getMainLooper()).postDelayed(1000){
            startActivity(Intent(this, Login_page::class.java))
            finish()
        }

    }
}