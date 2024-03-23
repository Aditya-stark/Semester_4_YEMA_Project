package com.example.yema

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

class IncomeAdd : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view: View = inflater.inflate(R.layout.fragment_income_add, container, false)
        val firebaseDataBase: DatabaseReference = FirebaseDatabase.getInstance().getReference()
        val inContinueButton = view.findViewById<Button>(R.id.in_continue_button);

        inContinueButton.setOnClickListener(){
            val amt: String = view.findViewById<EditText>(R.id.income_amount_input).text.toString()
            val category: String = view.findViewById<Spinner>(R.id.in_category_spinner).selectedItem.toString()
            val description: String = view.findViewById<EditText>(R.id.description_edittext).text.toString()

            val childReference = firebaseDataBase.push()

            val incomeData = IncomeData()
            incomeData.amount = amt
            incomeData.category = category
            incomeData.description = description

            childReference.setValue(incomeData).addOnSuccessListener {
                Log.d("REALTIME", "Success")
            }.addOnFailureListener{
                Log.d("REALTIME", it.toString())
            }
            // TODO: Fix fragment switching after adding data into firebase
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.main_activity_constraint_layout, HomeFragment()).commit()
        }



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
    }
}

class IncomeData() {
    public var amount: String = ""
    public var category: String = ""
    public var description: String = ""

}