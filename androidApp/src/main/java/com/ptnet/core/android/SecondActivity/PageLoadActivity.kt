package com.ptnet.core.android.SecondActivity

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.patrykandpatrick.vico.core.entry.entryOf
import com.ptnet.core.android.utils.doAsync
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import java.io.IOException
import kotlin.random.Random

class PageLoadActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PageLoadView()
        }
    }
}

@Preview(
    showSystemUi = true
)
@Composable
fun PageLoadView() {
    var urlText by remember { mutableStateOf("hi.fpt.vn") }
    var pageLoadTime by remember { mutableStateOf("") }
    var listTimer by remember { mutableStateOf(ArrayList<Long>()) }
    var mContext: Context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = urlText,
            onValueChange = { urlText = it },
            label = { Text("URL") },
            trailingIcon = {
                IconButton(onClick = { urlText = "" }) {
                    Icon(Icons.Rounded.Clear, contentDescription = "Clear")
                }
            }
        )


        Button(onClick = {
            doAsync {
                // Xóa dữ liệu trước đó
                listTimer.clear()
                pageLoadTime = ""

                // Lặp lại 10 lần để đo thời gian tải trang và cập nhật danh sách
                if (pageLoadTimer(urlText) == -1L) {
                    Toast.makeText(mContext, "Wrong URL", Toast.LENGTH_LONG).show()
                } else {

                }
                repeat(10) {
                    val time = pageLoadTimer(urlText)
                    listTimer.add(time)
                    pageLoadTime = Gson().toJson(listTimer)

                }
            }
        }, shape = RectangleShape) {
            Text(
                text = "START",
                Modifier
                    .padding(0.dp)
            )
        }
        Text(text = "Response 10 times page load: " + (pageLoadTime.toString() ?: "N/A"))
        fun getRandomEntries() = List(4) { entryOf(it, Random.nextFloat() * 16f) }
    }
}

//------------------------------------------------------------------------------

val client = OkHttpClient()

fun pageLoadTimer(host: String?): Long {
    if (host.isNullOrEmpty()) {
        return -1L
    }
    val url = "http://${host.trim()}"
    val request: Request = Request.Builder().url(url).build()

    val startTime = System.currentTimeMillis()
    return try {
        val response: Response = client.newCall(request).execute()
        // Xử lý response nếu cần thiết
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        // Close the response body to prevent connection leaks
        response.body()?.close()
        duration
    } catch (e: IOException) {
        -1L
    }
}