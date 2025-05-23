package com.example.weatherapp2.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Weather(
    val coord: Coordinates,
    val weather: List<WeatherCondition>,
    val base: String,
    val main: MainWeatherData,
    val visibility: Int,
    val wind: Wind,
    val rain: Rain? = null,
    val clouds: Clouds,
    val dt: Long,
    val sys: SystemInfo,
    val timezone: Int,
    val id: Long,
    val name: String,
    val cod: Int
)

@Serializable
data class Coordinates(
    val lon: Double,
    val lat: Double
)

@Serializable
data class WeatherCondition(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

@Serializable
data class MainWeatherData(
    val temp: Double,
    @SerialName("feels_like")
    val feelsLike: Double,
    @SerialName("temp_min")
    val tempMin: Double,
    @SerialName("temp_max")
    val tempMax: Double,
    val pressure: Int,
    val humidity: Int,
    @SerialName("sea_level")
    val seaLevel: Int? = null,
    @SerialName("grnd_level")
    val groundLevel: Int? = null
)

@Serializable
data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double? = null
)

@Serializable
data class Rain(
    @SerialName("1h")
    val oneHour: Double? = null
)

@Serializable
data class Clouds(
    val all: Int
)

@Serializable
data class SystemInfo(
    val type: Int? = null,
    val id: Int? = null,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)

