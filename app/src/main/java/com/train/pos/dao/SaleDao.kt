package com.train.pos.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.train.pos.entries.SaleEntity
import com.train.pos.entries.SaleItemEntity
import com.train.pos.model.DailyProfit
import com.train.pos.model.DailySummary
import com.train.pos.model.MonthlyProfitModel
import com.train.pos.model.MonthlyReportItem
import com.train.pos.model.MonthlySummary
import com.train.pos.model.SaleWithItems
import com.train.pos.model.YearlyChartPoint
import com.train.pos.model.YearlySummary
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {

    @Insert
    suspend fun insertSale(sale: SaleEntity): Long

    @Insert
    suspend fun insertSaleItems(items: List<SaleItemEntity>)



    @Query("SELECT * FROM sale_items WHERE saleId = :saleId")
    suspend fun getSaleItems(saleId: Int): List<SaleItemEntity>

    @Transaction
    @Query("SELECT * FROM sales ORDER BY dateTime DESC")
    fun getSalesWithItems(): LiveData<List<SaleWithItems>>

    @Transaction
    @Query("SELECT * FROM sales ORDER BY dateTime DESC")
    fun getSaleHistoryPaging(): PagingSource<Int, SaleWithItems>

    @Query("SELECT * FROM sale_items WHERE saleId = :saleId")
    suspend fun getItemsForSale(saleId: Int): List<SaleItemEntity>



    @Query("""
        SELECT 
            SUM(si.price * si.qty) AS revenue,
            SUM((si.price - si.costPrice) * si.qty) AS profit
        FROM sale_items si
        JOIN sales s ON si.saleId = s.saleId
        WHERE strftime('%Y-%m', s.dateTime/1000, 'unixepoch') = :yearMonth
    """)
    suspend fun getMonthlySummary(yearMonth: String): MonthlySummary

    @Query("""
        SELECT 
            strftime('%d', s.dateTime/1000, 'unixepoch') AS day,
            SUM((si.price - si.costPrice) * si.qty) AS profit
        FROM sale_items si
        JOIN sales s ON si.saleId = s.saleId
        WHERE strftime('%Y-%m', s.dateTime/1000, 'unixepoch') = :yearMonth
        GROUP BY day
        ORDER BY day
    """)
    suspend fun getMonthlyProfitChart(yearMonth: String): List<DailyProfit>



    //new
    @Query("""
        SELECT 
            productId,
            productName,
            price,
            costPrice,
            SUM(qty) AS sellingQty,
            SUM(qty * price) AS totalRevenue,
            SUM((price - costPrice) * qty) AS totalProfit
        FROM sale_items
        WHERE createAt BETWEEN :startTime AND :endTime
        GROUP BY productId
        ORDER BY totalRevenue DESC
    """)
    suspend fun getMonthlyReport(
        startTime: Long,
        endTime: Long
    ): List<MonthlyReportItem>



    @Query("""
    SELECT * FROM sales
    WHERE dateTime BETWEEN :start AND :end
    ORDER BY dateTime DESC
""")
    fun getSalesByDatePaging(
        start: Long,
        end: Long
    ): PagingSource<Int, SaleWithItems>

    @Query("""
    SELECT 
        IFNULL(SUM(qty * price), 0) AS totalSales,
        IFNULL(SUM(qty * (price - costPrice)), 0) AS totalProfit
    FROM sale_items
    INNER JOIN sales ON sales.saleId = sale_items.saleId
    WHERE sales.dateTime BETWEEN :start AND :end
""")
    suspend fun getDailyTotalAndProfit(
        start: Long,
        end: Long
    ): DailySummary


    @Query("""
    SELECT 
        IFNULL(SUM(qty * price), 0) AS totalSales,
        IFNULL(SUM(qty * (price - costPrice)), 0) AS totalProfit
    FROM sale_items
    INNER JOIN sales ON sales.saleId = sale_items.saleId
    WHERE sales.dateTime BETWEEN :start AND :end
""")
    suspend fun getYearlySummary(
        start: Long,
        end: Long
    ): YearlySummary


    /* ---------- YEARLY LINE CHART ---------- */
    @Query("""
    SELECT 
        strftime('%m', s.dateTime/1000, 'unixepoch') AS month,
        SUM((si.price - si.costPrice) * si.qty) AS profit
    FROM sale_items si
    JOIN sales s ON s.saleId = si.saleId
    WHERE strftime('%Y', s.dateTime/1000, 'unixepoch') = :year
    GROUP BY month
    ORDER BY month
   """)
    suspend fun getYearlyChart(
        year: String
    ): List<YearlyChartPoint>


    @Query("""
    SELECT 
        CAST(strftime('%m', s.dateTime/1000, 'unixepoch') AS INTEGER) AS month,
        IFNULL(SUM((si.price - si.costPrice) * si.qty), 0) AS profit
    FROM sales s
    JOIN sale_items si ON si.saleId = s.saleId
    WHERE strftime('%Y', s.dateTime/1000, 'unixepoch') = :year
    GROUP BY month
    ORDER BY month
""")
    suspend fun getYearlyProfitChart(year: String): List<YearlyChartPoint>


    @Query("""
    SELECT 
        IFNULL(SUM(si.qty * si.price), 0) AS totalSales,
        IFNULL(SUM(si.qty * (si.price - si.costPrice)), 0) AS totalProfit
    FROM sales s
    JOIN sale_items si ON si.saleId = s.saleId
    WHERE strftime('%Y', s.dateTime/1000, 'unixepoch') = :year
""")
    suspend fun getYearlySummary(year: String): YearlySummary

}



