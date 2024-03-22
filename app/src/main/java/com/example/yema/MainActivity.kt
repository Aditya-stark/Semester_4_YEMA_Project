package com.example.yema

import ProfileFragment
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.yema.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn // Import GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions // Import GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

interface SignOutListener {
    fun onSignOut()
}
class MainActivity : AppCompatActivity(), SignOutListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth // Declare FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance() // Initialize FirebaseAuth


        val profileFragment = ProfileFragment()

        // Pass the activity as the sign-out listener
        profileFragment.setSignOutListener(this)

//        binding.mainSign.setOnClickListener {
//            // Sign out from Firebase Authentication
//            auth.signOut()
//
//            // Sign out from Google Authentication
//            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build()
//            val googleSignInClient = GoogleSignIn.getClient(this, gso)
//            googleSignInClient.signOut()
//
//            Toast.makeText(this, "Done Logout", Toast.LENGTH_LONG).show()
//
//            startActivity(Intent(this, Login_page::class.java))
//            finish()// Finish MainActivity after signing out
//        }

        // Set up bottom navigation
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
//            LOADING FRAGMENTS
            when (item.itemId) {
                R.id.home -> {
                    // Handle Home selection
//                    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                    loadFragment(HomeFragment())
                    true
                }
                R.id.transaction -> {
                    // Handle Transactions selection
//                    Toast.makeText(this, "Transaction", Toast.LENGTH_SHORT).show()
                    loadFragment(TransactionsFragment())
                    true
                }
                R.id.budget -> {
                    // Handle Budget selection
//                    Toast.makeText(this, "Budget", Toast.LENGTH_SHORT).show()
                    loadFragment(BudgetFragment())
                    true
                }

                R.id.profile -> {
                    // Handle Profile selection
//                    Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }

        // Select the "Home" item by default
        bottomNavigationView.selectedItemId = R.id.home

        loadFragment(HomeFragment())

        //For the color and text of icons
        val iconColors = ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf(-android.R.attr.state_checked)),
            intArrayOf(ContextCompat.getColor(this, R.color.Main_Blue), ContextCompat.getColor(this, R.color.iconGrey))
        )
        bottomNavigationView.itemIconTintList = iconColors

        // Set text color selector
        val textColors = ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf(-android.R.attr.state_checked)),
            intArrayOf(ContextCompat.getColor(this, R.color.Main_Blue), ContextCompat.getColor(this, R.color.iconGrey))
        )
        bottomNavigationView.itemTextColor = textColors


    }


//    FUNCTION FOR LOADING THE FRAGMENT
override fun onSignOut() {
    auth.signOut()
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(this, gso)
    googleSignInClient.signOut()

    Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
    startActivity(Intent(this, Login_page::class.java))
    finish()
}

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }



}