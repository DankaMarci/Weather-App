package com.example.weatherapp2

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

//expect fun httpClient(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform