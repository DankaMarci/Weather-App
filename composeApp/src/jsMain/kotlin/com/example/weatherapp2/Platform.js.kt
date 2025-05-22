package com.example.weatherapp2

import io.ktor.client.*
import io.ktor.client.engine.js.*

actual fun httpClient(config: HttpClientConfig<*>.() -> Unit) = HttpClient(Js) {
    config(this)

}

class JsPlatform : Platform {
    override val name: String = "Web with Kotlin/Js"
}

actual fun getPlatform(): Platform = JsPlatform()
