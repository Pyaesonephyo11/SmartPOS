package com.train.pos.repository

import com.train.pos.dao.SaleDao
import com.train.pos.model.DailyProfit
import com.train.pos.model.MonthlyProfitModel
import com.train.pos.model.MonthlyReportItem
import com.train.pos.model.MonthlySummary
import kotlinx.coroutines.flow.Flow


class MonthlyRepository(private val dao: SaleDao) {

    suspend fun getSummary(month: String): MonthlySummary {
        return dao.getMonthlySummary(month)
    }

    suspend fun getChart(month: String): List<DailyProfit> {
        return dao.getMonthlyProfitChart(month)
    }

    suspend fun getMonthlyReport(
        startTime: Long,
        endTime: Long
    ): List<MonthlyReportItem> {
        return dao.getMonthlyReport(startTime, endTime)
    }
}

