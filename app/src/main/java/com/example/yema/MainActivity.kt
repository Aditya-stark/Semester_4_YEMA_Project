package com.example.yema

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.yema.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn // Import GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions // Import GoogleSignInOptions

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth // Declare FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance() // Initialize FirebaseAuth

        binding.mainSign.setOnClickListener {
            // Sign out from Firebase Authentication
            auth.signOut()

            // Sign out from Google Authentication
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(this, gso)
            googleSignInClient.signOut()

            Toast.makeText(this, "Done Logout", Toast.LENGTH_LONG).show()

            startActivity(Intent(this, Login_page::class.java))
            finish()// Finish MainActivity after signing out
        }
    }
}
