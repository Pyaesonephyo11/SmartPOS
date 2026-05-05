package com.train.pos.ViewModel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.train.pos.database.AppDatabase
import com.train.pos.model.YearlyChartPoint
import com.train.pos.model.YearlySummary
import com.train.pos.repository.SaleRepository
import kotlinx.coroutines.launch

class YearlyViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).saleDao()
    private val repo = SaleRepository(dao)

    private val _summary = MutableLiveData<YearlySummary>()
    val summary: LiveData<YearlySummary> = _summary

    private val _chart = MutableLiveData<List<YearlyChartPoint>>()
    val chart: LiveData<List<YearlyChartPoint>> = _chart

    fun load(year: Int) {
        viewModelScope.launch {
            _summary.value = repo.getYearlySummary(year)
            _chart.value = repo.getYearlyChart(year)
        }
    }
}

