package com.train.pos

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.train.pos.CartManager.cartItems
import com.train.pos.ViewModel.ManageViewModel
import com.train.pos.adapters.CheckoutAdapter
import com.train.pos.adapters.SaleHistoryPagingAdapter
import com.train.pos.database.AppDatabase
import com.train.pos.ProductActivity
import com.train.pos.adapters.GeneralAdapter
import com.train.pos.fragment.BracodeGeneratorFragment
import com.train.pos.fragment.MonthlyFragment
import com.train.pos.fragment.SaleHistoryFragment
import com.train.pos.fragment.YearReportFragment
import com.train.pos.model.General
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SaleHistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sale_history)
        val rvsaleh = findViewById<RecyclerView>(R.id.rvsaleh)
        val back=findViewById<Button>(R.id.backsale)

        val categories = listOf(General("Daily", true), General("Monthly"), General("Yearly"))
        rvsaleh.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvsaleh.adapter = GeneralAdapter(categories){


            if (it.name.equals("Daily")){
               replaceFragment(SaleHistoryFragment())
            }else if (it.name.equals("Monthly")){
                replaceFragment(MonthlyFragment())
            }else if(it.name.equals("Yearly")){
                replaceFragment(YearReportFragment())
            }
        }
        replaceFragment(SaleHistoryFragment())
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
