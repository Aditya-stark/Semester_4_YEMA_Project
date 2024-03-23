package com.example.yema

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Spinner
import android.widget.TextView

class ExpensesAdd : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_expenses_add, container, false)
    }

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

        // Optional: Set default selection to "Select Category"
        spinner.setSelection(0)



    }
}
