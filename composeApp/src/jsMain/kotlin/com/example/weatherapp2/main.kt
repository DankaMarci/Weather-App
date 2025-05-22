package com.example.weatherapp2

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    //val client = HttpClient(Js)
    ComposeViewport(document.body!!) {
        App()
    }
}