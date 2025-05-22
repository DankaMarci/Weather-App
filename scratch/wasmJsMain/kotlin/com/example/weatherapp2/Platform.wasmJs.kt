package com.example.weatherapp2

//import io.ktor.client.*
//import io.ktor.client.engine.darwin.*


class WasmPlatform : Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()

//actual fun httpClient(config: io.ktor.client.HttpClientConfig<*>.() -> Unit): io.ktor.client.HttpClient {
//    config(this)
//    engine {
//        configureRequest {
//            setAllowsCellularAccess(true)
//        }
//    }
//}