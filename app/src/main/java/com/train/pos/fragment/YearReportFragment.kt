package com.train.pos.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.train.pos.R
import com.train.pos.ViewModel.ManageViewModel
import com.train.pos.ViewModel.YearlyViewModel
import com.train.pos.YearlyBarChartView
import java.util.Calendar


class YearReportFragment : Fragment(R.layout.fragment_yearreport) {

    private lateinit var vm: YearlyViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        vm = ViewModelProvider(this)[YearlyViewModel::class.java]

        val tvYear = view.findViewById<TextView>(R.id.tvSelectYear)
        val cardSelectYear = view.findViewById<View>(R.id.cardSelectYear)
        val tvSales = view.findViewById<TextView>(R.id.tvYearTotalyr)
        val tvProfit = view.findViewById<TextView>(R.id.tvYearProfityr)
        val chart = view.findViewById<YearlyBarChartView>(R.id.yearlyChart)

        // 1. Get the current year
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        // 2. Generate a "Real" year list (e.g., from current year down to 2022)
        // This creates: ["2024", "2023", "2022"] automatically
        val startYear = 2022
        val years = (currentYear downTo startYear).map { it.toString() }

        // 3. Set the default text to current year
        tvYear.text = currentYear.toString()

        val popupMenu = PopupMenu(requireContext(), tvYear)
        years.forEachIndexed { index, year ->
            popupMenu.menu.add(0, index, index, year)
        }

        cardSelectYear.setOnClickListener {
            popupMenu.setOnMenuItemClickListener { item ->
                val selectedYear = years[item.itemId]
                tvYear.text = selectedYear
                vm.load(selectedYear.toInt())
                true
            }
            popupMenu.show()
        }

        // Initial Load
        vm.load(currentYear)

        vm.summary.observe(viewLifecycleOwner) {
            // Commas are already handled here by %,d
            tvSales.text = "%,d Ks".format(it.totalSales)
            tvProfit.text = "%,d Ks".format(it.totalProfit)
        }

        vm.chart.observe(viewLifecycleOwner) {
            chart.setData(it)
        }
    }
}

