package com.example.yema

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.bottomnavigation.BottomNavigationView
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
import java.util.regex.Pattern

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

    // Income Data

    private lateinit var incomeCardIcon: ImageView
    private lateinit var incomeCardCategory: TextView
    private lateinit var incomeCardDescription: TextView
    private lateinit var incomeCardAmount: TextView
    private lateinit var incomeCardTime: TextView

    // Expense Data

    private lateinit var expenseCardIcon: ImageView
    private lateinit var expenseCardCategory: TextView
    private lateinit var expenseCardDescription: TextView
    private lateinit var expenseCardAmount: TextView
    private lateinit var expenseCardTime: TextView

    private lateinit var recentIncomeMap: Map <String, String>
    private lateinit var recentExpenseMap: Map <String, String>
    @SuppressLint("CutPasteId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_home, container, false)
        // initializations for income card (recent transactions)

        incomeCardIcon = view.findViewById(R.id.income_cardIcon)
        incomeCardCategory = view.findViewById(R.id.income_cardCategory)
        incomeCardDescription = view.findViewById(R.id.income_cardDescription)
        incomeCardAmount = view.findViewById(R.id.income_cardAmount)
        incomeCardTime = view.findViewById(R.id.income_cardTime)

        // initializations for expense card (recent transactions)

        expenseCardIcon = view.findViewById(R.id.expense_cardIcon)
        expenseCardCategory = view.findViewById(R.id.expense_cardCategory)
        expenseCardDescription = view.findViewById(R.id.expense_cardDescription)
        expenseCardAmount = view.findViewById(R.id.expense_cardAmount)
        expenseCardTime = view.findViewById(R.id.expense_cardTime)

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
            switchFragment(ProfileFragment())
            val bottomNavigationBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomNavigationBar.selectedItemId = R.id.profile

        }

        val seeAllBtn = view.findViewById<TextView>(R.id.seeAllBtn)
        seeAllBtn.setOnClickListener {
            switchFragment(TransactionsFragment())
            val bottomNavigationBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomNavigationBar.selectedItemId = R.id.transaction
        }


        val userEmailKey = FirebaseAuth.getInstance().currentUser?.email
        val childPath = userEmailKey?.replace('.', ',')
        val expenseReference = parentReference.child(childPath.toString()).child("Expenses")
        val incomeReference = parentReference.child(childPath.toString()).child("Income")
        var totalExpenseAmount = 0
        var totalIncomeAmount = 0

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

        var userEmail = FirebaseAuth.getInstance().currentUser?.email
        userEmail = userEmail?.replace('.', ',')    // Sanitization

        val recentIncomeDataPath = "Root/Users/$userEmail/Income"
        val recentExpenseDataPath = "Root/Users/$userEmail/Expenses"

        val recentIncomeReference = FirebaseDatabase.getInstance().reference.child(recentIncomeDataPath)
        val recentExpenseReference = FirebaseDatabase.getInstance().reference.child(recentExpenseDataPath)

        recentIncomeReference.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("RECENT_INCOME_INDEX", snapshot.childrenCount.toString())
                recentIncomeMap = parseKeyValueString(snapshot.child(snapshot.childrenCount.toString()).value.toString())
                incomeCardTime.text = recentIncomeMap["incomeTime"]
                incomeCardAmount.text = "+₹" + recentIncomeMap["incomeAmount"]
                val incomeCategoryString = recentIncomeMap["incomeCategory"]

                incomeCardCategory.text = recentIncomeMap["incomeCategory"]

                when (incomeCategoryString) {
                    "Salary" -> incomeCardIcon.setImageResource(R.drawable.in_icon_salary)
                    "Pocket Money" -> incomeCardIcon.setImageResource(R.drawable.in_icon_pocket_money)
                    "Freelance/Consulting" -> incomeCardIcon.setImageResource(R.drawable.in_icon_free_lance)
                    "Business Income" -> incomeCardIcon.setImageResource(R.drawable.in_icon_business)
                    "Investments" -> incomeCardIcon.setImageResource(R.drawable.in_icon_investment)
                    "Rental Income" -> incomeCardIcon.setImageResource(R.drawable.in_icon_rent)
                    "Side Hustle" -> incomeCardIcon.setImageResource(R.drawable.in_icon_side_hustle)
                    "Gifts" -> incomeCardIcon.setImageResource(R.drawable.in_icon_gift)
                    "Other" -> incomeCardIcon.setImageResource(R.drawable.in_icon_other)
                }

                incomeCardDescription.text = recentIncomeMap["incomeDescription"]
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Database Error Home Fragment", "Error$error.message")
            }
        })

        recentExpenseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("RECENT_EXPENSE_INDEX", snapshot.childrenCount.toString())
                recentExpenseMap = parseKeyValueString(snapshot.child(snapshot.childrenCount.toString()).value.toString())
                expenseCardTime.text = recentExpenseMap["expenseTime"]
                expenseCardAmount.text = "-₹" + recentExpenseMap["expenseAmount"]

                val expenseCardCategoryString = recentExpenseMap["expenseCategory"]

                when (expenseCardCategoryString) {
                    "Shopping" -> expenseCardIcon.setImageResource(R.drawable.ex_icon_shopping)
                    "Food" -> expenseCardIcon.setImageResource(R.drawable.ex_icon_food)
                    "Transportation" -> expenseCardIcon.setImageResource(R.drawable.ex_icon_transport)
                    "Housing" -> expenseCardIcon.setImageResource(R.drawable.ex_icon_housing)
                    "Entertainment" -> expenseCardIcon.setImageResource(R.drawable.ex_icon_entertainment)
                    "Healthcare" -> expenseCardIcon.setImageResource(R.drawable.ex_icon_health)
                    "Travel" -> expenseCardIcon.setImageResource(R.drawable.ex_icon_travel)
                    "Education" -> expenseCardIcon.setImageResource(R.drawable.ex_icon_education)
                    "Personal Care" -> expenseCardIcon.setImageResource(R.drawable.ex_icon_personal_care)
                    "Subscriptions" -> expenseCardIcon.setImageResource(R.drawable.ex_icon_subscription)
                    "Other" -> expenseCardIcon.setImageResource(R.drawable.ex_icon_other)
                }

                expenseCardCategory.text = recentExpenseMap["expenseCategory"]
                expenseCardDescription.text = recentExpenseMap["expenseDescription"]
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Database Error Home Fragment", "Error$error.message")
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

    // If it works it works
    private fun updateAccountBalance(totalIncomeAmount: Int, totalExpenseAmount: Int) {
        val accountBalance = totalIncomeAmount - totalExpenseAmount
        "₹$accountBalance".also { accountBalanceTextView.text = it }
    }

    // If it works it works, don't fuck around with this.
    fun parseKeyValueString(input: String): Map<String, String> {
        val pattern = Pattern.compile("([a-zA-Z]+)=(.*?)[,}]")
        val matcher = pattern.matcher(input)
        val map = mutableMapOf<String, String>()

        while (matcher.find()) {
            val key = matcher.group(1)
            val value = matcher.group(2)
            if (value != null) {
                if (key != null) {
                    map[key.toString()] = value
                }
            }
        }

        return map
    }
    private fun switchFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.homeFragment, fragment)
            .commit()
    }
}
