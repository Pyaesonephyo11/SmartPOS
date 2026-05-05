package com.train.pos

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.train.pos.ViewModel.CategoryViewModel
import com.train.pos.ViewModel.ManageViewModel
import com.train.pos.adapters.CartAdapter
import com.train.pos.adapters.CategoryAdapter
import com.train.pos.adapters.ProductManagePagingAdapter
import com.train.pos.dao.ProductDao
import com.train.pos.entries.CategoryEntity
import com.train.pos.entries.ProductEntity
import com.train.pos.ProductActivity
import com.train.pos.adapters.LowStockAdapter
import com.train.pos.model.ProductWithCategory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var categoryviewModel: CategoryViewModel
    private lateinit var adapter: CategoryAdapter
    private lateinit var viewModel: ManageViewModel
    private lateinit var spinnerAdapter: ArrayAdapter<String>
    private var categoryList: List<CategoryEntity> = emptyList()
    private lateinit var selectImageUri: Uri
    private lateinit var lowStockAdapter: LowStockAdapter
    private lateinit var cardLowStock: View
    private lateinit var rvLowStock: RecyclerView
    var isselectedbtn = true
    var isselectedproduct = true
    private var editingProductId: Int? = null
    private var currentImagePath: String? = null
    lateinit var btnSaveProduct: Button
    lateinit var etProductName: EditText
    lateinit var etProductPrice: EditText
    lateinit var etcostProductPrice: EditText
    lateinit var spCategory: Spinner
    lateinit var imgProduct: ImageView
    lateinit var cardView: CardView
    lateinit var etstock: EditText
    private var searchJob: Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val etCategory = findViewById<EditText>(R.id.etCategory)
        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val rvCategory = findViewById<RecyclerView>(R.id.rvCategory)
        imgProduct=findViewById<ImageView>(R.id.imgProduct)
        val backbtn=findViewById<Button>(R.id.back)
        btnSaveProduct=findViewById<Button>(R.id.btnSaveProduct)
        etProductName=findViewById<EditText>(R.id.etProductName)
        etProductPrice=findViewById<EditText>(R.id.etProductPrice)
        etcostProductPrice=findViewById<EditText>(R.id.etCostPrice)
        etstock=findViewById<EditText>(R.id.etStock)
        spCategory=findViewById<Spinner>(R.id.spCategory)
        val btnAddNewProduct=findViewById<Button>(R.id.btnAddNewProduct)
        val btnAddNewCategory=findViewById<Button>(R.id.btnAddNewCategory)
        val cardViewcategory=findViewById<CardView>(R.id.cvcategory)
        cardView=findViewById<CardView>(R.id.cardviewnewproduct)
        val btncancel=findViewById<Button>(R.id.btnCancelProduct)
        rvLowStock = findViewById<RecyclerView>(R.id.rvLowStock)
        cardLowStock =findViewById(R.id.lowstockcard)
        val etsearch = findViewById<EditText>(R.id.etSearch)




        etsearch.doAfterTextChanged {
            viewModel.searchManageProduct(it.toString())
        }


        //low stock alert
        lowStockAdapter = LowStockAdapter{
            showEditStockDialog(it)
        }
        rvLowStock.layoutManager = LinearLayoutManager(this)
        rvLowStock.adapter = lowStockAdapter

        //image pick
        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                if (uri != null) {
                    imgProduct.setImageURI(uri)
                    selectImageUri = uri
                }
            }


        imgProduct.setOnClickListener { pickImage.launch("image/*") }
        categoryviewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        adapter = CategoryAdapter { category -> showDeleteDialog(category) }
        rvCategory.layoutManager = GridLayoutManager(this, 2)
        rvCategory.adapter = adapter


        //  LiveData observe
        categoryviewModel.categories.observe(this) { adapter.submitList(it) }

        btnAddNewCategory.setOnClickListener {
            if (isselectedbtn) {
                cardViewcategory.visibility= View.VISIBLE
            } else {
                cardViewcategory.visibility= View.GONE
            }
            isselectedbtn = !isselectedbtn
        }
        btnAdd.setOnClickListener {
            val name = etCategory.text.toString().trim()
            if (name.isNotEmpty()) {
                categoryviewModel.addCategory(name)
                etCategory.text.clear()
            }
            cardViewcategory.visibility= View.GONE
        }


        viewModel = ViewModelProvider(this)[ManageViewModel::class.java]

        // low stock alert
        lifecycleScope.launchWhenStarted {
            viewModel.lowStockProducts.collect { list ->

                if (list.isNotEmpty()) {
                    cardLowStock.visibility = View.VISIBLE
                    lowStockAdapter.submitList(list)

                    // blink animation start
                    val blink = AnimationUtils.loadAnimation(this@MainActivity, R.anim.blink)
                    cardLowStock.startAnimation(blink)

                } else {
                    cardLowStock.clearAnimation()
                    cardLowStock.visibility = View.GONE
                }
            }
        }

        spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            mutableListOf()
        )
        spinnerAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spCategory.adapter = spinnerAdapter

        categoryviewModel.categories.observe(this) { categories ->

            categoryList = categories

            val names = categories.map { it.name }

            spinnerAdapter.clear()
            spinnerAdapter.addAll(names)
            spinnerAdapter.notifyDataSetChanged()
        }
        spCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (categoryList.isEmpty()) return

                    val categoryId = categoryList[position].id
                    viewModel.selectCategory(categoryId)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }



        backbtn.setOnClickListener {
            val intent = Intent(this, ProductActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        btnAddNewProduct.setOnClickListener {
            if (isselectedproduct) {
                cardView.visibility= View.VISIBLE
            } else {
                cardView.visibility= View.GONE
            }
            isselectedproduct = !isselectedproduct
        }

//        btnSaveProduct.setOnClickListener {
//            var name = etProductName.text.toString()
//            var price = etProductPrice.text.toString().toIntOrNull()
//            var costprice = etcostProductPrice.text.toString().toIntOrNull()
//            var stock = etstock.text.toString()
//            var position = spCategory.selectedItemPosition
//
//            if (name.isEmpty() || price == null || costprice==null || position == -1 ) {
//                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//           // var imagePath = copyUriToInternal(this, selectImageUri)
//            val categoryId = categoryList[position].id
//
//            val finalImagePath = if (::selectImageUri.isInitialized) {
//                copyUriToInternal(this, selectImageUri)
//            } else {
//                currentImagePath   // image မပြောင်းရင် old image
//            }
//           // val barcodeData = name
//            val barcodeData = "P" + System.currentTimeMillis()
//            Log.d("Barcode :",barcodeData)
//            if (editingProductId == null) {
//                // SAVE
//                viewModel.saveProduct(name, price, categoryId, finalImagePath, stock.toInt(),barcodeData,costprice)
//            } else {
//                //  UPDATE
//                viewModel.updateProduct(ProductEntity(id = editingProductId!!, name = name, price = price, categoryId = categoryId, imageUri = finalImagePath, stock.toInt(),barcodeData,costprice))
//            }
//
//
//           resetForm()
//
//        }
        val rvProductManage=findViewById<RecyclerView>(R.id.rvProductManage)
        val productManageAdapter = ProductManagePagingAdapter(
            onEdit = {
                openEditProduct(it)
            },
            onDelete = {
                // viewModel.deleteProduct(it)
                DeleteDialog(it)
            }
        )



        rvProductManage.layoutManager = LinearLayoutManager(this)
        rvProductManage.adapter = productManageAdapter
        btnSaveProduct.setOnClickListener {
            val name = etProductName.text.toString().trim() // Added trim() to avoid space errors
            val price = etProductPrice.text.toString().toIntOrNull()
            val costprice = etcostProductPrice.text.toString().toIntOrNull()
            val stock = etstock.text.toString()
            val position = spCategory.selectedItemPosition

            if (name.isEmpty() || price == null || costprice == null || position == -1) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // --- FIX: CHECK FOR DUPLICATE NAME ---
            // Only check for duplicates if we are ADDING a new product (editingProductId == null)
            if (editingProductId == null) {
                val isDuplicate = productManageAdapter.snapshot().items.any {
                    it.productName.equals(name, ignoreCase = true)
                }

                if (isDuplicate) {
                    Toast.makeText(this, "Product '$name' already exists!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }
            // --------------------------------------

            val categoryId = categoryList[position].id

            val finalImagePath = if (::selectImageUri.isInitialized) {
                copyUriToInternal(this, selectImageUri)
            } else {
                currentImagePath
            }

            val barcodeData = "P" + System.currentTimeMillis()

            if (editingProductId == null) {
                // SAVE
                viewModel.saveProduct(name, price, categoryId, finalImagePath, stock.toInt(), barcodeData, costprice)
                Toast.makeText(this, "Product Saved Successfully", Toast.LENGTH_SHORT).show()
            } else {
                // UPDATE
                viewModel.updateProduct(ProductEntity(
                    id = editingProductId!!,
                    name = name,
                    price = price,
                    categoryId = categoryId,
                    imageUri = finalImagePath,
                    stock = stock.toInt(),
                    barcode = barcodeData,
                    costPrice = costprice
                ))
                Toast.makeText(this, "Product Updated Successfully", Toast.LENGTH_SHORT).show()
            }

            resetForm()
        }


        btncancel.setOnClickListener {
            cardView.visibility=View.GONE
        }




        val layoutempty=findViewById<View>(R.id.layoutEmpty)
        lifecycleScope.launch {
            productManageAdapter.loadStateFlow.collectLatest { loadState ->

                val isListEmpty = loadState.refresh is LoadState.NotLoading && productManageAdapter.itemCount == 0

                layoutempty.visibility = if (isListEmpty) View.VISIBLE else View.GONE
                rvProductManage.visibility = if (isListEmpty) View.GONE else View.VISIBLE
            }
        }



        lifecycleScope.launch {
            viewModel.manageSearchProducts.collectLatest {
                productManageAdapter.submitData(it)
            }
        }

    }

    private fun DeleteDialog(product: ProductWithCategory) {
        AlertDialog.Builder(this)
            .setTitle("Delete Category")
            .setMessage("${product.productName} ကို ဖျက်မလား?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteProduct(product)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteDialog(category: CategoryEntity) {
        AlertDialog.Builder(this)
            .setTitle("Delete Category")
            .setMessage("${category.name} ကို ဖျက်မလား?")
            .setPositiveButton("Delete") { _, _ ->
                categoryviewModel.deleteCategory(category)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    fun copyUriToInternal(context: Context, uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, "product_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)

        inputStream?.copyTo(outputStream)

        inputStream?.close()
        outputStream.close()

        return file.absolutePath
    }

    private fun openEditProduct(product: ProductWithCategory) {

        editingProductId = product.productId
        currentImagePath = product.imageUri

        cardView.visibility = View.VISIBLE

        etProductName.setText(product.productName)
        etProductPrice.setText(product.price.toString())
        etcostProductPrice.setText(product.costPrice.toString())
        etstock.setText(product.stock.toString())

        val index = categoryList.indexOfFirst {
            it.name == product.categoryName
        }
        if (index >= 0) {
            spCategory.setSelection(index)
        }
        product.imageUri?.let {
            imgProduct.setImageURI(Uri.fromFile(File(it)))
        }
        btnSaveProduct.text = "Update Product"

    }

    private fun resetForm() {
        editingProductId = null
        currentImagePath = null
        etProductName.text.clear()
        etProductPrice.text.clear()
        etcostProductPrice.text.clear()
        etstock.text.clear()
        spCategory.setSelection(0)
        imgProduct.setImageResource(R.drawable.ic_launcher_foreground)
        btnSaveProduct.text = "Save Product"
        cardView.visibility = View.GONE
    }

    private fun showEditStockDialog(item: ProductEntity) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_update_stock, null)

        val tvProductName = dialogView.findViewById<TextView>(R.id.tvProductName)
        val etStock = dialogView.findViewById<EditText>(R.id.etStock)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnUpdate = dialogView.findViewById<Button>(R.id.btnUpdate)

        tvProductName.text = item.name
        etStock.setText(item.stock.toString())

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnUpdate.setOnClickListener {
            val newStock = etStock.text.toString().toIntOrNull()

            if (newStock == null) {
                Toast.makeText(this, "Enter valid stock", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.updateStock(item.id, newStock)

            Toast.makeText(this, "Stock updated", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        dialog.window?.setDimAmount(0.8f)
        dialog.show()
    }


}