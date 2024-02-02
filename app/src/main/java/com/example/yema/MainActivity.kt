package com.example.yema

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById<Button>(R.id.goto_next)
        val intent = Intent(this, TestScreen::class.java)
        btn.setOnClickListener(View.OnClickListener {
            startActivity(intent)
        })
    }
}