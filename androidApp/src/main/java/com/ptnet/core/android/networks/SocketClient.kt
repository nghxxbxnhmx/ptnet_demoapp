package com.ptnet.core.android.networks

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket


class SocketClient(private val serverAddress: String, private val serverPort: Int) {

    private var socket: Socket? = null

    fun create() {
        try {
            socket = Socket(serverAddress, serverPort)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun close() {
        socket?.close()
    }

    fun sendMessage(message: String) {
        socket?.let {
            try {
                val outputStream = socket!!.getOutputStream()
                val out = PrintWriter(outputStream, true)
                out.println(message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun readMessage(): String {
        var response = ""
        socket?.let {
            try {
                val inStream = BufferedReader(InputStreamReader(it.getInputStream()))
                response = inStream.readLine() ?: ""
            } catch (e: Exception) {
                e.printStackTrace()
                response = "N/A"
            }
        }
        return "Received from server: $response"
    }

    fun isConnected(): Boolean {
        return socket?.isConnected ?: false
    }
}
