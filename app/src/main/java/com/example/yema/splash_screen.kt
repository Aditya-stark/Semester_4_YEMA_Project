package com.example.yema

//import MainActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import com.google.firebase.auth.FirebaseAuth


//THIS IS THE NEW SPLASH SCREEN PROVIDE AND SUGGESTED BY CHAT GPT TO CHECK THE USER LOGIN STATE
//Note by Aditya S
class splash_screen : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        enableEdgeToEdge()

        auth = FirebaseAuth.getInstance()

        //This helped me on the login state
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // User is signed in
                startActivity(Intent(this@splash_screen, MainActivity::class.java))
                finish() // Finish splash screen activity
            } else {
                // User is signed out
                startActivity(Intent(this@splash_screen, Login_page::class.java))
                finish() // Finish splash screen activity
            }
        }

        // Start listening for authentication state changes
        auth.addAuthStateListener(authStateListener)

        // Delayed handler is not necessary here, as we are waiting for the authentication state change
    }

    override fun onStop() {
        super.onStop()
        // Remove authStateListener when activity stops
        auth.removeAuthStateListener(authStateListener)
    }
}
