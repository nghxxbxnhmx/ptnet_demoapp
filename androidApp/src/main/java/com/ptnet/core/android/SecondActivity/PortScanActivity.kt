package com.ptnet.core.android.SecondActivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

class PortScanActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PortScanContent()
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PortScanContent() {
    Surface {
        var address by remember { mutableStateOf("hi.fpt.vn") }
        var startPort by remember { mutableStateOf("1") }
        var endPort by remember { mutableStateOf("1023") }
        var isScanning by remember { mutableStateOf(false) }
        var progressPercentage by remember { mutableStateOf(0) }
        var openPortJson by remember { mutableStateOf("[]") }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            TextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                TextField(
                    value = startPort,
                    onValueChange = { startPort = it },
                    label = { Text("Start Port") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                TextField(
                    value = endPort,
                    onValueChange = { endPort = it },
                    label = { Text("End Port") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (!isScanning) {
                        startPortScan(
                            address,
                            startPort.toInt(),
                            endPort.toInt(),
                            onProgressUpdate = { progressPercentage = it },
                            onScanFinish = { isScanning = false; progressPercentage = 0 }
                        )
                        isScanning = true
                    }
                }
            ) {
                Text(if (isScanning) "Stop Scan" else "Start Port Scan")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Progress: $progressPercentage%")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Open Ports (JSON): $openPortJson")
        }
    }
}

fun startPortScan(
    address: String,
    startPort: Int,
    endPort: Int,
    onProgressUpdate: (Int) -> Unit,
    onScanFinish: () -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        val openPortList = mutableListOf<Int>()
        var scannedPorts = 0

        for (port in startPort..endPort) {
            try {
                val socket = Socket()
                socket.connect(InetSocketAddress(address, port), 2000)
                socket.close()
                openPortList.add(port)

            } catch (e: IOException) {
                // Port is closed or not reachable
            }

            scannedPorts++
            val progress = ((scannedPorts.toFloat() / (endPort - startPort + 1)) * 100).toInt()
            onProgressUpdate(progress)
        }
        onScanFinish()
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PortScanContent()
}