package com.enderthor.kpower.data


import io.hammerhead.karooext.models.StreamState

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class RealKarooValues(
    val speed: StreamState? = null,
    val slope: StreamState? = null,
    val elevation: StreamState? = null,
    val headwind: StreamState? = null,
    val pressure: StreamState? = null,
    val userWeight: StreamState? = null,
)

@Serializable
data class ConfigData(
    val id: Int,
    val name: String,
    val isActive: Boolean,
    val bikeMass: String ,
    val rollingResistanceCoefficient: String,
    val dragCoefficient: String,
    val frontalArea: String,
    val powerLoss: String ,
    val headwindconf: String,
    val isOpenWeather: Boolean,
    val apikey: String,
    val ftp: String ,
    )

val defaultConfigData = Json.encodeToString(listOf(ConfigData(0,"default", true, "14.0","0.0095","0.8","0.9","2.2","0.0", false, "","200")))