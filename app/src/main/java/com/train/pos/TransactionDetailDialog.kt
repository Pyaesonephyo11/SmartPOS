package com.train.pos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.train.pos.adapters.TransactionItemAdapter
import com.train.pos.model.SaleWithItems
import com.train.pos.printer.PrinterHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionDetailDialog : BottomSheetDialogFragment() {

    private lateinit var data: SaleWithItems

    companion object {
        fun newInstance(sale: SaleWithItems): TransactionDetailDialog {
            val d = TransactionDetailDialog()
            d.data = sale
            return d
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val v = inflater.inflate(R.layout.dialog_historyreprint, container, false)

        v.findViewById<TextView>(R.id.tvRepintInvoice).text=data.sale.invoiceNo
        v.findViewById<TextView>(R.id.tvReprintDate).text = SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault()).format(Date(data.sale.dateTime))
        v.findViewById<TextView>(R.id.tvReptintStatus).text = data.sale.status
        val rv = v.findViewById<RecyclerView>(R.id.rvReprintItems)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = TransactionItemAdapter(data.items)

        val total = data.items.sumOf { it.total }
        val change = data.sale.receivedAmount - total
        v.findViewById<TextView>(R.id.tvReprintTotal).text = String.format("%,d Ks", total)
        v.findViewById<TextView>(R.id.tvTeprintReceived).text = String.format("%,d Ks", data.sale.receivedAmount)
        v.findViewById<TextView>(R.id.tvReprintChange).text = String.format("%,d Ks", change)

        v.findViewById<Button>(R.id.btnRepintClose).setOnClickListener { dismiss() }
        v.findViewById<Button>(R.id.btnReprint).setOnClickListener {
            PrinterHelper.printSale(requireContext(), data)
        }

        return v
    }
}
