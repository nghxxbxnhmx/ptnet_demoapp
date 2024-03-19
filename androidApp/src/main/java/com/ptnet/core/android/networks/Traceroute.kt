package com.ptnet.core.android.networks

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.toLowerCase
import com.ptnet.core.android.models.HopInfo
import com.ptnet.core.android.models.PingContainer
import com.ptnet.core.android.models.TraceContainer

class Traceroute(val url: String, val context: Context, val ttl: Int = 30) {
    val mContext = context

    fun execute(maxhops: Int = 30): List<TraceContainer> {
        val ping = Ping(url)
        val traceList = mutableListOf<TraceContainer>()
        for (i in 1..maxhops) {
            val startTime: Long = System.currentTimeMillis()
            val pingResult = ping.executeWithTtl(i)
            val duration: Long = System.currentTimeMillis() - startTime
            val pingContainer = parsePingOutput(pingResult)
            pingContainer.time = duration.toFloat()
            pingContainer.hop = traceList.size + 1

            traceList.add(pingContainer)

            val endPoint: PingContainer = parseEndPoint(pingResult)
            if (pingContainer.ipAddress == endPoint.ip) break
        }

        return traceList
    }

    fun executeOne(ttl: Int): TraceContainer {
        val ping = Ping(url)
        val startTime: Long = System.currentTimeMillis()
        val pingResult = ping.executeWithTtl(ttl)
        val duration: Long = System.currentTimeMillis() - startTime
        val pingContainer = parsePingOutput(pingResult)
        pingContainer.time = duration.toFloat()
        return pingContainer
    }


    fun parsePingOutput(pingOutput: String): TraceContainer {
        val traceContainer = TraceContainer(0, "", "", 0f, false)

        val lines = pingOutput.split("\n")
        val hopInfo = lines.getOrNull(1) ?: return TraceContainer(0, "", "", -1f, false)

        traceContainer.status = true
        traceContainer.domain = parseDomain(hopInfo)
        traceContainer.ipAddress = parseIpAddress(hopInfo)
        traceContainer.time = parseTime(pingOutput)
        Log.d("PING RESPONSE", pingOutput)
        Log.d(
            "DATA PING",
            "${traceContainer.domain}- ${traceContainer.ipAddress} - ${traceContainer.time}"
        )
//        Log.d("TRACE CONTAINER",Gson().toJson(traceContainer))
        return traceContainer
    }

    fun parseDomain(hopInfo: String): String {
        val hopMatch = Regex("From (\\S+).* icmp_seq=\\d+ Time to live exceeded").find(hopInfo)
        return hopMatch?.groups?.get(1)?.value ?: ""
    }

    fun parseIpAddress(hopInfo: String): String {
        val ipMatch = Regex("(\\d+\\.\\d+\\.\\d+\\.\\d+)").find(hopInfo)
        return ipMatch?.groups?.get(1)?.value ?: ""
    }

    fun parseTime(pingOutput: String): Float {
        val hopPattern = "from (.*?):.*time=(\\d+\\.\\d+|\\d+) ms".toRegex()
        val timeLine = pingOutput.lines().firstOrNull { it.contains("time=") }

        return hopPattern.find(timeLine ?: "")?.groupValues?.get(2)?.toFloatOrNull() ?: -1f
    }


    fun parseEndPoint(pingOutput: String): PingContainer {
        val domainAndIpPattern = Regex("PING (.+?) \\((.*?)\\)")
        val matchResult = domainAndIpPattern.find(pingOutput)

        return matchResult?.let { match ->

            val address = match.groupValues[1] // domain
            val ip = match.groupValues[2] // địa chỉ IP
            val pingResult = PingContainer(address, ip, ArrayList<HopInfo>())
            pingResult
        } ?: PingContainer("", "", ArrayList<HopInfo>())
    }


    fun getEndPoint(): String {
        return "parseEndPoint"
    }

    fun hasConnectivity(): Boolean {
        val connectivityManager =
            mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}