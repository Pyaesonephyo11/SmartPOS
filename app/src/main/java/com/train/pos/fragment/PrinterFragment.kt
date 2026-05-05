package com.train.pos.fragment

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.train.pos.PrinterManager
import com.train.pos.R
import com.train.pos.adapters.BluetoothDeviceAdapter
import java.util.UUID

class PrinterFragment : Fragment(R.layout.fragment_printer) {

    private lateinit var tvStatus: TextView
    private lateinit var btnTest: MaterialButton

    // 🔐 Permission launcher (Fragment-safe)
    private val bluetoothPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            val granted = result.values.all { it }
            if (granted) {
                showPrinterDialog()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Bluetooth permission denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        tvStatus = view.findViewById(R.id.tvStatus)
        val btnConnect = view.findViewById<MaterialButton>(R.id.btnConnect)
        btnTest = view.findViewById(R.id.btnTestPrint)

        updateStatus()

        btnConnect.setOnClickListener {
            if (hasBluetoothPermission()) {
                showPrinterDialog()
            } else {
                requestBluetoothPermission()
            }
        }

        btnTest.setOnClickListener {
            testPrint()
        }
    }

    // ---------------- PERMISSION ----------------

    private fun hasBluetoothPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    private fun requestBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            bluetoothPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
                )
            )
        }
    }

    // ---------------- UI STATUS ----------------

    private fun updateStatus() {
        if (PrinterManager.isConnected()) {
            tvStatus.text = "Connected: ${PrinterManager.getDeviceName()}"
            tvStatus.setTextColor(Color.GREEN)
            btnTest.isEnabled = true
        } else {
            tvStatus.text = "Not Connected"
            tvStatus.setTextColor(Color.RED)
            btnTest.isEnabled = false
        }
    }

    // ---------------- PRINTER DIALOG ----------------

    private fun showPrinterDialog() {

        if (!hasBluetoothPermission()) return

        val dialog = BottomSheetDialog(requireContext())
        val v = layoutInflater.inflate(R.layout.bottom_sheet_printer, null)
        dialog.setContentView(v)

        val rv = v.findViewById<RecyclerView>(R.id.rvDevices)

        val adapter = BluetoothDeviceAdapter { device ->
            connectPrinter(device)
            dialog.dismiss()
        }

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        val devices = BluetoothAdapter
            .getDefaultAdapter()
            ?.bondedDevices
            ?.toList()
            ?: emptyList()

        adapter.submit(devices)

        dialog.show()
    }

    // ---------------- CONNECT PRINTER ----------------

    private fun connectPrinter(device: BluetoothDevice) {

        if (!hasBluetoothPermission()) return

        Thread {
            try {
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery()

                val uuid =
                    UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

                val socket =
                    device.createRfcommSocketToServiceRecord(uuid)

                socket.connect()

                PrinterManager.setSocket(socket, device.name)

                requireActivity().runOnUiThread {
                    updateStatus()
                }

            } catch (e: Exception) {
                requireActivity().runOnUiThread {
                    tvStatus.text = "Connection failed"
                    tvStatus.setTextColor(Color.RED)
                    Toast.makeText(
                        requireContext(),
                        e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.start()
    }

    // ---------------- TEST PRINT ----------------

    private fun testPrint() {
        if (!PrinterManager.isConnected()) {
            Toast.makeText(
                requireContext(),
                "Printer not connected",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        Thread {
            PrinterManager.printText(
                "\nPOS Printer Test\n----------------\nConnected OK\n\n"
            )
        }.start()
    }
}
