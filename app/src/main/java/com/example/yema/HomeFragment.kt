package com.example.yema

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import java.util.Calendar

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var monthSpinner: Spinner

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Your existing code here...

        // Initialize the Spinner
        monthSpinner = view.findViewById(R.id.monthSpinner)
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

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_home, container, false)

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

        val  expensesLinearLayoutButton = view.findViewById<LinearLayout>(R.id.expensesBtn)
        expensesLinearLayoutButton.setOnClickListener{
            val fragment = ExpensesAdd()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.homeFragment, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }


        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun getCurrentMonthIndex(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.MONTH)
    }
}
