package com.ptnet.core.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ptnet.core.android.networks.Ping
import java.io.InputStream
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainView()
        }
    }
}

@Composable
fun MainView() {
    Column (
        Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var hostTextField: String by remember { mutableStateOf("hi.fpt.vn") }
        var resultText: String by remember { mutableStateOf("") }
        OutlinedTextField(
            value = hostTextField,
            onValueChange = { hostTextField = it },
            label = { Text("URL") },
            trailingIcon = {
                IconButton(onClick = {
                    hostTextField = ""
                }) {
                    Text(text = "X")
                }
            }
        )

        Button(onClick = {
            var pingAsync = PingAsync(hostTextField.toString())
            pingAsync.execute { result ->
                resultText = result
            }

        },
            Modifier.padding(all = 8.dp)
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

class PingAsync(private val host: String) {

    private val executor = Executors.newSingleThreadExecutor()

    fun execute(callback: (String) -> Unit) {
        executor.submit {
            val result = Ping(host).execute()
            callback(result)
        }
    }
}

@Preview (
    showSystemUi = true,
    showBackground = true,
    name = "PT Net",
    backgroundColor = 0x000000
)
@Composable
fun DefaultPreview() {
    MainView()
}
