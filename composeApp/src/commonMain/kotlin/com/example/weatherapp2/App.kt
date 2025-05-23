package com.example.weatherapp2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.weatherapp2.network.ApiService
import com.example.weatherapp2.network.createPlatformHttpClient
import com.example.weatherapp2.weather.ForecastEntry
import com.example.weatherapp2.weather.Weather
import com.example.weatherapp2.weather.WeatherForecast
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

data class DailyForecast(
    val date: String,
    val minTemp: Double,
    val maxTemp: Double
)

fun List<ForecastEntry>.groupByDay(): List<DailyForecast> {
    return groupBy { it.dtTxt.split(" ")[0] }
        .map { (date, entries) ->
            DailyForecast(
                date = date,
                minTemp = entries.minOf { it.main.temp },
                maxTemp = entries.maxOf { it.main.temp }
            )
        }
}

val httpClient = createPlatformHttpClient()
val apiService = ApiService(httpClient)

@Composable
@Preview
fun App() {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1B262C),
            Color(0xFF0F4C75)
        )
    )

    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        var searchText by remember { mutableStateOf("") }
        var weather by remember { mutableStateOf<Weather?>(null) }
        var forecast by remember { mutableStateOf<WeatherForecast?>(null) }
        val coroutineScope = rememberCoroutineScope()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("Enter city name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF3282B8),
                        unfocusedBorderColor = Color(0xFF3282B8).copy(alpha = 0.6f),
                        focusedLabelColor = Color(0xFF3282B8),
                        unfocusedLabelColor = Color(0xFF3282B8).copy(alpha = 0.6f),
                        cursorColor = Color(0xFF3282B8)
                    )
                )

                Button(
                    onClick = {
                        coroutineScope.launch {
                            val (lat, lon) = apiService.getCoordinates(searchText)
                            weather = apiService.getWeather(lat, lon)
                            forecast = apiService.getForecast(lat, lon)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3282B8)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Get Weather")
                }

                Spacer(modifier = Modifier.height(24.dp))

                weather?.let { currentWeather ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1B262C).copy(alpha = 0.7f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = currentWeather.name,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                text = "${currentWeather.main.temp.toInt()}°C",
                                style = MaterialTheme.typography.displayLarge
                            )
                            Text(
                                text = currentWeather.weather.firstOrNull()?.description?.capitalize() ?: "",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color(0xFF3282B8)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                forecast?.let { weatherForecast ->
                    Text(
                        text = "5-Day Forecast",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    weatherForecast.list.groupByDay().forEach { dailyForecast ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1B262C).copy(alpha = 0.5f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = dailyForecast.date.split("-").takeLast(2).joinToString("/"),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${dailyForecast.minTemp.toInt()}°",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color(0xFF3282B8)
                                    )
                                    Text(
                                        text = "/",
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        text = "${dailyForecast.maxTemp.toInt()}°",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color(0xFFBBE1FA)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
