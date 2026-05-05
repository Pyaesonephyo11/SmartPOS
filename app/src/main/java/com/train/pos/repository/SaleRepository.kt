package com.train.pos.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.train.pos.YearUtil
import com.train.pos.dao.SaleDao
import com.train.pos.model.DailySummary
import com.train.pos.model.SaleWithItems
import com.train.pos.model.YearlyChartPoint
import com.train.pos.model.YearlySummary
import kotlinx.coroutines.flow.Flow

class SaleRepository(private val dao: SaleDao) {

    fun getSaleHistoryPaging(): Flow<PagingData<SaleWithItems>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,        // 👈 paging count ဒီမှာ
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                dao.getSaleHistoryPaging()
            }
        ).flow
    }


    fun getSaleHistoryByDatePaging(start: Long, end: Long) =
        Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                dao.getSalesByDatePaging(start, end)
            }
        ).flow
    suspend fun getDailySummary(start: Long, end: Long): DailySummary {
        return dao.getDailyTotalAndProfit(start, end)
    }

    suspend fun getYearlySummary(year: Int): YearlySummary {
        return dao.getYearlySummary(year.toString())
    }

    suspend fun getYearlyChart(year: Int): List<YearlyChartPoint> {
        val raw = dao.getYearlyProfitChart(year.toString())

        // ✅ missing months fill (Jan–Dec)
        val map = raw.associateBy { it.month }
        return (1..12).map {
            YearlyChartPoint(it, map[it]?.profit ?: 0)
        }
    }
}
