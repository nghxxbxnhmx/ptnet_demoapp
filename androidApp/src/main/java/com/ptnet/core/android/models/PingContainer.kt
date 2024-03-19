package com.ptnet.core.android.models

import com.ptnet.core.android.networks.Ping

data class PingContainer(
    var address: String,
    var ip: String,
    var pingList: ArrayList<HopInfo>
)