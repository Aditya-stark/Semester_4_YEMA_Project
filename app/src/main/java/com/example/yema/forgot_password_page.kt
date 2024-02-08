package com.example.yema


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class forgot_password_page : AppCompatActivity() {

    private lateinit var resetBtn : Button
    private lateinit var email: EditText
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password_page)


//    INTENTS
        val backToSignin = findViewById<TextView>(R.id.forgotPwd_SignIn)
        val backToSignup = findViewById<TextView>(R.id.forgotPwd_SignUp)

        backToSignin.setOnClickListener {
            startActivity(Intent(this, Login_page::class.java))
            finish()
        }
        backToSignup.setOnClickListener {
            startActivity(Intent(this, signup_page::class.java))
            finish()
        }

        //    MAIN CODE FOR FORGOT PASSWORD
        email = findViewById(R.id.forgotPsd_email)
        resetBtn = findViewById(R.id.forgotPsdReset_btn)

        auth = FirebaseAuth.getInstance()

        resetBtn.setOnClickListener {
        if (email.text.toString().trim().isNotEmpty()) {
                val sPassword = email.text.toString()
                auth.sendPasswordResetEmail(sPassword)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Check Email", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, Login_page::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                    }
            }

            else{
                Toast.makeText(this, "Email Can't be Empty", Toast.LENGTH_SHORT).show()
            }
        }
    }



    }



