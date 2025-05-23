package com.example.weatherapp2

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.weatherapp2.network.ApiService
import com.example.weatherapp2.network.createPlatformHttpClient
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import weatherapp2.composeapp.generated.resources.Res
import weatherapp2.composeapp.generated.resources.compose_multiplatform

val httpClient = createPlatformHttpClient()
val apiService = ApiService(httpClient)

@Composable
@Preview
fun App() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        var searchText by remember { mutableStateOf("") }
        val coroutineScope = rememberCoroutineScope()

        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Enter city name") },
                modifier = Modifier.fillMaxWidth().safeContentPadding()
            )
            Button(onClick = {
                coroutineScope.launch {
                    val (lat, lon) = apiService.getCoordinates(searchText)
                    apiService.getWeather(lat, lon)
                }
            }) {
                Text("Search Weather")
            }
        }
    }
}

