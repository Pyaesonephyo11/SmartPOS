package com.train.pos.fragment


import android.app.DatePickerDialog
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.train.pos.MonthlyChartView
import com.train.pos.MonthlyPdfGenerator
import com.train.pos.R
import com.train.pos.ViewModel.ManageViewModel
import com.train.pos.ViewModel.MonthlyViewModel
import com.train.pos.adapters.MonthlyChartAdapter
import com.train.pos.adapters.MonthlyReportAdapter
import com.train.pos.database.AppDatabase
import com.train.pos.model.MonthlyProfitModel
import com.train.pos.repository.MonthlyRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MonthlyFragment : Fragment(R.layout.monthly_fragment) {

    private lateinit var viewModel: MonthlyViewModel
    private lateinit var adapter: MonthlyReportAdapter

    private var start: Long = 0
    private var end: Long = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val tvRevenue = view.findViewById<TextView>(R.id.tvRevenue)
        val tvProfit = view.findViewById<TextView>(R.id.tvProfit)
        val chart = view.findViewById<MonthlyChartView>(R.id.monthlyChart)
        val tvSelectMonth = view.findViewById<TextView>(R.id.tvSelectMonth)
        val rv = view.findViewById<RecyclerView>(R.id.rvMonthly)

        val btnExport = view.findViewById<Button>(R.id.btnExportPdf)


        viewModel = ViewModelProvider(this)[MonthlyViewModel::class.java]
        btnExport.setOnClickListener {

            val summary = viewModel.summary.value ?: return@setOnClickListener
            val list = viewModel.reportList.value ?: return@setOnClickListener
            val month = tvSelectMonth.text.toString()

            val file = MonthlyPdfGenerator.export(
                requireContext(),
                month,
                summary.revenue,
                summary.profit,
                list
            )

            Toast.makeText(
                requireContext(),
                "PDF Saved:\n${file.absolutePath}",
                Toast.LENGTH_LONG
            ).show()
        }



        adapter = MonthlyReportAdapter()
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        /* ---------- OBSERVE (ONLY ONCE) ---------- */

        viewModel.summary.observe(viewLifecycleOwner) {
            tvRevenue.text ="%,d Ks".format(it.revenue)
            tvProfit.text = "%,d Ks".format(it.profit)
        }

        viewModel.reportList.observe(viewLifecycleOwner) {
            adapter.submit(it)
        }

        // Inside onViewCreated, replace the picker logic and default setup:

        /* ---------- DEFAULT MONTH ---------- */
        val calendar = Calendar.getInstance()
        updateMonth(calendar)
// Show "January 2024" instead of "2024-01"
        tvSelectMonth.text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time)

        /* ---------- MONTH PICKER ---------- */
        tvSelectMonth.setOnClickListener {
            showMonthPicker { year, month ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)

                updateMonth(calendar)

                // UPDATED: Show full month name
                tvSelectMonth.text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time)
            }
        }
    }

    /* ---------- UPDATE MONTH DATA ---------- */

    private fun updateMonth(calendar: Calendar) {

        // start time
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        start = calendar.timeInMillis

        // end time
        calendar.add(Calendar.MONTH, 1)
        end = calendar.timeInMillis - 1

        // chart uses yyyy-MM
        val monthStr = SimpleDateFormat(
            "yyyy-MM",
            Locale.getDefault()
        ).format(Date(start))

        viewModel.load(monthStr)

        lifecycleScope.launch {
            val chartData = viewModel.getMonthlyProfitChart(monthStr)
            view?.findViewById<MonthlyChartView>(R.id.monthlyChart)
                ?.setData(chartData)
        }

        // recycler + summary
        viewModel.loadMonthly(start, end)
    }

    /* ---------- MONTH PICKER ---------- */

    private fun showMonthPicker(
        onMonthSelected: (Int, Int) -> Unit
    ) {
        val cal = Calendar.getInstance()

        val dialog = DatePickerDialog(
            requireContext(),
            { _, year, month, _ ->
                onMonthSelected(year, month)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )

        // hide day
        try {
            val dayId = Resources.getSystem()
                .getIdentifier("day", "id", "android")
            dialog.datePicker.findViewById<View>(dayId)?.visibility = View.GONE
        } catch (_: Exception) {
        }

        dialog.show()
    }
}



