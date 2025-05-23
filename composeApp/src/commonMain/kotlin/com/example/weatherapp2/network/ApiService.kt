// shared/src/commonMain/kotlin/com/example/myapp/network/ApiService.kt
package com.example.weatherapp2.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.*
import io.ktor.http.encodeURLParameter

class ApiService(private val httpClient: HttpClient) {

    suspend fun getCoordinates(city: String): Pair<Float, Float> {
        if (city.isEmpty()) {
            return Pair(0f, 0f)
        }
        val encodedCity = city.encodeURLParameter()
        val baseUrl = "https://api.openweathermap.org/geo/1.0/direct?q=${encodedCity}&limit=5&appid=5459fb445f2ceefa006ae17934b9fd0a"
        val cords = httpClient.get(baseUrl) // Use the injected httpClient instead of the global one
        val body = cords.bodyAsText()
        println("Response body: $body")

        try {
            val jsonArray = Json.parseToJsonElement(body).jsonArray
            if (jsonArray.isNotEmpty()) {
                val firstResult = jsonArray[0].jsonObject
                val lat = firstResult["lat"]?.jsonPrimitive?.float ?: 0f
                val lon = firstResult["lon"]?.jsonPrimitive?.float ?: 0f
                println("Coordinates: lat=$lat, lon=$lon")
                return Pair(lat, lon)
            }
        } catch (e: Exception) {
            println("Error parsing coordinates: ${e.message}")
        }
        return Pair(0f, 0f)
    }

    suspend fun getWeather(lat: Float, lon: Float) {
        val baseUrl = "https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&appid=5459fb445f2ceefa006ae17934b9fd0a"
        val weather = httpClient.get(baseUrl) // Use the injected httpClient instead of the global one
        val weatherData = weather.bodyAsText()
        println(weatherData)
    }
}

