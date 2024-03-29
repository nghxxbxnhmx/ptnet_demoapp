package com.ptnet.core.android.networks

import android.util.Log
import com.ptnet.core.android.models.HopInfo
import com.ptnet.core.android.models.PingContainer
import java.io.IOException
import java.io.InputStream

class Ping(private val url: String) {
    companion object {
        private const val TIMEOUT = 3000
    }

    fun execute(ttl: Int = 4): String {
        return executePing("-c $ttl -W $TIMEOUT")
    }

    fun executeWithTtl(ttl: Int): String {
        return executePing("-c 1 -t $ttl -W $TIMEOUT")
    }

    private fun executePing(options: String): String {
        return try {
            Runtime.getRuntime().exec("ping $options $url").inputStream.bufferedReader()
                .use { it.readText() }
        } catch (e: IOException) {
            Log.e("Ping", "Error executing ping command", e)
            ""
        }
    }

    fun parsePingOutput(pingOutput: String): PingContainer? {
        val pingContainer = PingContainer("", "", ArrayList())

        // Extracting domain and IP address
        val domainAndIpPattern = "PING (.+?) \\((.*?)\\)".toRegex()
        domainAndIpPattern.find(pingOutput)?.apply {
            pingContainer.address = groupValues[1] // domain
            pingContainer.ip = groupValues[2] // IP address
        }

        // Extracting hop information
        val hopPattern = "from (.*?):.*time=(\\d+\\.\\d+|\\d+) ms".toRegex()
        pingOutput.lines().filter { it.contains("bytes from") }.forEach { line ->
            hopPattern.find(line)?.apply {
                val ip = groupValues[1]
                val time = groupValues[2].toFloatOrNull() ?: -1f
                val status = true // Trạng thái luôn là true vì lệnh ping đã nhận được phản hồi
                pingContainer.pingList.add(HopInfo(ip, time, status))
            }
        }

        return pingContainer
    }
}