package com.example.yema

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Calendar

/*
* Changes (Code Cleanup, Typo Fix):
* ~ Removed the not so "necessary" functions => onViewCreated(), onCreate()
* ~ Merged every code into onCreateView()
* */

class HomeFragment : Fragment() {
    private lateinit var profileImageView: CircleImageView
    private lateinit var profileImageDownloadUri: Uri
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_home, container, false)

        // Circular Image Initialization
        profileImageView = view.findViewById(R.id.profile_image_home_fragment)

        // Checking for the providers to load the profile accordingly
        val providerPreferences =
            requireActivity().getSharedPreferences("login_provider", Context.MODE_PRIVATE)
        val PROVIDER_ID = providerPreferences.getString("PROVIDER_ID", "")

        when (PROVIDER_ID) {
            "google.com" -> loadGoogleProfile()
            "firebase_auth" -> loadFirebaseProfile()
            "" -> {
                Log.d("TAG", "Invalid Provider")
                Toast.makeText(requireActivity(), "Invalid Login", Toast.LENGTH_SHORT).show()
            }

            else -> Toast.makeText(requireActivity(), "Invalid Login", Toast.LENGTH_SHORT).show()
        }

        // Spinner Initialization
//        monthSpinner = view.findViewById(R.id.monthSpinner)
//        ArrayAdapter.createFromResource(
//            requireContext(),
//            R.array.months_array,
//            android.R.layout.simple_spinner_item
//        ).also { adapter ->
//            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_month)
//            monthSpinner.adapter = adapter
//
//            // Set default selection to the current month
//            val currentMonthIndex = getCurrentMonthIndex()
//            monthSpinner.setSelection(currentMonthIndex)
//        }


        // FOR REPLACE THE FRAGMENT OF THE INCOME BUTTON
        val incomeLinearLayoutButton = view.findViewById<LinearLayout>(R.id.incomeBtn)
        incomeLinearLayoutButton.setOnClickListener {
            val fragment = IncomeAdd()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.homeFragment, fragment)
            fragmentTransaction.addToBackStack(null)  // Optional: Add to back stack
            fragmentTransaction.commit()
        }

        val expensesLinearLayoutButton = view.findViewById<LinearLayout>(R.id.expensesBtn)
        expensesLinearLayoutButton.setOnClickListener{
            val fragment = ExpensesAdd()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.homeFragment, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        val profileCircleBtn = view.findViewById<View>(R.id.profile_image_home_fragment)
        profileCircleBtn.setOnClickListener{
            val fragment = ProfileFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.homeFragment, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

        }

        return view
    }

    // Changes authored by @Penguin5681 => start
    private fun loadFirebaseProfile() {
        var currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

        if (currentUserEmail != null) {
            currentUserEmail = currentUserEmail.replace('@', '_')
            currentUserEmail = currentUserEmail.replace('.', '_')
        }

        val childPath = "profile_images/$currentUserEmail.jpg"
        val imageReference: StorageReference = FirebaseStorage.getInstance().getReference().child(childPath)

        imageReference.downloadUrl.addOnSuccessListener {
            profileImageDownloadUri = it
            Picasso.get().load(profileImageDownloadUri).into(profileImageView)
        }
    }

    // Changes authored by @Penguin5681 => end



    // Loading the google profile using the last signed in account
    private fun loadGoogleProfile() {
        val currentUser: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(requireActivity())
        val photoUri: Uri? = currentUser?.photoUrl
        Glide.with(this).load(photoUri).into(profileImageView)
    }
}
