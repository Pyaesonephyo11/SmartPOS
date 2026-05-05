package com.train.pos.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.train.pos.CartManager
import com.train.pos.dao.ProductDao
import com.train.pos.entries.ProductEntity
import com.train.pos.model.CartItem
import com.train.pos.model.ProductSpinnerItem
import com.train.pos.model.ProductWithCategory
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao) {

    suspend fun insert(product: ProductEntity) {
        productDao.insertProduct(product)
    }


    fun getProductsByCategoryPaging(categoryId: Int): Flow<PagingData<ProductEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                productDao.getProductsByCategoryPaging(categoryId)
            }
        ).flow
    }

    // ALl product search
    fun getProductsPaging(keyword: String): Flow<PagingData<ProductEntity>> {
        return Pager(config = PagingConfig(pageSize = 20,
            enablePlaceholders = false)) {
            productDao.searchAllProductsPaging(keyword)
        }.flow
    }

    fun getAllProductsWithCategory(): Pager<Int, ProductWithCategory> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {productDao.getProductsWithCategory() }
        )
    }

    suspend fun deleteProduct(product: ProductEntity) {
        productDao.delete(product)
    }
    suspend fun updateProduct(product: ProductEntity) {
        productDao.update(product)
    }

    suspend fun checkout(cartItems: List<CartItem>) {
        val safeList = cartItems.toList()
        safeList.forEach { item ->
            productDao.reduceStock(productId = item.productId, qty = item.qty)
        }
        CartManager.clear()
    }
    fun getProductsForSpinner(): LiveData<List<ProductSpinnerItem>> {
        return productDao.getProductsForSpinner()
    }

    //barcode
    suspend fun getProductByBarcode(barcode: String): ProductEntity? {
        return productDao.getProductByBarcode(barcode)
    }

    // low stock alert
    fun getLowStockProducts(): Flow<List<ProductEntity>> {
        return productDao.getLowStockProducts(3)
    }
    suspend fun updateStock(productId: Int, newStock: Int) {
        productDao.updateStock(productId, newStock)
    }

    fun searchProductsPaging(keyword: String) =
        Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                productDao.searchProductsPaging(keyword)
            }
        ).flow

}
