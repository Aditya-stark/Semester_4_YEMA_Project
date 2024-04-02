package com.example.yema

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.yema.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn // Import GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions // Import GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView

/*
* Changes:
* ~ Depreciated method fix for bottom nav bar setOnItemSelectedListener() instead of setOnNavigationItemSelectedListener()
* ~ Unnecessary comment removal from the switch-case (when) => Code Cleanup
* */
interface SignOutListener {
    fun onSignOut()
}
class MainActivity : AppCompatActivity(), SignOutListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth // Declare FirebaseAuth
    public lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance() // Initialize FirebaseAuth


        val profileFragment = ProfileFragment()

        // Pass the activity as the sign-out listener
        profileFragment.setSignOutListener(this)

        // Set up bottom navigation
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Loading the HomeFragment First (Default)
        bottomNavigationView.selectedItemId = R.id.home
        loadFragment(HomeFragment())

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.transaction -> {
                    loadFragment(TransactionsFragment())
                    true
                }
                R.id.budget -> {
                    loadFragment(BudgetFragment())
                    true
                }

                R.id.profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }

        // For the color and text of icons
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

    Toast.makeText(this,  "Logged out successfully", Toast.LENGTH_SHORT).show()
    startActivity(Intent(this, Login_page::class.java))
    finish()
}

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }
}