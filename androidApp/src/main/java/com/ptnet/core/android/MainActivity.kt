package com.ptnet.core.android

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ptnet.core.android.SecondActivity.PageLoadActivity
import com.ptnet.core.android.SecondActivity.PingActivity
import com.ptnet.core.android.SecondActivity.PortScanActivity
import com.ptnet.core.android.SecondActivity.TracerouteActivity
import com.ptnet.core.android.SecondActivity.WifiInfoActivity
import com.ptnet.core.android.SecondActivity.WifiScanActivity
import com.ptnet.core.android.models.ButtonInfo
import com.ptnet.core.android.networks.IpConfigHelper
import com.ptnet.core.android.networks.PermissionHelper
import kotlin.concurrent.thread


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainView()
            if (!PermissionHelper.isPermissionGranted(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                PermissionHelper.showPermissionDialog(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    2002
                ) { allow ->
                }
            }
        }
    }
}


@Preview(
    showSystemUi = true
)
@Composable
fun MainView() {
    val mContext = LocalContext.current
    val buttons = listOf(
        ButtonInfo("WifiInfo", WifiInfoActivity::class.java, true),
        ButtonInfo("Ping", PingActivity::class.java, true),
        ButtonInfo("Page Load Timer", PageLoadActivity::class.java, true),
        ButtonInfo("Traceroute", TracerouteActivity::class.java, true),
        ButtonInfo("Wifi Scan", WifiScanActivity::class.java, false),
        ButtonInfo("Port Scan", PortScanActivity::class.java, false),
        )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(buttons) { buttonInfo ->
            Button(
                onClick = {
                    mContext.startActivity(Intent(mContext, buttonInfo.composeActivityClass))
                },
                shape = RectangleShape,
                modifier = Modifier.width(360.dp),
                enabled = buttonInfo.enable
            ) {
                Text(
                    text = buttonInfo.text,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


