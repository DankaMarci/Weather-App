// shared/src/commonMain/kotlin/com/example/myapp/network/ApiService.kt
package com.example.weatherapp2.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.*
import io.ktor.http.encodeURLParameter
import com.example.weatherapp2.weather.*

class ApiService(private val httpClient: HttpClient) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getCoordinates(city: String): Pair<Float, Float> {
        if (city.isEmpty()) {
            return Pair(0f, 0f)
        }
        val encodedCity = city.encodeURLParameter()
        val baseUrl = "https://api.openweathermap.org/geo/1.0/direct?q=${encodedCity}&limit=5&appid=5459fb445f2ceefa006ae17934b9fd0a"
        val cords = httpClient.get(baseUrl)
        val body = cords.bodyAsText()

        try {
            val jsonArray = json.parseToJsonElement(body).jsonArray
            if (jsonArray.isNotEmpty()) {
                val firstResult = jsonArray[0].jsonObject
                val lat = firstResult["lat"]?.jsonPrimitive?.float ?: 0f
                val lon = firstResult["lon"]?.jsonPrimitive?.float ?: 0f
                println("Coordinates found: lat=$lat, lon=$lon")
                return Pair(lat, lon)
            }
        } catch (e: Exception) {
            println("Error parsing coordinates: ${e.message}")
        }
        return Pair(0f, 0f)
    }

    suspend fun getWeather(lat: Float, lon: Float): Weather? {
        try {
            val baseUrl = "https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&units=metric&appid=5459fb445f2ceefa006ae17934b9fd0a"
            val response = httpClient.get(baseUrl)
            val body = response.bodyAsText()

            return json.decodeFromString<Weather>(body).also {
                println("Weather data received for: ${it.name}")
                println("Temperature: ${it.main.temp}°C")
                println("Condition: ${it.weather.firstOrNull()?.description ?: "Unknown"}")
            }
        } catch (e: Exception) {
            println("Error fetching weather: ${e.message}")
            return null
        }
    }

    suspend fun getForecast(lat: Float, lon: Float): WeatherForecast? {
        try {
            val baseUrl = "https://api.openweathermap.org/data/2.5/forecast?lat=${lat}&lon=${lon}&units=metric&cnt=96&appid=5459fb445f2ceefa006ae17934b9fd0a"
            val response = httpClient.get(baseUrl)
            val body = response.bodyAsText()

            return json.decodeFromString<WeatherForecast>(body).also {
                println("Forecast data received for: ${it.city.name}")
                println("Number of forecasts: ${it.list.size}")
            }
        } catch (e: Exception) {
            println("Error fetching forecast: ${e.message}")
            return null
        }
    }
}
