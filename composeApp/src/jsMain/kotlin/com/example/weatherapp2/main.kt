import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import com.example.weatherapp2.apiService
import com.example.weatherapp2.network.ApiService
import com.example.weatherapp2.weather.ForecastEntry
import com.example.weatherapp2.weather.Weather
import com.example.weatherapp2.weather.WeatherForecast
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable
import kotlin.collections.component1
import kotlin.collections.component2

val apiService = ApiService(HttpClient(Js))

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

fun main() {
    renderComposable(rootElementId = "root") {
        WeatherApp()
    }
}

@Composable
fun WeatherApp() {
    println("WeatherApp started")
    var searchText by remember { mutableStateOf("") }
    var weather by remember { mutableStateOf<Weather?>(null) }
    var forecast by remember { mutableStateOf<WeatherForecast?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var actualCity by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope { Dispatchers.Main }

    Div {
        H1 { Text("Időjárás Kereső") }

        Div(attrs = { classes("input-group") }) {
            Input(type = InputType.Text) {
                value(searchText)
                onInput { event -> searchText = event.value }
                placeholder("Város neve...")
            }
            Button(attrs = {
                onClick {
                        scope.launch {
                            isLoading = true
                            val (lat, lon) = apiService.getCoordinates(searchText)
                            weather = apiService.getWeather(lat, lon)
                            forecast = apiService.getForecast(lat, lon)
                            isLoading = false
                            actualCity = searchText
                        }
                    println("Button clicked")
                    console.log("Button clicked")
                }
            }) {
                Text(if (isLoading) "Keresés..." else "Keresés")
            }
        }

        if (isLoading) {
            P { Text("Adatok betöltése...") }
        }

        errorMessage?.let {
            P(attrs = { style { color(Color.red) } }) {
                Text(it)
            }
        }

        weather?.let { weather ->
            Div(attrs = { classes("weather-card") }) {
                H2 { Text(actualCity) }
                P {
                    WeatherIcon(weather.weather.firstOrNull()?.icon ?: "")
                    //Span { style { fontSize(2.em); fontWeight(FontWeight.Bold) } }
                    Text("${weather.main.temp.toInt()}°C - ${weather.weather.firstOrNull()?.description ?: ""}")
                }
                P { Text("Páratartalom: ${weather.main.humidity}%") }
                P { Text("Szél: ${weather.wind.speed} m/s") }
            }
        }

        forecast?.let { forecastList ->
            Div(attrs = { classes("weather-card") }) {
                H3 { Text("Előrejelzés") }
                forecastList.list.groupByDay().forEach { item ->
                    Div(attrs = { classes("forecast-item") }) {
                        Span { Text(formatDate(item.date)) }
                        WeatherIcon(item.icon, size = 30.px)
                        Span { Text("${item.minTemp.toInt()}°C / ${item.maxTemp.toInt()}°C") }
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherIcon(iconCode: String, size: CSSpxValue = 50.px) {
    if (iconCode.isNotEmpty()) {
        Img(
            src = "https://openweathermap.org/img/wn/${iconCode}@2x.png",
            alt = "Időjárás ikon",
            attrs = {
                style {
                    width(size)
                    height(size)
                    property("vertical-align", "middle")
                    marginRight(8.px)
                }
            }
        )
    }
}