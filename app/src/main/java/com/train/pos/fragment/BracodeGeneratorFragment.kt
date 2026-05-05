package com.train.pos.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.train.pos.PrinterManager
import com.train.pos.R
import com.train.pos.ViewModel.ManageViewModel
import com.train.pos.model.ProductSpinnerItem
import java.text.NumberFormat
import java.util.*

class BracodeGeneratorFragment : Fragment() {

    lateinit var spinner: Spinner
    lateinit var btnPrint: Button
    lateinit var imgBarcode: ImageView
    lateinit var tvName: TextView
    lateinit var tvPrice: TextView
    lateinit var etCount: EditText
    lateinit var viewModel: ManageViewModel
    private var productList: List<ProductSpinnerItem> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_barcode, container, false)

        spinner = view.findViewById(R.id.spbc)
        btnPrint = view.findViewById(R.id.btnbc)
        imgBarcode = view.findViewById(R.id.imgBc)
        tvName = view.findViewById(R.id.tvbcname)
        tvPrice = view.findViewById(R.id.tvpbc)
        etCount = view.findViewById(R.id.etCount)

        viewModel = ViewModelProvider(this).get(ManageViewModel::class.java)
        observeProducts()

        btnPrint.setOnClickListener {
            if (PrinterManager.isConnected()) {
                val position = spinner.selectedItemPosition
                if (position == -1) return@setOnClickListener

                val product = productList[position]
                val bitmap = generateBarcode(product.barcodeData)

                val countString = etCount.text.toString()
                val printCount = if (countString.isNotEmpty()) countString.toInt() else 1

                if (bitmap != null) {
                    printProduct(bitmap, product.name, product.price, printCount)
                }
            } else {
                Toast.makeText(requireContext(), "Printer not connected", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun observeProducts() {
        viewModel.spinnerProducts.observe(viewLifecycleOwner) { list ->
            if (list.isNullOrEmpty()) return@observe
            productList = list
            setupSpinner(list)
        }
    }

    private fun setupSpinner(list: List<ProductSpinnerItem>) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            list.map { "${it.name} - ${it.price} Ks" }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val product = list[position]
                val bitmap = generateBarcode(product.barcodeData)
                imgBarcode.setImageBitmap(bitmap)
                tvName.text = product.name
                tvPrice.text = "${product.price} Ks"
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    fun generateBarcode(data: String, width: Int = 1500, height: Int = 500): Bitmap? {
        return try {
            val barcodeEncoder = BarcodeEncoder()
            barcodeEncoder.encodeBitmap(data, BarcodeFormat.CODE_128, width, height)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun printProduct(barcodeBitmap: Bitmap, name: String, price: Int, count: Int) {
        if (!PrinterManager.isConnected()) return

        val combinedBitmap = createLabelBitmap(name, price, barcodeBitmap, 384)

        Thread {
            try {
                for (i in 1..count) {
                    PrinterManager.reset()
                    PrinterManager.printBitmap(combinedBitmap)
                    PrinterManager.feedLine(2)
                    Thread.sleep(200)
                }
                PrinterManager.feedLine(2)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun createLabelBitmap(name: String, price: Int, barcode: Bitmap, width: Int): Bitmap {
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 24f
            isAntiAlias = true
            try {
                typeface = android.graphics.Typeface.createFromAsset(requireContext().assets, "fonts/pyidaungsubold.ttf")
            } catch (e: Exception) {
                typeface = android.graphics.Typeface.DEFAULT
            }
        }

        val labelHeight = 250
        val bitmap = Bitmap.createBitmap(width, labelHeight, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.WHITE)

        val centerX = width / 2f
        paint.textAlign = android.graphics.Paint.Align.CENTER

        var y = 40f
        canvas.drawText(name, centerX, y, paint)

        y += 20
        val scaledBarcode = Bitmap.createScaledBitmap(barcode, width - 40, 100, false)
        canvas.drawBitmap(scaledBarcode, 20f, y, null)

        y += 130
        val formatter = NumberFormat.getInstance(Locale.US)
        val priceText = "${formatter.format(price)} Ks"
        canvas.drawText(priceText, centerX, y, paint)

        return bitmap
    }
}