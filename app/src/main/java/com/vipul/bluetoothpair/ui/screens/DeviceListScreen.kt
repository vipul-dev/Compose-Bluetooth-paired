package com.vipul.bluetoothpair.ui.screens

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DeviceListScreen(
    modifier: Modifier = Modifier,
    viewModel: BluetoothViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val deviceList = viewModel.deviceList
    val snackBarHostState = remember { SnackbarHostState() }
    val isStartDiscovery by viewModel.isStartDiscovery.collectAsState()


    if (isStartDiscovery) {
        RegisterReceivers(context, viewModel)
        viewModel.startDiscover()
    }

    val enableBluetoothLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.enableStartDiscovery()
        }
    }

    LaunchedEffect(Unit) {
        if (viewModel.isBluetoothEnabled()) {
            viewModel.enableStartDiscovery()
        } else {
            enableBluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }
    }


    Scaffold(snackbarHost = { SnackbarHost(hostState = snackBarHostState) }) { padding ->
        Column(
            modifier = modifier
                .padding(12.dp)
                .fillMaxSize()
                .padding(padding)
        ) {

            Text("Bluetooth Devices", style = MaterialTheme.typography.headlineMedium)

            LazyColumn {

                items(deviceList) { device ->
                    Card(
                        modifier = Modifier
                            .padding(start = 12.dp, end = 12.dp)
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    if (ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.BLUETOOTH_CONNECT
                                        ) != PackageManager.PERMISSION_GRANTED
                                    ) return@clickable
                                }

                                if (device.bondState != BluetoothDevice.BOND_BONDED){
                                    viewModel.pairDevice(device){message->
                                        Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
                                    }
                                }else{
                                    Toast.makeText(context,"${device.name} is already paired",Toast.LENGTH_SHORT).show()
                                }
                            }
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Name: ${device.name ?: "Unknown"} ")
                            Text("Address: ${device.address}")
                            Text(if(device.bondState == BluetoothDevice.BOND_BONDED) "Connected" else "Not Connected")
                        }
                    }
                }
            }
        }

    }
}


//@Preview(showBackground = true)
//@Composable
//fun PreviewDeviceListScreen() {
//    DeviceListScreen()
//}