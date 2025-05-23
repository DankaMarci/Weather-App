package com.example.weatherapp2

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp2.components.AsyncImage
import com.example.weatherapp2.network.ApiService
import com.example.weatherapp2.network.createPlatformHttpClient
import com.example.weatherapp2.weather.ForecastEntry
import com.example.weatherapp2.weather.Weather
import com.example.weatherapp2.weather.WeatherForecast
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import org.jetbrains.compose.ui.tooling.preview.Preview

val apiService = ApiService(createPlatformHttpClient())

data class DailyForecast(
    val date: String,
    val minTemp: Double,
    val maxTemp: Double,
    val description: String,
    val icon: String
)

fun List<ForecastEntry>.groupByDay(): List<DailyForecast> {
    return groupBy { it.dtTxt.split(" ")[0] }
        .map { (date, entries) ->
            DailyForecast(
                date = date,
                minTemp = entries.minOf { it.main.temp },
                maxTemp = entries.maxOf { it.main.temp },
                description = entries[entries.size / 2].weather.firstOrNull()?.description ?: "",
                icon = entries[entries.size / 2].weather.firstOrNull()?.icon ?: ""
            )
        }
}

fun formatDate(dateString: String): String {
    val date = LocalDate.parse(dateString)
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val tomorrow = today.plus(DatePeriod(days = 1))

    return when(date) {
        today -> "Today"
        tomorrow -> "Tomorrow"
        else -> buildString {
            append(date.month.name.lowercase().replaceFirstChar { it.uppercase() })
            append(" ")
            append(date.dayOfMonth)
        }
    }
}

@Composable
fun WeatherIcon(iconCode: String, modifier: Modifier = Modifier) {
    if (iconCode.isNotEmpty()) {
        AsyncImage(
            resource = "https://openweathermap.org/img/wn/${iconCode}@2x.png",
            contentDescription = "Weather icon",
            modifier = modifier,
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun WeatherInfoColumn(title: String, value: String, icon: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 24.sp
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}

@Composable
@Preview
fun App() {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1B262C),
            Color(0xFF0F4C75),
            Color(0xFF3282B8)
        )
    )

    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        var searchText by remember { mutableStateOf("") }
        var weather by remember { mutableStateOf<Weather?>(null) }
        var forecast by remember { mutableStateOf<WeatherForecast?>(null) }
        var isLoading by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1B262C).copy(alpha = 0.7f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            label = { Text("Enter city name") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF3282B8),
                                unfocusedBorderColor = Color(0xFF3282B8).copy(alpha = 0.6f),
                                focusedLabelColor = Color(0xFF3282B8),
                                unfocusedLabelColor = Color(0xFF3282B8).copy(alpha = 0.6f),
                                cursorColor = Color(0xFF3282B8)
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isLoading = true
                                    val (lat, lon) = apiService.getCoordinates(searchText)
                                    weather = apiService.getWeather(lat, lon)
                                    forecast = apiService.getForecast(lat, lon)
                                    isLoading = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF3282B8)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            enabled = !isLoading,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White
                                )
                            } else {
                                Text("Get Weather")
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = weather != null,
                    enter = fadeIn() + expandVertically(
                        expandFrom = Alignment.Top,
                        animationSpec = tween(500)
                    ),
                    exit = fadeOut() + shrinkVertically(
                        shrinkTowards = Alignment.Top,
                        animationSpec = tween(500)
                    )
                ) {
                    weather?.let { currentWeather ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1B262C).copy(alpha = 0.7f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = currentWeather.name,
                                    style = MaterialTheme.typography.headlineMedium
                                )
                                WeatherIcon(
                                    currentWeather.weather.firstOrNull()?.icon ?: "",
                                    modifier = Modifier.size(100.dp)
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

                                Spacer(modifier = Modifier.height(16.dp))
                                Divider(color = Color.White.copy(alpha = 0.1f))
                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    WeatherInfoColumn(
                                        title = "Humidity",
                                        value = "${currentWeather.main.humidity}%",
                                        icon = "💧"
                                    )
                                    WeatherInfoColumn(
                                        title = "Wind",
                                        value = "${currentWeather.wind.speed} m/s",
                                        icon = "💨"
                                    )
                                    WeatherInfoColumn(
                                        title = "Feels Like",
                                        value = "${currentWeather.main.feelsLike.toInt()}°C",
                                        icon = "🌡️"
                                    )
                                }
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = forecast != null,
                    enter = fadeIn() + expandVertically(
                        expandFrom = Alignment.Top,
                        animationSpec = tween(700)
                    ),
                    exit = fadeOut() + shrinkVertically(
                        shrinkTowards = Alignment.Top,
                        animationSpec = tween(300)
                    )
                ) {
                    forecast?.let { weatherForecast ->
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "5-Day Forecast",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )

                            weatherForecast.list.groupByDay().forEach { dailyForecast ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF1B262C).copy(alpha = 0.5f)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = formatDate(dailyForecast.date),
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = Color.White.copy(alpha = 0.9f)
                                            )
                                            Text(
                                                text = dailyForecast.description.capitalize(),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF3282B8)
                                            )
                                        }
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            WeatherIcon(
                                                dailyForecast.icon,
                                                modifier = Modifier.size(40.dp)
                                            )
                                            Column(
                                                horizontalAlignment = Alignment.End
                                            ) {
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
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
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF3282B8)
                    )
                }
            }
        }
    }
}
