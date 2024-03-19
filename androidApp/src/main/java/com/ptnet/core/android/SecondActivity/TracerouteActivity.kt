package com.ptnet.core.android.SecondActivity

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.google.gson.Gson
import com.ptnet.core.android.models.PingContainer
import com.ptnet.core.android.models.TraceContainer
import com.ptnet.core.android.networks.Ping
import com.ptnet.core.android.networks.Traceroute
import com.ptnet.core.android.utils.doAsync
import java.lang.StringBuilder
import kotlin.concurrent.thread

class TracerouteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TracerouteView()
        }
    }
}

@Preview(
    showSystemUi = true
)
@Composable
fun TracerouteView() {
    var mContext: Context = LocalContext.current
    var addressText by remember { mutableStateOf("facebook.com") }
    var resultText by remember { mutableStateOf("Result here") }
    var isExecutingCommand by remember { mutableStateOf(false) }
    var traceroute = Traceroute(addressText, mContext)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = addressText,
            onValueChange = { addressText = it },
            label = { Text("Address") },
            trailingIcon = {
                IconButton(onClick = {
                    addressText = ""
                }) {
                    Icon(
                        Icons.Rounded.Clear,
                        contentDescription = "Clear"
                    )
                }
            }
        )
        Button(
            onClick = {
                thread {
                    isExecutingCommand = true
                    repeat(30) { i ->
                        val trace = traceroute.executeOne(i)
                        val line = StringBuilder()
                        if(trace.domain.length > 1) line.append("${trace.domain}")
                        if(trace.ipAddress.length > 1) line.append(" | ${trace.ipAddress}")
                        if(trace.time != 0f) line.append(" | ${trace.time}")

                        resultText += "\n$line"
                    }
                }

//                    resultText = Gson().toJson(traceroute.execute())
//                    isExecutingCommand = false

            },
            shape = RectangleShape,
            enabled = !isExecutingCommand
        ) {
            Text(
                text = "LAUNCH",
                Modifier.padding(0.dp)
            )
        }
        Text(text = resultText)
    }
}