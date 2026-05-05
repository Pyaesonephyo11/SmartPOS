package com.train.pos.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.train.pos.R
import com.train.pos.TransactionDetailDialog
import com.train.pos.ViewModel.ManageViewModel
import com.train.pos.adapters.SaleHistoryPagingAdapter
import com.train.pos.printer.PrinterHelper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// ... existing imports

class SaleHistoryFragment : Fragment() {

    private lateinit var viewModel: ManageViewModel
    private lateinit var adapter: SaleHistoryPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_sale_history, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvSaleHistory)
        val tvTotal = view.findViewById<TextView>(R.id.tvDailyTotal)
        val tvProfit = view.findViewById<TextView>(R.id.tvDailyProfit)
        val tvDate = view.findViewById<TextView>(R.id.tvSelectedDate)

        viewModel = ViewModelProvider(this)[ManageViewModel::class.java]

        // --- ADDED: Set current date on first load ---
        val calendar = Calendar.getInstance()
        val todayMillis = calendar.timeInMillis
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // 1. Show today's date in the TextView immediately
        tvDate.text = sdf.format(Date(todayMillis))

        // 2. Tell the ViewModel to fetch today's data immediately
        viewModel.setSaleDate(todayMillis)
        // --------------------------------------------

        adapter = SaleHistoryPagingAdapter { sale ->
            TransactionDetailDialog.newInstance(sale).show(parentFragmentManager, "DETAIL")
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        tvDate.setOnClickListener {
            val cal = Calendar.getInstance()

            DatePickerDialog(
                requireContext(),
                { _, y, m, d ->
                    cal.set(y, m, d)
                    val selected = cal.timeInMillis

                    tvDate.text = SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    ).format(Date(selected))

                    viewModel.setSaleDate(selected)
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.saleHistoryFlow.collectLatest {
                adapter.submitData(it)
            }
        }

        /* Observe daily total & profit */
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dailySummary.collect { summary ->
                tvTotal.text = "%,d Ks".format(summary.totalSales)
                tvProfit.text = "%,d Ks".format(summary.totalProfit)
            }
        }

        return view
    }
}