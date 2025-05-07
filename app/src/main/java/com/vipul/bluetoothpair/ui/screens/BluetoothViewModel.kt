package com.vipul.bluetoothpair.ui.screens

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Context.BLUETOOTH_SERVICE
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val bluetoothAdapter: BluetoothAdapter? =
        (context.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager).adapter

    var deviceList = mutableStateListOf<BluetoothDevice>()
        private set

    private val _isStartDiscovery = MutableStateFlow(false)
    val isStartDiscovery: StateFlow<Boolean> = _isStartDiscovery

    fun enableStartDiscovery() {
        _isStartDiscovery.value = true
    }

    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    fun startDiscover() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) return
        }
        deviceList.clear()
        bluetoothAdapter?.startDiscovery()
    }

    fun cancelDiscovery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) return
        }
        bluetoothAdapter?.cancelDiscovery()
    }


    fun pairDevice(device: BluetoothDevice, onResult: (String) -> Unit) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) return
            }
            val method = device.javaClass.getMethod("createBond")
            val result = method.invoke(device) as Boolean

            if (result) {
                onResult("Initiating pairing with ${device.name}")
            } else {
                onResult("Failed to initiate pairing with ${device.name}")
            }
        } catch (e: Exception) {
            onResult("Pairing error: ${e.message}")
        }
    }

    fun handleDeviceFound(device: BluetoothDevice) {
        if (deviceList.none { it.address == device.address }) {
            deviceList.add(device)
        }
    }

}