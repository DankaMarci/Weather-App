// shared/src/commonMain/kotlin/com/example/myapp/network/ApiService.kt
package com.example.weatherapp2.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText

class ApiService(private val httpClient: HttpClient) {

    private val baseUrl = "http://api.openweathermap.org/geo/1.0/direct?q=Budapest&limit=5&appid=5459fb445f2ceefa006ae17934b9fd0a"

    suspend fun getCoordinates(city: String): Pair<Double, Double> {
        val cords = com.example.weatherapp2.httpClient.get(baseUrl)
        val body = cords.bodyAsText()
        val lat = body.substringAfter("\"lat\":").substringBefore(",").toDouble()
        val lon = body.substringAfter("\"lon\":").substringBefore(",").toDouble()
        println("Coordinates: lat=$lat, lon=$lon")
        return Pair(lat, lon)
    }

    suspend fun getWeather(lat: Double, lon: Double): Unit {
        val weather = com.example.weatherapp2.httpClient.get(baseUrl)
        val weatherData = weather.bodyAsText()
        println(weatherData)
    }
}