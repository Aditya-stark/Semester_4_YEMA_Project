package com.example.yema

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import java.util.Calendar

class TransactionsFragment : Fragment() {
    private lateinit var monthSpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment's layout first
        val view = inflater.inflate(R.layout.fragment_transactions, container, false)

        // Access views after layout inflation
        monthSpinner = view.findViewById(R.id.monthSpinner)

        // Create and set adapter for spinner
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.months_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_month)
            monthSpinner.adapter = adapter

            // Set default selection to the current month
            val currentMonthIndex = getCurrentMonthIndex()
            monthSpinner.setSelection(currentMonthIndex)
        }

        return view
    }

    private fun getCurrentMonthIndex(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.MONTH) // Months are 0-indexed
    }
}
