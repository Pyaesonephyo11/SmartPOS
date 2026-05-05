package com.train.pos.fragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.train.pos.R
import com.train.pos.ShopPref
import com.train.pos.model.ShopInfo

class ShopSettingFragment : Fragment(R.layout.fragment_shop_setting) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etName = view.findViewById<EditText>(R.id.etShopName)
        val etAddress = view.findViewById<EditText>(R.id.etAddress)
        val etPhone = view.findViewById<EditText>(R.id.etPhone)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        // --- 1. Load and display existing data ---
        val savedShop = ShopPref.load(requireContext())
        etName.setText(savedShop.shopName)
        etAddress.setText(savedShop.address)
        etPhone.setText(savedShop.phone)

        // --- 2. Save new data ---
        btnSave.setOnClickListener {

            val name = etName.text.toString()
            val address = etAddress.text.toString()
            val phone = etPhone.text.toString()

            if (name.isEmpty()) {
              //  Toast.makeText(requireContext(), "Please enter Shop Name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val info = ShopInfo(
                shopName = name,
                address = address,
                phone = phone
            )

            ShopPref.save(requireContext(), info)
           // Toast.makeText(requireContext(), "Settings Saved", Toast.LENGTH_SHORT).show()
        }
    }
}