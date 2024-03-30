package com.example.yema

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicInteger

class ExpensesAdd : Fragment() {
    private lateinit var parentReference: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_expenses_add, container, false)
    }

    @SuppressLint("CutPasteId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinner: Spinner = view.findViewById(R.id.category_spinner)
        val categories = arrayOf(
            "Category", "Shopping", "Food", "Transportation", "Housing",
            "Entertainment", "Healthcare", "Travel", "Education",
            "Personal Care", "Subscriptions", "Other"
        )

        val adapter = object : BaseAdapter() {
            override fun getCount(): Int = categories.size

            override fun getItem(position: Int): String = categories[position]

            override fun getItemId(position: Int): Long = position.toLong()

            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val textView = convertView as? TextView ?: LayoutInflater.from(requireContext())
                    .inflate(android.R.layout.simple_spinner_item, parent, false) as TextView
                textView.text = categories[position]

                // Set hint style for the first item (empty string)
                if (position == 0) {
                    textView.setTextColor(Color.GRAY) // Set hint text color to gray (optional)
                }

                return textView
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
                return getView(position, convertView, parent)
            }
        }
        spinner.adapter = adapter

        val continueButton = view.findViewById<Button>(R.id.ex_continue_button)
        val expenseAmountEditText = view.findViewById<EditText>(R.id.expenses_amount_input)
        val expenseDescriptionEditText = view.findViewById<EditText>(R.id.description_edittext)
        val expenseCategorySpinner = view.findViewById<Spinner>(R.id.category_spinner)
        parentReference = FirebaseDatabase.getInstance().getReference("Users")


        // TODO: Figure out unique id for each transaction
        val expenseIdx = 1

        continueButton.setOnClickListener {
            val amountText: String = expenseAmountEditText.text.toString()
            val categorySelectedText: String = expenseCategorySpinner.selectedItem.toString()
            val descriptionText: String = expenseDescriptionEditText.text.toString()

            if (amountText.isNotEmpty() && categorySelectedText.isNotEmpty() && descriptionText.isNotEmpty()) {
                val parentNode = requireActivity().getSharedPreferences("user_prefs_username", Context.MODE_PRIVATE)
                val childPath = parentNode.getString("user_username", "")
                val expenseReference = parentReference.child(childPath.toString()).child("Expenses")

                expenseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val expenseCount = snapshot.childrenCount
                        val nextId = (expenseCount + 1).toString()
                        expenseReference.child(nextId).setValue(currentMonth()?.let { it1 ->
                            ExpenseData(amountText, categorySelectedText, descriptionText,
                                it1, currentTime())
                        })
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

                replaceFragment(HomeFragment())
            }
            else {
                // Empty Values (edge case)
                Log.d("Empty Fields", "Fields empty")
                Toast.makeText(requireActivity(), "One or more fields empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Optional: Set default selection to "Select Category"
        spinner.setSelection(0)
    }
    private fun replaceFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }

    private fun currentMonth(): String? {
        return LocalDate.now().month.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH)
    }

    private fun currentTime(): String {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
    }
}

data class ExpenseData (
    val expenseAmount: String,
    val expenseCategory: String,
    val expenseDescription: String,
    val expenseMonth: String,
    val expenseTime: String
) {
    constructor() : this("", "", "", "", "")
}