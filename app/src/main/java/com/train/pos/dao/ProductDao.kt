package com.train.pos.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.train.pos.entries.ProductEntity
import com.train.pos.model.ProductSpinnerItem
import com.train.pos.model.ProductWithCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert
    suspend fun insertProduct(product: ProductEntity)

    @Query("""
        SELECT products.* FROM products
        INNER JOIN categories
        ON products.categoryId = categories.id
        WHERE categories.id = :categoryId
    """)
    fun getProductsByCategory(categoryId: Int): LiveData<List<ProductEntity>>

    @Query("SELECT * FROM products")
    fun getAllProducts(): LiveData<List<ProductEntity>>


    //paging
    @Query("""
    SELECT * FROM products
    WHERE (:categoryId = 0 OR categoryId = :categoryId)
    ORDER BY id DESC
""")
    fun getProductsByCategoryPaging(categoryId: Int): PagingSource<Int, ProductEntity>


    //ALL products search
    @Query("""
        SELECT * FROM products
        WHERE name LIKE '%' || :keyword || '%'
        ORDER BY name ASC
    """)
    fun searchAllProductsPaging(
        keyword: String
    ): PagingSource<Int, ProductEntity>





    @Query("""
        SELECT 
            p.id AS productId,
            p.name AS productName,
            p.price AS price,
            c.name AS categoryName,
            p.imageUri AS imageUri,
            p.stock As stock,
            p.costPrice As costPrice
        FROM products p
        LEFT JOIN categories c
        ON p.categoryId = c.id
        ORDER BY p.id DESC
    """)
    fun getProductsWithCategory(): PagingSource<Int, ProductWithCategory>


    @Delete
    suspend fun delete(product: ProductEntity)

    @Update
    suspend fun update(product: ProductEntity)

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Int): ProductEntity

    @Query("UPDATE products SET stock = stock - :qty WHERE id = :productId")
    suspend fun reduceStock(productId: Int, qty: Int)


    @Query("""
    SELECT id, name, price ,barcode
    FROM products
    ORDER BY name ASC
""")
    fun getProductsForSpinner(): LiveData<List<ProductSpinnerItem>>

    //barcode
    @Query("SELECT * FROM products WHERE barcode = :barcode LIMIT 1")
    suspend fun getProductByBarcode(barcode: String): ProductEntity?

    //low stock alert
    @Query("""
        SELECT * FROM products
        WHERE stock <= :threshold
        ORDER BY stock ASC
    """)
    fun getLowStockProducts(threshold: Int = 5): Flow<List<ProductEntity>>

    @Query("UPDATE products SET stock = :stock WHERE id = :id")
    suspend fun updateStock(id: Int, stock: Int)



    @Query("""
SELECT p.id AS productId,
       p.name AS productName,
       p.price,
       p.costPrice,
       p.stock,
       p.imageUri,
       c.name AS categoryName
FROM products p
JOIN categories c ON p.categoryId = c.id
WHERE p.name LIKE '%' || :keyword || '%'
ORDER BY p.id DESC
""")
    fun searchProductsPaging(keyword: String): PagingSource<Int, ProductWithCategory>

}
