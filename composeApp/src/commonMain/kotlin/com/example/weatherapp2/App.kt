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
import com.example.weatherapp2.weather.Weather
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
        var searchText by remember { mutableStateOf("") }
        var weather by remember { mutableStateOf<Weather?>(null) }
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
                    weather = apiService.getWeather(lat, lon)
                }
            }) {
                Text("Search Weather")
            }

            weather?.let { currentWeather ->
                Column(
                    modifier = Modifier.fillMaxWidth().safeContentPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentWeather.name,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "${currentWeather.main.temp.toInt()}°C",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Text(
                        text = currentWeather.weather.firstOrNull()?.description?.capitalize() ?: "",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Feels like: ${currentWeather.main.feelsLike.toInt()}°C",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Humidity: ${currentWeather.main.humidity}%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Wind: ${currentWeather.wind.speed} m/s",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
