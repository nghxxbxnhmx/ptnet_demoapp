package com.ptnet.core

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform