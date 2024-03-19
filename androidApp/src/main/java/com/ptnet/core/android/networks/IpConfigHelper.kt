package com.ptnet.core.android.networks

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.SupplicantState
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import androidx.compose.ui.text.toUpperCase
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.net.UnknownHostException
import java.security.Permission
import java.util.Collections
import java.util.Locale


object IpConfigHelper {
    fun getIPAddress(useIPv4: Boolean): String {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val intf = interfaces.nextElement()
                val addrs: List<InetAddress> = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress) {
                        val sAddr = addr.hostAddress
                        if (useIPv4 && addr is Inet4Address || !useIPv4 && addr is Inet6Address) {
                            return sAddr.uppercase(Locale.getDefault())
                        }
                    }
                }
            }
        } catch (e: SocketException) {
            Log.e("IpConfigHelper", "Lỗi khi lấy địa chỉ IP", e)
        }
        return "N/A"
    }

    val subnetMask: String
        // Start Subnet Mask - Start Subnet Mask - Start Subnet Mask - Start Subnet Mask - Start Subnet Mask - Start Subnet Mask
        get() {
            try {
                val interfaces = NetworkInterface.getNetworkInterfaces()
                while (interfaces.hasMoreElements()) {
                    val intf = interfaces.nextElement()
                    if (intf.isUp && !intf.isLoopback) {
                        for (addr in Collections.list(intf.inetAddresses)) {
                            if (addr is Inet4Address) {
                                val prefixLength = getIpv4PrefixLength(intf)
                                if (prefixLength >= 0 && prefixLength <= 32) {
                                    return convertPrefixLengthToSubnetMask(prefixLength.toShort())
                                }
                            }
                        }
                    }
                }
            } catch (e: SocketException) {
                e.printStackTrace()
            }
            return "Unknown"
        }

    private fun convertPrefixLengthToSubnetMask(prefixLength: Short): String {
        val netmask = -0x1 shl 32 - prefixLength
        return String.format(
            Locale.getDefault(),
            "%d.%d.%d.%d",
            netmask shr 24 and 0xff,
            netmask shr 16 and 0xff,
            netmask shr 8 and 0xff,
            netmask and 0xff
        )
    }

    private fun getIpv4PrefixLength(networkInterface: NetworkInterface): Int {
        val addresses = networkInterface.interfaceAddresses
        for (address in addresses) {
            val inetAddress = address.address
            if (inetAddress is Inet4Address) {
                return address.networkPrefixLength.toInt()
            }
        }
        return -1
    }

    val defaultGateway: String
        // End Subnet Mask - End Subnet Mask - End Subnet Mask - End Subnet Mask - End Subnet Mask - End Subnet Mask
        get() {
            try {
                val interfaces = NetworkInterface.getNetworkInterfaces()
                while (interfaces.hasMoreElements()) {
                    val intf = interfaces.nextElement()
                    if (intf.isUp && !intf.isLoopback) {
                        val gateway = findDefaultGateway(intf)
                        if (gateway != null) {
                            return gateway
                        }
                    }
                }
            } catch (e: SocketException) {
                e.printStackTrace()
            }
            return "Unknown"
        }

    private fun findDefaultGateway(networkInterface: NetworkInterface): String? {
        for (interfaceAddress in networkInterface.interfaceAddresses) {
            val address = interfaceAddress.address
            if (address is Inet4Address) {
                val networkAddress =
                    calculateNetworkAddress(address, interfaceAddress.networkPrefixLength)
                return networkAddress!!.substring(0, networkAddress.lastIndexOf(".")) + ".1"
            }
        }
        return null
    }

    private fun calculateNetworkAddress(address: InetAddress, prefixLength: Short): String? {
        val ipBytes = address.address
        val ipInt = ipBytes[0].toInt() and 0xFF shl 24 or
                (ipBytes[1].toInt() and 0xFF shl 16) or
                (ipBytes[2].toInt() and 0xFF shl 8) or
                (ipBytes[3].toInt() and 0xFF)
        val networkInt = ipInt and (-0x1 shl 32 - prefixLength)
        val networkBytes = ByteArray(4)
        networkBytes[0] = (networkInt and -0x1000000 ushr 24).toByte()
        networkBytes[1] = (networkInt and 0x00FF0000 ushr 16).toByte()
        networkBytes[2] = (networkInt and 0x0000FF00 ushr 8).toByte()
        networkBytes[3] = (networkInt and 0x000000FF).toByte()
        return try {
            InetAddress.getByAddress(networkBytes).hostAddress
        } catch (e: UnknownHostException) {
            e.printStackTrace()
            null
        }
    }

    // Start Mac Address - Start Mac Address - Start Mac Address - Start Mac Address - Start Mac Address - Start Mac Address
    fun getMacAddress(context: Context): String {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo: WifiInfo? = wifiManager.connectionInfo
        val macAddress = wifiInfo?.macAddress
        return if (macAddress.isNullOrEmpty() || macAddress == "02:00:00:00:00:00" || macAddress == "00:00:00:00:00:00") {
            "N/A"
        } else {
            macAddress
        }
    }

    fun getSSID(context: Context): String {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo: WifiInfo? = wifiManager.connectionInfo
        val ssid = wifiInfo?.ssid?.let {
            val cleanedSSID = it.trim().removePrefix("\"").removeSuffix("\"")
            if (cleanedSSID == "<unknown ssid>") {
                "N/A"
            } else {
                cleanedSSID
            }
        }
        Log.d("SSID",ssid ?: "N/A")
        return ssid ?: "N/A"
    }

    fun getBSSID(context: Context): String {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo: WifiInfo? = wifiManager.connectionInfo
        val bssid = wifiInfo?.bssid ?: "N/A"
        return bssid.toUpperCase()
    }

    fun getSecurityType(context: Context): String {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (wifiManager != null && wifiManager.isWifiEnabled) {
            val wifiInfo = wifiManager.connectionInfo
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                return wifiInfo.currentSecurityType.toString() + ""
            }
        } else {
            return "Unknown"
        }
        return "N/A"
    }

    fun getSecurity(network: WifiConfiguration?): String {
        if (network == null) {
            return "N/A"
        }
        if (network.allowedGroupCiphers == null) {
            return "N/A"
        }
        return if (network.allowedGroupCiphers[WifiConfiguration.GroupCipher.CCMP]) {
            "WPA2"
        } else if (network.allowedGroupCiphers[WifiConfiguration.GroupCipher.TKIP]) {
            "WPA"
        } else if (network.allowedGroupCiphers[WifiConfiguration.GroupCipher.WEP40]
            || network.allowedGroupCiphers[WifiConfiguration.GroupCipher.WEP104]
        ) {
            "WEP"
        } else {
            "N/A Callback"
        }
    }

    fun getWifiInfo(context: Context): WifiInfo? {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return if (wifiManager != null && wifiManager.isWifiEnabled) wifiManager.connectionInfo else null
    }

    fun getScanResults(context: Context): List<ScanResult>? {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (wifiManager != null && wifiManager.isWifiEnabled) wifiManager.scanResults else null
        } else {
            null
        }
    }
}