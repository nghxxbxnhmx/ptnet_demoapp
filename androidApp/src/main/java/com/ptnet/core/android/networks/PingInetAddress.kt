package com.ptnet.core.android.networks

import com.ptnet.core.android.models.HopInfo
import com.ptnet.core.android.models.PingContainer
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetAddress

class PingInetAddress(private val url: String) {

    fun execute(ttl: Int = 4, count: Int = 1): String {
        val address = InetAddress.getByName(url)
        val command = if (System.getProperty("os.name").startsWith("Windows")) {
            "ping -n $count -i $ttl ${address.hostAddress}"
        } else {
            "ping -c $count -t $ttl ${address.hostAddress}"
        }

        return executeCommand(command)
    }

    private fun executeCommand(command: String): String {
        val process = Runtime.getRuntime().exec(command)
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val stringBuilder = StringBuilder()
        var line: String?

        while (reader.readLine().also { line = it } != null) {
            stringBuilder.append(line).append("\n")
        }

        process.waitFor() // Chờ quá trình kết thúc

        return stringBuilder.toString()
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
