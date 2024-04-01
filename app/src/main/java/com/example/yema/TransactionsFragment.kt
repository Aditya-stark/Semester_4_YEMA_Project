package com.example.yema

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar

class TransactionsFragment : Fragment() {
    private lateinit var monthSpinner: Spinner
    private lateinit var data: ArrayList<TransactionDataModel>
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var usersDatabaseReference: DatabaseReference
    private lateinit var all_transaction_text: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transactions, container, false)
        monthSpinner = view.findViewById(R.id.monthSpinner)
        recyclerView = view.findViewById(R.id.transaction_recycler_view)
        all_transaction_text = view.findViewById(R.id.all_transaction_text)

        data = ArrayList()

        usersDatabaseReference = FirebaseDatabase.getInstance().getReference("Root/Users")

        usersDatabaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val expenseList = mutableListOf<Map<String, String>>()
                val incomeList = mutableListOf<Map<String, String>>()

                dataSnapshot.children.forEach { emailSnapshot ->
                    emailSnapshot.children.forEach { userChildSnapshot ->
                        if (emailSnapshot.key.toString() == FirebaseAuth.getInstance().currentUser?.email.toString().replace('.', ',')) {
                            if (userChildSnapshot.key == "Expenses") {
                                Log.d("EMAIL", emailSnapshot.key.toString())

                                userChildSnapshot.children.forEach { expenseSnapshot ->
                                    val expenseMap = mutableMapOf<String, String>()
                                    expenseSnapshot.children.forEach { entrySnapshot ->
                                        entrySnapshot.key?.let { entryKey ->
                                            val entryValue = entrySnapshot.value as String
                                            expenseMap[entryKey] = entryValue
                                        }
                                    }
                                    expenseList.add(expenseMap)
                                }

                            }
                            if (userChildSnapshot.key == "Income") {
                                userChildSnapshot.children.forEach { incomeSnapshot ->
                                    val incomeMap = mutableMapOf<String, String>()
                                    incomeSnapshot.children.forEach { entrySnapshot ->
                                        entrySnapshot.key?.let { entryKey ->
                                            val entryValue = entrySnapshot.value as String
                                            incomeMap[entryKey] = entryValue
                                        }
                                    }
                                    incomeList.add(incomeMap)
                                }
                            }
                        }
                    }
                }

                expenseList.forEach { expenseMap ->
                    val expenseAmount = expenseMap["expenseAmount"]

                    val expenseCategory = expenseMap["expenseCategory"]
                    val expenseDescription = expenseMap["expenseDescription"]
                    val expenseMonth = expenseMap["expenseMonth"]
                    val expenseTime = expenseMap["expenseTime"]
                    data.add(
                        TransactionDataModel(
                            R.drawable.business,
                            expenseCategory,
                            expenseDescription,
                            "-₹$expenseAmount",
                            expenseTime
                        )
                    )
                }
                transactionAdapter = TransactionAdapter(requireActivity(), data)
                recyclerView.setAdapter(transactionAdapter)

                incomeList.forEach { incomeMap ->
                    val incomeAmount = incomeMap["incomeAmount"]
                    val incomeCategory = incomeMap["incomeCategory"]
                    val incomeDescription = incomeMap["incomeDescription"]
                    val incomeMonth = incomeMap["incomeMonth"]
                    val incomeTime = incomeMap["incomeTime"]
                    data.add(
                        TransactionDataModel(
                            R.drawable.business,
                            incomeCategory,
                            incomeDescription,
                            "+₹$incomeAmount",
                            incomeTime
                        )
                    )

                }
                transactionAdapter = TransactionAdapter(requireActivity(), data)
                recyclerView.setAdapter(transactionAdapter)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Error: ${databaseError.message}")
            }
        })

        recyclerView.setLayoutManager(LinearLayoutManager(requireActivity()))

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

    private fun convertToRed(text: String): String {
        return "\u001B[31m$text\u001B[0m"
    }
}
