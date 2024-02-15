package com.ptnet.core.android.networks

import java.io.IOException
import java.io.InputStream
import java.io.PrintWriter

class Ping(private val host: String, private val ttl: Int = 4) {
    fun execute(): String {
        val process = Runtime.getRuntime().exec("ping -c $ttl $host")
        val inputStream: InputStream = process.inputStream
        val reader = inputStream.bufferedReader()
        val output = StringBuilder()

        var line: String?
        while (reader.readLine().also { line = it } != null) {
            output.append(line).append("\n")
        }
        return output.toString()
    }
}
