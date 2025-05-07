package com.vipul.bluetoothpair

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.vipul.bluetoothpair.permissionHelper.BluetoothPermissionHandler
import com.vipul.bluetoothpair.ui.screens.DeviceListScreen
import com.vipul.bluetoothpair.ui.theme.BluetoothPairTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BluetoothPairTheme {
                BluetoothPermissionHandler {
                    DeviceListScreen()
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BluetoothPairTheme {
        DeviceListScreen()
    }
}