package com.train.pos.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.train.pos.dao.SaleDao
import com.train.pos.database.AppDatabase
import com.train.pos.model.DailyProfit
import com.train.pos.model.MonthlyReportItem
import com.train.pos.model.MonthlySummary
import com.train.pos.repository.MonthlyRepository
import kotlinx.coroutines.launch

class MonthlyViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).saleDao()

    private val repo = MonthlyRepository(dao)

    val summary = MutableLiveData<MonthlySummary>()
    val chartData = MutableLiveData<List<DailyProfit>>()

    fun load(month: String) {
        viewModelScope.launch {
            summary.value = repo.getSummary(month)
            chartData.value = repo.getChart(month)
        }
    }
    suspend fun getMonthlyProfitChart(month: String): List<DailyProfit> {
        return dao.getMonthlyProfitChart(month)
    }


    val reportList = MutableLiveData<List<MonthlyReportItem>>()
    val totalRevenue = MutableLiveData<Int>()
    val totalProfit = MutableLiveData<Int>()

    fun loadMonthly(start: Long, end: Long) {
        viewModelScope.launch {
            val list = repo.getMonthlyReport(start, end)

            reportList.value = list
            totalRevenue.value = list.sumOf { it.totalRevenue }
            totalProfit.value = list.sumOf { it.totalProfit }
        }
    }

}


