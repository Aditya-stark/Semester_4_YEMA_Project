package com.example.yema

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

/*
* Changes (Code Cleanup, Typo Fix):
* ~ Removed the not so "necessary" functions => onViewCreated(), onCreate()
* ~ Merged every code into onCreateView()
* */

class HomeFragment : Fragment() {
    private lateinit var profileImageView: CircleImageView
    private lateinit var profileImageDownloadUri: Uri
    private lateinit var accountBalanceTextView: TextView
    private lateinit var accountBalanceIncomePreview: TextView
    private lateinit var accountBalanceExpensePreview: TextView
    private lateinit var parentReference: DatabaseReference
    private val handler = Handler(Looper.getMainLooper())

    @SuppressLint("CutPasteId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_home, container, false)

        // Changes Authored: Change the parent reference location

        parentReference = FirebaseDatabase.getInstance().getReference("Root/Users")
        accountBalanceTextView = view.findViewById(R.id.account_balance_home_fragment)
        accountBalanceIncomePreview = view.findViewById(R.id.income_value_home_fragment_preview)
        accountBalanceExpensePreview = view.findViewById(R.id.expense_value_home_fragment_preview)
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

        // loading the account balance
//        val parentNode = requireActivity().getSharedPreferences("user_prefs_username", Context.MODE_PRIVATE)
//        val childPath = parentNode.getString("user_username", "")

        val userEmailKey = FirebaseAuth.getInstance().currentUser?.email
        val childPath = userEmailKey?.replace('.', ',')
        val expenseReference = parentReference.child(childPath.toString()).child("Expenses")
        val incomeReference = parentReference.child(childPath.toString()).child("Income")
        var totalExpenseAmount = 0
        var totalIncomeAmount = 0


        // TODO: Don't Change
        expenseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (expenseSnapshot in snapshot.children) {
                    val expense = expenseSnapshot.getValue(ExpenseData::class.java)
                    val expenseAmount = expense?.expenseAmount?.toIntOrNull() ?: 0
                    totalExpenseAmount += expenseAmount
                }
                updateAccountBalance(totalIncomeAmount, totalExpenseAmount)
                "₹$totalExpenseAmount".also { accountBalanceExpensePreview.text = it }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        // TODO: Don't Change
        incomeReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (incomeSnapshot in snapshot.children) {
                    val income = incomeSnapshot.getValue(IncomeData::class.java)
                    val incomeAmount = income?.incomeAmount?.toIntOrNull() ?: 0
                    totalIncomeAmount += incomeAmount
                }
                updateAccountBalance(totalIncomeAmount, totalExpenseAmount)
                "₹$totalIncomeAmount".also { accountBalanceIncomePreview.text = it }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
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
    private fun updateAccountBalance(totalIncomeAmount: Int, totalExpenseAmount: Int) {
        val accountBalance = totalIncomeAmount - totalExpenseAmount
        "₹$accountBalance".also { accountBalanceTextView.text = it }
    }
}
