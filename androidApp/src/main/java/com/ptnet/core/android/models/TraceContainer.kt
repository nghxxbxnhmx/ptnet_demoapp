package com.ptnet.core.android.models

data class TraceContainer(
    var hop: Int = 0,
    var domain: String = "",
    var ipAddress: String = "",
    var time: Float = 0.0f,
    var status: Boolean = false
)