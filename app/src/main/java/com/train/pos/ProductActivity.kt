package com.train.pos

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.core.widget.doAfterTextChanged
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.train.pos.ViewModel.ManageViewModel
import com.train.pos.adapters.BluetoothDeviceAdapter
import com.train.pos.adapters.CartAdapter
import com.train.pos.adapters.CheckoutAdapter
import com.train.pos.adapters.DrawerMenuAdapter
import com.train.pos.adapters.SeeCategoryAdapter
import com.train.pos.adapters.SeeProductPagingAdapter
import com.train.pos.database.AppDatabase
import com.train.pos.entries.CategoryEntity
import com.train.pos.entries.SaleEntity
import com.train.pos.entries.SaleItemEntity
import com.train.pos.model.CartItem
import com.train.pos.model.ShopInfo
import com.train.pos.printer.PaperWidth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.properties.Delegates

class ProductActivity : AppCompatActivity() {

    lateinit var viewModel: ManageViewModel
    var isselected: Boolean=false
    //new update
    lateinit var cartAdapter: CartAdapter
    lateinit var  tvSubtotal: TextView
    lateinit var  cartCount: TextView
    var receiveAMT: Int = 0
    lateinit var  btnCheckOut: Button
    lateinit var emptyCartLayout: View

    lateinit var invoiceNo: String



    private val scannerLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                val barcode = it.data?.getStringExtra("barcode")
                barcode?.let { code ->
                    viewModel.loadProductByBarcode(code)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)


        // --- Notification Bar Color Fix ---
        window.statusBarColor = ContextCompat.getColor(this, R.color.white) // မိမိနှစ်သက်ရာ Color ပြောင်းနိုင်သည်
        WindowCompat.getInsetsController(window, window.decorView).apply {
            // Status Bar အရောင်က အဖြူ (သို့မဟုတ်) အဖျော့ရောင်ဆိုလျှင် Icon လေးများကို အမည်းရောင်ပြောင်းရန် true ထားပါ
            isAppearanceLightStatusBars = true
        }
        setContentView(R.layout.activity_product)

        val rv = findViewById<RecyclerView>(R.id.recyclerProducts)
        val rvCategory = findViewById<RecyclerView>(R.id.rvCategory)

        //new update
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val btnCart = findViewById<ImageView>(R.id.btnCart)
        cartCount = findViewById<TextView>(R.id.txtCartCount)
        val btnBarcode = findViewById<ImageView>(R.id.bc)
        val btnSaleHistory = findViewById<ImageView>(R.id.btnSaleHistory)
        val btnSettings = findViewById<ImageView>(R.id.btnSettings)
        btnCheckOut = findViewById<Button>(R.id.btnCheckOut)
        emptyCartLayout = findViewById(R.id.layoutEmptyCart)
        val etsearch = findViewById<EditText>(R.id.edtSearch)
        val scan = findViewById<ImageView>(R.id.imgscan)
        val progressLoading = findViewById<ProgressBar>(R.id.progressLoading)

        //new update
        cartAdapter = CartAdapter(CartManager.cartItems) {
            // Qty တိုး/လျော့တိုင်းရောက်သွားတဲ့ callback
            updateCartUI()
        }

        tvSubtotal = findViewById<TextView>(R.id.tvSubtotal)
        val rvCart = findViewById<RecyclerView>(R.id.card_recycler)
        rvCart.adapter = cartAdapter
        rvCart.layoutManager = LinearLayoutManager(this)
        val categoryAdapter = SeeCategoryAdapter {
            viewModel.selectCategory(it.id)
        }
        rvCategory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvCategory.adapter = categoryAdapter


        viewModel = ViewModelProvider(this).get(ManageViewModel::class.java)
        viewModel.categories.observe(this) { list ->
        //    categoryAdapter.submitList(list)
            val all = CategoryEntity(id = 0, name = "All Items")
            categoryAdapter.submitList(listOf(all) + list)
        }


        val adapter = SeeProductPagingAdapter {
            cartCount.visibility = View.VISIBLE
            // Cart counter update
            cartCount.text = CartManager.getTotalQty().toString()
            btnCheckOut.isEnabled = true
        } //paging

        rv.layoutManager = GridLayoutManager(this, 2)
        rv.adapter = adapter


        //paging
        lifecycleScope.launch {
            viewModel.products.collectLatest {
                adapter.submitData(it)

            }
        }


        val layoutempty=findViewById<View>(R.id.layout_Empty)

//        lifecycleScope.launch {
//            adapter.loadStateFlow.collectLatest { loadState ->
//
//                val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
//
//                layoutempty.visibility = if (isListEmpty) View.VISIBLE else View.GONE
//                rv.visibility = if (isListEmpty) View.GONE else View.VISIBLE
//            }
//        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadState ->

                // Loading state
                val isLoading = loadState.refresh is LoadState.Loading

                progressLoading.visibility =
                    if (isLoading) View.VISIBLE else View.GONE

                // Empty state
                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading &&
                            adapter.itemCount == 0

                layoutempty.visibility =
                    if (isListEmpty) View.VISIBLE else View.GONE

                rv.visibility =
                    if (isListEmpty || isLoading) View.GONE else View.VISIBLE
            }
        }

        //all product search
        lifecycleScope.launch {
            viewModel.searchproducts.collectLatest {
                adapter.submitData(it)
            }
        }



//        viewModel.scannedProduct.observe(this) { product ->
//            if (product != null) {
//                CartManager.addProductStock(product)
//                updateCartUI()
//            } else {
//                Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show()
//            }
//        }

        viewModel.scannedProduct.observe(this) { product ->
            if (product != null) {
                val currentInCart = CartManager.cartItems.find { it.productId == product.id }?.qty ?: 0

                if (currentInCart < product.stock) {
                    CartManager.addProductStock(product)
                    updateCartUI()
                } else {
                    Toast.makeText(this, "Out of Stock! (လက်ကျန်မလုံလောက်ပါ)", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show()
            }
        }

        scan.setOnClickListener {
            startScanner()
        }


        //new update
        btnCart.setOnClickListener {
            cartAdapter.notifyDataSetChanged()
            // ပထမဆုံးရောက်လာလျှင် UI အချက်အလက်
            updateCartUI()
            drawerLayout.openDrawer(GravityCompat.END)
        }
        btnSettings.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btnSaleHistory.setOnClickListener {
            val intent= Intent(this, SaleHistoryActivity::class.java)
            startActivity(intent)
        }
        btnBarcode.setOnClickListener {
            val intent= Intent(this, BarcodeActivity::class.java)
            startActivity(intent)
        }
        btnCheckOut.setOnClickListener {
            showCheckoutDialog()
        }
        etsearch.doAfterTextChanged {
            viewModel.search(it.toString())
        }


    }
    override fun onBackPressed() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

    private fun updateCartUI() {
        val subtotal = CartManager.getTotalAmount()
        tvSubtotal.text = String.format("%,d Ks", subtotal)

        val totalQty = CartManager.getTotalQty()
        if (totalQty > 0) {
            emptyCartLayout.visibility = View.GONE
            cartCount.visibility = View.VISIBLE
            cartCount.text = totalQty.toString()
            btnCheckOut.isEnabled=true
        } else {
            emptyCartLayout.visibility = View.VISIBLE
            cartCount.visibility = View.GONE
            btnCheckOut.isEnabled=false
        }
        cartAdapter.notifyDataSetChanged()
    }

    private fun showCheckoutDialog() {
//        val dialog = BottomSheetDialog(this)
//        val dialogView = layoutInflater.inflate(R.layout.layoutdialog_checkout, null)
//        dialog.setContentView(dialogView)

        val dialogView = layoutInflater.inflate(R.layout.layoutdialog_checkout, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val tvTotal = dialogView.findViewById<TextView>(R.id.tvTotal)
        val tvtime = dialogView.findViewById<TextView>(R.id.tvcheckouttime)
        val shopname = dialogView.findViewById<TextView>(R.id.tvshopname)
        val shopaddress = dialogView.findViewById<TextView>(R.id.tvaddress)
        val invno = dialogView.findViewById<TextView>(R.id.tvInvoice)
        val etReceived = dialogView.findViewById<EditText>(R.id.etReceived)
        val tvChange = dialogView.findViewById<TextView>(R.id.tvChange)
     //   val btnSave = dialogView.findViewById<Button>(R.id.btnSave)
        val btnPrint = dialogView.findViewById<Button>(R.id.btnPrint)
        val rv = dialogView.findViewById<RecyclerView>(R.id.rvCheckoutItems)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = CheckoutAdapter(CartManager.cartItems)


        val shop = ShopPref.load(this)
        shopname.text=shop.shopName
        shopaddress.text=shop.address
        invoiceNo = InvoiceUtil.generate()
        invno.text=invoiceNo

        val totalAmount = CartManager.getTotalAmount()
        tvTotal.text = String.format("%,d Ks", totalAmount)

        tvtime.text = SimpleDateFormat("MM/dd/yyyy,\nhh:mm a", Locale.getDefault()).format(
            Date(System.currentTimeMillis()))

        etReceived.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val received = s.toString().toIntOrNull() ?: 0
                receiveAMT=received
                val change= received - totalAmount
                val changetotal=String.format("%,d Ks", change)
                if (change >= 0) {
                    tvChange.text = "Change (ပြန်အမ်းငွေ) : $changetotal"
                    tvChange.setTextColor(Color.parseColor("#208968")) // green
                   // btnSave.isEnabled = true
                    btnPrint.isEnabled = true
                } else {
                    tvChange.text = "Change (ပြန်အမ်းငွေ): $changetotal"
                    tvChange.setTextColor(Color.RED)
                 //   btnSave.isEnabled = false
                    btnPrint.isEnabled = false
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

//        btnSave.setOnClickListener {
//            dialog.dismiss()
//        }
        btnPrint.setOnClickListener {
            saveSale(receiveAMT,invoiceNo)
            printReceipt(received = receiveAMT, total = CartManager.getTotalAmount(),invoiceNo)
            cartCount.visibility = View.GONE
            tvSubtotal.text="0 Ks"
            viewModel.checkout(CartManager.cartItems)
            dialog.dismiss()
        }
        rv.adapter?.notifyDataSetChanged()
        dialog.window?.setDimAmount(0.8f)
        dialog.show()
    }
    private fun saveSale(receiveAMT: Int,invoiceNo: String) {
        val db = AppDatabase.Companion.getDatabase(this)

        lifecycleScope.launch {

            val sale = SaleEntity(
                invoiceNo = invoiceNo,
                dateTime = System.currentTimeMillis(),
                totalAmount = CartManager.getTotalAmount(),
                status = "Completed",
                receivedAmount = receiveAMT
            )

            val saleId = db.saleDao().insertSale(sale).toInt()
            val items = CartManager.cartItems.map {
                SaleItemEntity(
                    saleId = saleId, productName = it.name, qty = it.qty,
                    price = it.price, productId = it.productId, costPrice = it.costPrice, createAt = System.currentTimeMillis()
                )
            }
            db.saleDao().insertSaleItems(items)
            CartManager.clear()
        }
    }
    //Scanner Activity ကို ဖွင့်
    private fun startScanner() {
        val intent = Intent(this, BarcodeScannerActivity::class.java)
        scannerLauncher.launch(intent)
    }


    private fun printReceipt(received: Int, total: Int, invoiceNo: String) {
        val shop = ShopPref.load(this)
        val items = ArrayList(CartManager.cartItems)

        if (items.isEmpty()) return

        // --- ၁။ Loading Dialog စဖွင့်မည် ---
        val progressDialog = AlertDialog.Builder(this)
            .setView(layoutInflater.inflate(R.layout.layout_loading_dialog, null)) // Custom Layout တစ်ခုဆောက်ထားပါ
            .setCancelable(false) // ပိတ်လို့မရအောင်လုပ်ထားမည်
            .create()

        progressDialog.show()

        val width = 384
        val bitmap = createReceiptBitmap(shop, items, total, received, invoiceNo, width)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                if (PrinterManager.isConnected()) {
                    PrinterManager.reset()
                    PrinterManager.printBitmap(bitmap)
                    PrinterManager.feed(3)

                    withContext(Dispatchers.Main) {
                        // --- ၂။ ခဏစောင့်ပြီး Dialog ပိတ်မည် ---
                        delay(1000)
                        progressDialog.dismiss()
                       // Toast.makeText(this@ProductActivity, "Printing Completed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Toast.makeText(this@ProductActivity, "Printer not connected!", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Toast.makeText(this@ProductActivity, "Print error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun createReceiptBitmap(
        shop: ShopInfo,
        items: List<CartItem>,
        total: Int,
        received: Int,
        invoice: String,
        width: Int
    ): android.graphics.Bitmap {

        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 20f
            // Inside createReceiptBitmap
            try {
                typeface = android.graphics.Typeface.createFromAsset(assets, "pyidaungsubold.ttf")
            } catch (e: Exception) {
                typeface = android.graphics.Typeface.DEFAULT
            }

            isAntiAlias = true
        }

        val headerPaint = android.graphics.Paint(paint).apply {
            textSize = 24f
            typeface = android.graphics.Typeface.create(paint.typeface, android.graphics.Typeface.BOLD)
        }

        // Calculate height: Header(150) + Items(size * 35) + Footer(150)
        val height = 300 + (items.size * 35) + 150
        val bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.WHITE)

        var y = 40f
        val centerX = width / 2f
        val rightX = (width - 10).toFloat()

        // --- Header ---
        headerPaint.textAlign = android.graphics.Paint.Align.CENTER
        canvas.drawText(shop.shopName, centerX, y, headerPaint)
        y += 30
        paint.textAlign = android.graphics.Paint.Align.CENTER
        canvas.drawText(shop.address, centerX, y, paint)
        y += 40

        // --- Info ---
        paint.textAlign = android.graphics.Paint.Align.LEFT
        canvas.drawText("Inv: $invoice", 10f, y, paint)
        y += 25
        canvas.drawText("Date: ${SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault()).format(Date())}", 10f, y, paint)
        y += 20
        canvas.drawLine(10f, y, rightX, y, paint)
        y += 30

        // --- Items Loop ---
        items.forEach { item ->
            paint.textAlign = android.graphics.Paint.Align.LEFT
            // Myanmar + English Name
            canvas.drawText(item.name, 10f, y, paint)

            paint.textAlign = android.graphics.Paint.Align.RIGHT
            // Price Calculation
            val priceStr = "${item.qty} x ${String.format("%,d", item.price)}"
            canvas.drawText(priceStr, rightX, y, paint)
            y += 35
        }

        y += 5
        canvas.drawLine(10f, y, rightX, y, paint)
        y += 40

        // --- Footer Totals ---
        val formatter = java.text.NumberFormat.getInstance(Locale.US)

        paint.typeface = android.graphics.Typeface.create(paint.typeface, android.graphics.Typeface.BOLD)

        // Total
        paint.textAlign = android.graphics.Paint.Align.LEFT
        canvas.drawText("စုစုပေါင်း:", 10f, y, paint)
        paint.textAlign = android.graphics.Paint.Align.RIGHT
        canvas.drawText("${formatter.format(total)} Ks", rightX, y, paint)
        y += 35

        // Received
        paint.typeface = android.graphics.Typeface.DEFAULT
        paint.textAlign = android.graphics.Paint.Align.LEFT
        canvas.drawText("လက်ခံငွေ:", 10f, y, paint)
        paint.textAlign = android.graphics.Paint.Align.RIGHT
        canvas.drawText("${formatter.format(received)} Ks", rightX, y, paint)
        y += 35

        // Change
        paint.textAlign = android.graphics.Paint.Align.LEFT
        canvas.drawText("ပြန်အမ်းငွေ:", 10f, y, paint)
        paint.textAlign = android.graphics.Paint.Align.RIGHT
        canvas.drawText("${formatter.format(received - total)} Ks", rightX, y, paint)

        y += 60 // Space before footer
        paint.textAlign = android.graphics.Paint.Align.CENTER
        canvas.drawText("ကျေးဇူးအထူးတင်ပါသည်", centerX, y, paint)


        return bitmap
    }
    private fun format(amount: Int): String {
        return "%,d".format(amount)
    }
}