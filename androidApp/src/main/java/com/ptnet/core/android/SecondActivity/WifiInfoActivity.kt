package com.ptnet.core.android.SecondActivity

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.MutableLiveData
import com.ptnet.core.android.networks.IpConfigHelper
import com.ptnet.core.android.networks.PermissionHelper

class WifiInfoActivity: ComponentActivity() {
    var isPermissionGranted = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WifiInfoView(isPermissionGranted)
        }
        if(!PermissionHelper.isPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            PermissionHelper.showPermissionDialog(this, Manifest.permission.ACCESS_FINE_LOCATION, 2002) { allow ->
                isPermissionGranted = allow
                handlePermissionResult()
            }
        } else {
            isPermissionGranted = true
            handlePermissionResult()
        }
    }

    private fun handlePermissionResult() {
        if (isPermissionGranted) {
            Toast.makeText(this, "ACCESS_FINE_LOCATION granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "PT Net needs access to your location to function properly.", Toast.LENGTH_SHORT).show()
            finish()
        }
        setContent {
            WifiInfoView(isPermissionGranted)
        }
    }
}

@Composable
fun WifiInfoView(isPermissionGranted: Boolean) {
    MaterialTheme {
        if (isPermissionGranted) {
            val mContext = LocalContext.current

            val ipAddress: String = IpConfigHelper.getIPAddress(true)
            val subnet: String = IpConfigHelper.subnetMask
            val gateway: String = IpConfigHelper.defaultGateway
            val macAddress: String = IpConfigHelper.getMacAddress(mContext)
            val bssid: String = IpConfigHelper.getBSSID(mContext)
            val ssid: String = IpConfigHelper.getSSID(mContext)

            Column(
                Modifier.fillMaxSize()
            ) {
                Text(text = "IP Address: $ipAddress")
                Text(text = "Subnet Mask: $subnet")
                Text(text = "Default Gateway: $gateway")
                Text(text = "Physical Address: $macAddress")
                Text(text = "BSSID modem: $bssid")
                Text(text = "SSID: $ssid")
            }
        } else {
            Text("Please grant permission to access location.")
        }
    }
}