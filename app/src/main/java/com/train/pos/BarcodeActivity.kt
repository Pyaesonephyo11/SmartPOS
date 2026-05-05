package com.train.pos


import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.train.pos.adapters.GeneralAdapter
import com.train.pos.entries.ProductEntity
import com.train.pos.fragment.BracodeGeneratorFragment
import com.train.pos.fragment.PrinterFragment
import com.train.pos.fragment.ShopSettingFragment
import com.train.pos.model.General
import com.train.pos.model.ProductSpinnerItem
import java.util.Locale.Category
import java.util.UUID

class BarcodeActivity : AppCompatActivity() {

    private var printerSocket: BluetoothSocket? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_barcode)
        val rvgeneral = findViewById<RecyclerView>(R.id.rvgeneral)
        val back = findViewById<Button>(R.id.back)


        val categories = listOf(General("My Shop", true), General("Bluetooth"), General("Barcode"))
        rvgeneral.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvgeneral.adapter = GeneralAdapter(categories){

            if (it.name.equals("My Shop")){
                replaceFragment(ShopSettingFragment())
            }else if (it.name.equals("Bluetooth")){
               replaceFragment(PrinterFragment())
            }else if(it.name.equals("Barcode")){
                replaceFragment(BracodeGeneratorFragment())
            }
        }
        replaceFragment(ShopSettingFragment())

        back.setOnClickListener {
            val intent = Intent(this, ProductActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameContainer, fragment)
            .commit()
    }

}
