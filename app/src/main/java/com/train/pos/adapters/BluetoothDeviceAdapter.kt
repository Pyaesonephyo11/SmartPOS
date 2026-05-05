package com.train.pos.adapters

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresPermission
import androidx.recyclerview.widget.RecyclerView
import com.train.pos.R

class BluetoothDeviceAdapter(
    private val onClick: (BluetoothDevice) -> Unit
) : RecyclerView.Adapter<BluetoothDeviceAdapter.VH>() {

    private val devices = mutableListOf<BluetoothDevice>()

    fun submit(list: List<BluetoothDevice>) {
        devices.clear()
        devices.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvName: TextView = v.findViewById(R.id.tvblueName)
        val tvAddress: TextView = v.findViewById(R.id.tvblueAddress)
    }

    override fun onCreateViewHolder(p: ViewGroup, v: Int): VH {
        val view = LayoutInflater.from(p.context)
            .inflate(R.layout.item_bluetooth_device, p, false)
        return VH(view)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onBindViewHolder(h: VH, pos: Int) {
        val device = devices[pos]
        h.tvName.text = device.name ?: "Unknown"
        h.tvAddress.text = device.address

        h.itemView.setOnClickListener {
            onClick(device)
        }
    }

    override fun getItemCount() = devices.size
}
