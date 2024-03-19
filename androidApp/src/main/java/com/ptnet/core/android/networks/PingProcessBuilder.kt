package com.ptnet.core.android.networks

import com.ptnet.core.android.models.HopInfo
import com.ptnet.core.android.models.PingContainer
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class PingProcessBuilder(private val url: String) {

    fun execute(ttl: Int = 4): String {
        return executePing("-c $ttl")
    }

    fun executeWithTtl(ttl: Int): String {
        return executePing("-c 1 -t $ttl")
    }

    private fun executePing(options: String): String {
        val processBuilder = ProcessBuilder("ping", options, url)
        try {
            val process = processBuilder.start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val stringBuilder = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line).append("\n")
            }

            process.waitFor() // Chờ quá trình kết thúc

            return stringBuilder.toString()
        } catch (e: IOException) {
            e.printStackTrace()
            return ""
        } catch (e: InterruptedException) {
            e.printStackTrace()
            return ""
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
