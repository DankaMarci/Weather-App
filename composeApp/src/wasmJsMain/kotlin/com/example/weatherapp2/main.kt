package com.example.weatherapp2

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.example.weatherapp2.network.ApiService
import com.example.weatherapp2.network.createPlatformHttpClient
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
//    val httpClient = createPlatformHttpClient()
//    val apiService = ApiService(httpClient)
    ComposeViewport(document.body!!) {
        App()
    }
}