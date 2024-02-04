package com.example.yema

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.yema.databinding.ActivityMainBinding
import com.example.yema.databinding.ActivitySignUpBinding

class MainActivity : AppCompatActivity() {
    //Here cant find the ActivitySignOutBinding
    //private lateinit var binding: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}