package com.ptnet.core.android.SecondActivity

import android.content.Context
import android.os.Bundle
import android.os.Looper
import android.os.PersistableBundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.ptnet.core.android.networks.IpConfigHelper
import com.ptnet.core.android.networks.SocketClient
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class ConnectSocketActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConnectActivityView()
        }
    }

    val mainScope = MainScope()

    @Preview(
        showSystemUi = true
    )
    @Composable
    fun ConnectActivityView() {
        val mContext: Context = LocalContext.current
        val IP_ADDRESS = IpConfigHelper.getIPAddress(true)

        var message by remember { mutableStateOf("Hello, from android to the world") }
        // --
        var hostText by remember { mutableStateOf("192.168.15.94") }
        var portText by remember { mutableStateOf("6600") }

        var socketClient = SocketClient(hostText, portText.toInt())

        var isConnected by remember {
            mutableStateOf(false)
        }
        Column(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
//            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {


                OutlinedTextField(
                    value = hostText,
                    onValueChange = { hostText = it },

                    label = {
                        Row {
                            Icons.Rounded.MailOutline
                            Text(text = "IP")
                        }
                    },
                    trailingIcon = {
                        IconButton(onClick = { hostText = "" }) {
                            Icon(Icons.Rounded.Clear, contentDescription = "Clear")
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                )
                OutlinedTextField(
                    value = portText,
                    onValueChange = { portText = it },
                    label = {
                        Row {
                            Icons.Rounded.MailOutline
                            Text(text = "Port")
                        }
                    },
                    trailingIcon = {
                        IconButton(onClick = { portText = "" }) {
                            Icon(Icons.Rounded.Clear, contentDescription = "Clear")
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                )
            }


            OutlinedTextField(value = message, onValueChange = { message = it },
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                label = {
                    Row {
                        Icons.Rounded.MailOutline
                        Text(text = "Message")
                    }
                },
                trailingIcon = {
                    IconButton(onClick = {
                        message = ""
                    }) {
                        Icon(
                            Icons.Rounded.Clear,
                            contentDescription = "Clear"
                        )
                    }
                }
            )

            Row(
                Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        thread {
                            Looper.prepare() // Prepare a Looper for this thread
                            socketClient.create()
                            isConnected = socketClient.isConnected()
                            if (!isConnected) {
                                Toast.makeText(mContext, "Connection error!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            Looper.loop() // Run the message loop
                        }
                    },
                    Modifier.padding(4.dp), shape = RectangleShape,
//                    enabled = !isConnected
                ) {
                    Text(
                        text = "Connect",
                        fontSize = 16.sp
                    )
                }

                Button(
                    onClick = {
                        thread {
                            socketClient.close()
                            isConnected = socketClient.isConnected()
                        }
                    },
                    Modifier.padding(4.dp), shape = RectangleShape,
//                    enabled = isConnected
                ) {
                    Text(
                        text = "Close",
                        fontSize = 16.sp
                    )
                }

                Button(
                    onClick = {
                        thread {
                            socketClient.sendMessage(message)
                            isConnected = socketClient.isConnected()
                        }
                    },
                    Modifier.padding(4.dp), shape = RectangleShape,
//                    enabled = isConnected
                ) {
                    Text(
                        text = "Send message",
                        fontSize = 16.sp
                    )
                }
            }
            Text(
                text = "Ip Address: $IP_ADDRESS",
                fontSize = 16.sp
            )
            Text(
                text = "Server host: $hostText, port: $portText",
                fontSize = 16.sp
            )
            Text(
                text = "Connection: ${isConnected.toString().uppercase()}",
                fontSize = 16.sp
            )
        }
    }
}