package com.train.pos.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.train.pos.DateUtil
import com.train.pos.database.AppDatabase
import com.train.pos.entries.CategoryEntity
import com.train.pos.entries.ProductEntity
import com.train.pos.model.CartItem
import com.train.pos.model.DailySummary
import com.train.pos.model.MonthlyProfitModel
import com.train.pos.model.ProductSpinnerItem
import com.train.pos.model.ProductWithCategory
import com.train.pos.model.YearlyChartPoint
import com.train.pos.model.YearlySummary
import com.train.pos.repository.MonthlyRepository
import com.train.pos.repository.ProductRepository
import com.train.pos.repository.SaleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ManageViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)

    // Category
    val categories: LiveData<List<CategoryEntity>> = db.categoryDao().getAllCategories()

    //sale
    val saleRepository = SaleRepository(db.saleDao())

    // Product
    private val productRepo = ProductRepository(db.productDao())
   // private val selectedCategoryId = MutableLiveData<Int>()
    val productWithCategory = productRepo.getAllProductsWithCategory().flow.cachedIn(viewModelScope)

    // all product search
    private val searchQuery = MutableStateFlow("")
    val searchproducts = searchQuery.flatMapLatest { keyword -> productRepo.getProductsPaging(keyword) }
        .cachedIn(viewModelScope)

    fun search(text: String) {
        searchQuery.value = text
    }


// Products (Paging)
private val selectedCategoryId = MutableStateFlow(0)
val products = selectedCategoryId.flatMapLatest { categoryId ->
        productRepo.getProductsByCategoryPaging(categoryId)
    }
    .cachedIn(viewModelScope)


    fun selectCategory(id: Int) {
        selectedCategoryId.value = id
    }





    fun saveProduct(name: String, price: Int, categoryId: Int,imageUri: String?,stock: Int,barcode: String,costPrice: Int) {
        viewModelScope.launch {
            productRepo.insert(ProductEntity(name = name, price = price, categoryId = categoryId, imageUri = imageUri, stock = stock, barcode = barcode, costPrice = costPrice))
        }
    }


    fun deleteProduct(item: ProductWithCategory) {
        viewModelScope.launch {
            productRepo.deleteProduct(ProductEntity(id = item.productId, name = item.productName, price = item.price, categoryId = 0, imageUri = item.imageUri, stock = item.stock, costPrice = item.costPrice))
        }
    }

    fun updateProduct(product: ProductEntity) {
        viewModelScope.launch {
            productRepo.updateProduct(product)
        }
    }
    fun checkout(cartItems: List<CartItem>) {
        viewModelScope.launch {
            productRepo.checkout(cartItems)
        }
    }
  //  val saleHistoryFlow = saleRepository.getSaleHistoryPaging().cachedIn(viewModelScope)

    val spinnerProducts: LiveData<List<ProductSpinnerItem>> = productRepo.getProductsForSpinner()

    private val _scannedProduct = MutableLiveData<ProductEntity?>()
    val scannedProduct: LiveData<ProductEntity?> = _scannedProduct

    fun loadProductByBarcode(barcode: String) {
        viewModelScope.launch {
            val product = productRepo.getProductByBarcode(barcode)
            _scannedProduct.postValue(product)
        }
    }

    //low stock alert
    val lowStockProducts = productRepo.getLowStockProducts()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun updateStock(productId: Int, newStock: Int) {
        viewModelScope.launch {
            productRepo.updateStock(productId, newStock)
        }
    }

    private val manageSearchQuery = MutableStateFlow("")

    val manageSearchProducts = manageSearchQuery
        .flatMapLatest { keyword ->
            productRepo.searchProductsPaging(keyword)
        }
        .cachedIn(viewModelScope)

    fun searchManageProduct(text: String) {
        manageSearchQuery.value = text
    }

    /* ---------------- DAILY SALE FILTER ---------------- */

    // Selected date (default = Today)
    private val selectedDate = MutableStateFlow<Long>(System.currentTimeMillis())

    fun setSaleDate(date: Long) {
        selectedDate.value = date
    }

    val saleHistoryFlow = selectedDate
        .flatMapLatest { date ->
            val start = DateUtil.startOfDay(date)
            val end = DateUtil.endOfDay(date)
            saleRepository.getSaleHistoryByDatePaging(start, end)
        }
        .cachedIn(viewModelScope)


    /* ---------- Daily Total & Profit ---------- */
    val dailySummary = selectedDate
        .flatMapLatest { date ->
            kotlinx.coroutines.flow.flow {
                val start = DateUtil.startOfDay(date)
                val end = DateUtil.endOfDay(date)
                emit(saleRepository.getDailySummary(start, end))
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            DailySummary(0, 0)
        )


    private val selectedYear = MutableStateFlow(
        java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
    )

    fun setYear(year: Int) {
        selectedYear.value = year
    }

    val yearlySummary = selectedYear
        .flatMapLatest { year ->
            kotlinx.coroutines.flow.flow {
                emit(saleRepository.getYearlySummary(year))
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            YearlySummary(0, 0)
        )

    suspend fun loadYearlyChart(year: Int): List<YearlyChartPoint> {
        return saleRepository.getYearlyChart(year)
    }


}
