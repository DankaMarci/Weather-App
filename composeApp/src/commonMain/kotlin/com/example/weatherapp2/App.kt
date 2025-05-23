package com.example.weatherapp2

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapp2.network.ApiService
import com.example.weatherapp2.network.createPlatformHttpClient
import com.example.weatherapp2.weather.Weather
import com.example.weatherapp2.weather.WeatherForecast
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
        var forecast by remember { mutableStateOf<WeatherForecast?>(null) }
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
                    forecast = apiService.getForecast(lat, lon)
                }
            }) {
                Text("Get Weather")
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

            forecast?.let { weatherForecast ->
                Column(
                    modifier = Modifier.fillMaxWidth().safeContentPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "5-day Forecast",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    weatherForecast.list.take(5).forEach { entry ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = entry.dtTxt,
                                modifier = Modifier.width(100.dp)
                            )
                            Text(
                                text = " ${entry.main.temp.toInt()}°C",
                                modifier = Modifier.width(60.dp)
                            )
                            Text(
                                text = entry.weather.firstOrNull()?.description?.capitalize() ?: "",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}
