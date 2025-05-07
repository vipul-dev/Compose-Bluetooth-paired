package com.vipul.bluetoothpair.ui.screens

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import com.vipul.bluetoothpair.utils.parcelable

@Composable
fun RegisterReceivers(context: Context, viewModel: BluetoothViewModel) {
    val discoveryReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val device =
                            intent.parcelable<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)

                        device?.let {
                            viewModel.handleDeviceFound(device)
                        }
                    }

                    BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                        Toast.makeText(context, "Discovery started", Toast.LENGTH_SHORT).show()
                    }

                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                        Toast.makeText(context, "Discovery finished", Toast.LENGTH_SHORT).show()
                        viewModel.cancelDiscovery()
                        context?.unregisterReceiver(this)
                    }
                }
            }

        }
    }

    val bondReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val device = intent?.parcelable<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ContextCompat.checkSelfPermission(
                            context!!,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) return
                }
                when (device?.bondState) {
                    BluetoothDevice.BOND_BONDING -> {
                        Toast.makeText(
                            context,
                            "Pairing with ${device.name}...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    BluetoothDevice.BOND_BONDED -> {
                        Toast.makeText(context, "Paired with ${device.name}", Toast.LENGTH_SHORT)
                            .show()
                    }

                    BluetoothDevice.BOND_NONE -> {
                        Toast.makeText(
                            context,
                            "Failed to pair with ${device.name}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        }
    }


    DisposableEffect(Unit) {
        val discoveryFilter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        val bondFilter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)

        context.registerReceiver(discoveryReceiver, discoveryFilter)
        context.registerReceiver(bondReceiver, bondFilter)

        onDispose {
            runCatching {
                context.unregisterReceiver(discoveryReceiver)
            }
            runCatching {
                context.unregisterReceiver(bondReceiver)
            }
        }
    }

}

