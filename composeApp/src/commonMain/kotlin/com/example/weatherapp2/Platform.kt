package com.example.weatherapp2

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform