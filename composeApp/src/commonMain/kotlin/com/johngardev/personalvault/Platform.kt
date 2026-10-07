package com.johngardev.personalvault

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform