package com.ptnet.core.android.SecondActivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.ptnet.core.android.networks.Ping
import com.ptnet.core.android.networks.PingInetAddress
import com.ptnet.core.android.networks.PingProcessBuilder
import com.ptnet.core.android.utils.doAsync

class PingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PingView()
        }
    }
}

@Preview (showSystemUi = true)
@Composable
fun PingView() {
    Column (
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var hostTextField by remember { mutableStateOf("hi.fpt.vn") }
        var resultText by remember { mutableStateOf("") }

        OutlinedTextField(
            value = hostTextField,
            onValueChange = { hostTextField = it },
            label = { Text("URL") },
            trailingIcon = {
                IconButton(onClick = {
                    hostTextField = ""
                }) {
                    Icon(
                        Icons.Rounded.Clear,
                        contentDescription = "Clear"
                    )
                }
            }
        )

        Button(onClick = {
            doAsync {
                var ping = Ping(hostTextField)
                resultText = ping.execute()
                var pingContainer= ping.parsePingOutput(resultText)
                resultText += "\n ${Gson().toJson(pingContainer).toString()}"
            }
        },
            Modifier.padding(all = 8.dp),
            shape = RectangleShape
            ) {

            Text(text = "Ping")
        }
        Box (
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(text = "\n\n$resultText")
        }

    }
}