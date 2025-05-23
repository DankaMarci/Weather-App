package com.example.weatherapp2.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherForecast(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<ForecastEntry>,
    val city: City
)

@Serializable
data class ForecastEntry(
    val dt: Long,
    val main: MainWeatherData,
    val weather: List<WeatherCondition>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Double,
    val rain: Rain? = null,
    val sys: ForecastSys,
    @SerialName("dt_txt")
    val dtTxt: String
)

@Serializable
data class ForecastSys(
    val pod: String
)

@Serializable
data class City(
    val id: Long,
    val name: String,
    val coord: Coordinates,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)
