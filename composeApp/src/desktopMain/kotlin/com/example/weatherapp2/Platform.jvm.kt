package com.example.weatherapp2

//import io.ktor.client.*
//import io.ktor.client.engine.java.*
//import io.ktor.client.engine.okhttp.*
//import java.util.concurrent.TimeUnit

//actual fun httpClient(config: HttpClientConfig<*>.() -> Unit) = HttpClient(Java) {
//    config(this)
//
//}

class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()