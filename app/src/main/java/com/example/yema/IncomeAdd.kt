package com.example.yema

import android.annotation.SuppressLint
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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

/*
* Fixes:
* ~ Fixed a bug where it won't load a fragment upon a successful data push
*
* Tweaks (Additional fixes)
* ~ Code won't allow empty values to be pushed into database
* ~ Code cleanup => add a replaceFragment() 
* ~ Typo Fix: Common identifier naming mistakes
*
* Critical Fix:
* ~ Addition of the Context property in the corresponding XML view file
* */

class IncomeAdd : Fragment() {
    private var DATA_PUSH_STATUS: String = "Realtime Data Push Status"
    @SuppressLint("CutPasteId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view: View = inflater.inflate(R.layout.fragment_income_add, container, false)

        val spinner: Spinner = view.findViewById(R.id.in_category_spinner)
        val categories = arrayOf(
            "Category", "Salary",
            "Pocket Money",
            "Freelance/Consulting",
            "Business Income",
            "Investments",
            "Rental Income",
            "Side Hustle",
            "Gifts",
            "Other",
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

        // Optional: Set default selection to "Select Category"
        spinner.setSelection(0)

        val firebaseDataBase: DatabaseReference = FirebaseDatabase.getInstance().getReference()
        val inContinueButton = view.findViewById<Button>(R.id.in_continue_button);
        val amountEditText = view.findViewById<EditText>(R.id.income_amount_input)
        val categorySpinner = view.findViewById<Spinner>(R.id.in_category_spinner)
        val descriptionEditText = view.findViewById<EditText>(R.id.description_edittext)

        inContinueButton.setOnClickListener(){
            val amountText: String = amountEditText.text.toString()
            val categorySelectedText: String = categorySpinner.selectedItem.toString()
            val descriptionText: String = descriptionEditText.text.toString()

            if (amountText.isNotEmpty() && categorySelectedText.isNotEmpty() && descriptionText.isNotEmpty()) {
                val childReference = firebaseDataBase.push()

                val incomeData = IncomeData()
                incomeData.amount = amountText
                incomeData.category = categorySelectedText
                incomeData.description = descriptionText

                childReference.setValue(incomeData).addOnSuccessListener {
                    Log.d(DATA_PUSH_STATUS, "Data Pushed")

                    // Resetting the values to defaults upon a successful push
                    amountEditText.setText("")
                    categorySpinner.setSelection(0)
                    descriptionEditText.setText("")
                }.addOnFailureListener{
                    Log.d(DATA_PUSH_STATUS, it.message.toString())
                }

                replaceFragment(HomeFragment())
            }
            else {
                // Empty Values (edge case)
                Log.d("Empty Fields", "Fields empty")
                Toast.makeText(requireActivity(), "One or more fields empty", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun replaceFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }
}

class IncomeData() {
    public var amount: String = ""
    public var category: String = ""
    public var description: String = ""
}