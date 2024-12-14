package com.enderthor.kpower.extension

import android.content.Context

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey

import com.enderthor.kpower.activity.dataStore
import com.enderthor.kpower.data.GpsCoordinates
import com.enderthor.kpower.data.OpenMeteoCurrentWeatherResponse
import com.enderthor.kpower.data.OpenWeatherCurrentWeatherResponse
import com.enderthor.kpower.data.HeadwindStats
import com.enderthor.kpower.data.ConfigData
import com.enderthor.kpower.data.OpenMeteoData
import com.enderthor.kpower.data.defaultConfigData


import io.hammerhead.karooext.KarooSystemService
import io.hammerhead.karooext.models.DataType
import io.hammerhead.karooext.models.HttpResponseState
import io.hammerhead.karooext.models.OnHttpResponse
import io.hammerhead.karooext.models.OnStreamState
import io.hammerhead.karooext.models.StreamState

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch

import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.time.debounce

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.time.Duration

import kotlin.math.absoluteValue
import kotlin.time.Duration.Companion.seconds


val jsonWithUnknownKeys = Json { ignoreUnknownKeys = true }

val currentDataKey = stringPreferencesKey("current")
val statsKey = stringPreferencesKey("stats")
val preferencesKey = stringPreferencesKey("configdata")


suspend fun saveStats(context: Context, stats: HeadwindStats) {
    context.dataStore.edit { t ->
        t[statsKey] = Json.encodeToString(stats)
    }
}

suspend fun saveCurrentData(context: Context, forecast: OpenMeteoCurrentWeatherResponse) {
    context.dataStore.edit { t ->
        Timber.d("Saving current data forecast" + forecast)
        t[currentDataKey] = Json.encodeToString(forecast)
        Timber.d("Saved current data " + t[currentDataKey])
    }
}

fun KarooSystemService.streamDataFlow(dataTypeId: String): Flow<StreamState> {
    return callbackFlow {
        val listenerId = addConsumer(OnStreamState.StartStreaming(dataTypeId)) { event: OnStreamState ->
            trySendBlocking(event.state)
        }
        awaitClose {
            removeConsumer(listenerId)
        }
    }
}


fun Context.streamCurrentWeatherData(): Flow<OpenMeteoCurrentWeatherResponse> {
    return dataStore.data.map { settingsJson ->
        try {
            val data = settingsJson[currentDataKey]
            data?.let { d -> jsonWithUnknownKeys.decodeFromString<OpenMeteoCurrentWeatherResponse>(d) }
        } catch (e: Throwable) {
            Timber.e("Failed to stream current weather data" + e)
            null
        }
    }.filterNotNull().distinctUntilChanged().filter { it.current.time * 1000 >= System.currentTimeMillis() - (1000 * 60 * 60 ) }
}

fun Context.streamStats(): Flow<HeadwindStats> {
    return dataStore.data.map { statsJson ->
        try {
            jsonWithUnknownKeys.decodeFromString<HeadwindStats>(
                statsJson[statsKey] ?: HeadwindStats.defaultStats
            )
        } catch(e: Throwable){
            Timber.e("Failed to read stats" + e)
            jsonWithUnknownKeys.decodeFromString<HeadwindStats>(HeadwindStats.defaultStats)
        }
    }.distinctUntilChanged()
}

fun Context.loadPreferencesFlow(): Flow<List<ConfigData>> {
    return dataStore.data.map { settingsJson ->
        try {
            jsonWithUnknownKeys.decodeFromString<List<ConfigData>>(
                settingsJson[preferencesKey] ?: defaultConfigData
            )
        } catch(e: Throwable){
            Timber.tag("kpower").e(e, "Failed to read preferences Flow Extension")
            jsonWithUnknownKeys.decodeFromString<List<ConfigData>>(defaultConfigData)
        }
    }.distinctUntilChanged()
}

/*fun Context.loadPreferencesFlow(): Flow<List<ConfigData>> = flow {
    try {
        val preferences = dataStore.data.first()
        val jsonString = preferences[preferencesKey]
        val entries = if (jsonString != null) {
            jsonWithUnknownKeys.decodeFromString<List<ConfigData>>(jsonString)
        } else {
            jsonWithUnknownKeys.decodeFromString<List<ConfigData>>(defaultConfigData)
        }
        emit(entries)
        Timber.d("Preferences loaded in EstimatedPowerSource")
    } catch (e: Throwable) {
        Timber.tag("kpower").e(e, "Failed to read preferences")
        throw e
    }
}.distinctUntilChanged().catch { e ->
    Timber.tag("kpower").e(e, "Error in preferences flow")
}
*/

fun Context.parseWeatherResponse(responseString: String): OpenMeteoCurrentWeatherResponse {
    Timber.d("Decoded weather: $responseString")

    val decoded = try {
        if (responseString.contains("\"current\"")) {
            jsonWithUnknownKeys.decodeFromString<OpenMeteoCurrentWeatherResponse>(responseString)
        } else {
            val weather = jsonWithUnknownKeys.decodeFromString<OpenWeatherCurrentWeatherResponse>(responseString)
            Timber.d("Decoded weather: $weather")
            OpenMeteoCurrentWeatherResponse(
                current = OpenMeteoData(
                    windSpeed = weather.wind.speed,
                    windDirection = weather.wind.deg,
                    time = weather.time,
                    interval = 0
                ),
                latitude = weather.coord.lat,
                longitude = weather.coord.lon,
                timezone = "",
                elevation = 0.0,
                utfOffsetSeconds = 0
            )
        }
    } catch (e: Exception) {
        throw IllegalArgumentException("Invalid response format parse weather", e)

    }

    return decoded
}


@OptIn(FlowPreview::class)
suspend fun KarooSystemService.makeOpenMeteoHttpRequest(gpsCoordinates: GpsCoordinates, isOpenWeather: Boolean, api: String): HttpResponseState.Complete {
    return callbackFlow {
        // https://open-meteo.com/en/docs#current=temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,weather_code,cloud_cover,wind_speed_10m,wind_direction_10m,wind_gusts_10m&hourly=&daily=&location_mode=csv_coordinates&timeformat=unixtime&forecast_days=3

        val url = if(isOpenWeather && api.trim().isNotEmpty())  "https://api.openweathermap.org/data/2.5/weather?lat=${gpsCoordinates.lat}&lon=${gpsCoordinates.lon}&appid=$api"
        else "https://api.open-meteo.com/v1/forecast?latitude=${gpsCoordinates.lat}&longitude=${gpsCoordinates.lon}&current=wind_speed_10m,wind_direction_10m&timeformat=unixtime&wind_speed_unit=ms"

        Timber.d("Http request to ${url}...")

        val listenerId = addConsumer(
            OnHttpResponse.MakeHttpRequest(
                "GET",
                url= url,
                waitForConnection = false,
            ),
        ) { event: OnHttpResponse ->
            Timber.d("Http response event $event")
            if (event.state is HttpResponseState.Complete){
                trySend(event.state as HttpResponseState.Complete)
                close()
            }
        }
        awaitClose {
            removeConsumer(listenerId)
        }
    }.timeout(20.seconds).catch { e: Throwable ->
        if (e is TimeoutCancellationException){
            emit(HttpResponseState.Complete(500, mapOf(), null, "Timeout"))
        } else {
            throw e
        }
    }.single()
}
/*
fun signedAngleDifference(angle1: Double, angle2: Double): Double {
    val a1 = angle1 % 360
    val a2 = angle2 % 360
    var diff = abs(a1 - a2)

    val sign = if (a1 < a2) {
        if (diff > 180.0) -1 else 1
    } else {
        if (diff > 180.0) 1 else -1
    }

    if (diff > 180.0) {
        diff = 360.0 - diff
    }

    return sign * diff
}

@OptIn(FlowPreview::class)
fun KarooSystemService.getRelativeHeadingFlow(context: Context): Flow<Double> {
    val currentWeatherData = context.streamCurrentWeatherData()
    return getHeadingFlow()
        .filter { it >= 0 }
        .combine(currentWeatherData) { bearing, data -> bearing to data }
        .map { (bearing, data) ->
            val windBearing = data.current.windDirection + 180

            val diff = signedAngleDifference(bearing, windBearing)
            Timber.d("Wind bearing: $bearing vs $windBearing => $diff")

            diff
        }
}
*/
@OptIn(FlowPreview::class)
fun KarooSystemService.getHeadingFlow(): Flow<Double> {
    //return flowOf(20.0)
    return streamDataFlow("TYPE_LOCATION_ID")
        .mapNotNull { (it as? StreamState.Streaming)?.dataPoint?.values }
        .map { values ->
            val heading = values[DataType.Field.LOC_BEARING]
            /* Timber.d( "Updated heading: $heading") */
            heading ?: 0.0
        }
        .distinctUntilChanged()
        .scan(emptyList<Double>()) { acc, value ->
            val newAcc = acc + value
            if (newAcc.size > 3) newAcc.drop(1) else newAcc
        }
        .map { it.average() }
    /* .timeout(2.seconds)
     .catch { emit(0.0) }*/
}

@OptIn(FlowPreview::class)
fun KarooSystemService.getGpsCoordinateFlow(): Flow<GpsCoordinates> {
    // return flowOf(GpsCoordinates(52.5164069,13.3784))

    return streamDataFlow("TYPE_LOCATION_ID")
        .mapNotNull { (it as? StreamState.Streaming)?.dataPoint?.values }
        .mapNotNull { values ->
            val lat = values[DataType.Field.LOC_LATITUDE]
            val lon = values[DataType.Field.LOC_LONGITUDE]

            if (lat != null && lon != null) {
                GpsCoordinates(lat, lon)
            } else {
                Timber.e("Missing gps values: $values")
                null
            }
        }
        .map { it }
        .distinctUntilChanged { old, new -> old.distanceTo(new).absoluteValue < 0.001 }
        .debounce(Duration.ofSeconds(10))
}