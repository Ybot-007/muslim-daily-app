package com.muslimdaily.domain.device

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class UserLocation(val latitude: Double, val longitude: Double, val cityLabel: String)

class LocationProvider(private val context: Context) {
    suspend fun lastKnownLocation(): UserLocation? = withContext(Dispatchers.IO) {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!hasFine && !hasCoarse) return@withContext null
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
        val best: Location? = providers.mapNotNull { provider ->
            runCatching { manager.getLastKnownLocation(provider) }.getOrNull()
        }.maxByOrNull { it.time }
        best?.let { UserLocation(it.latitude, it.longitude, "Current location") }
    }
}

object ManualCities {
    val cities = listOf(
        UserLocation(34.5553, 69.2075, "Kabul, Afghanistan"),
        UserLocation(21.3891, 39.8579, "Makkah, Saudi Arabia"),
        UserLocation(24.4539, 54.3773, "Abu Dhabi, UAE"),
        UserLocation(25.2048, 55.2708, "Dubai, UAE"),
        UserLocation(31.5204, 74.3587, "Lahore, Pakistan"),
        UserLocation(51.5072, -0.1276, "London, UK"),
        UserLocation(40.7128, -74.0060, "New York, USA"),
        UserLocation(41.8781, -87.6298, "Chicago, USA"),
        UserLocation(33.6844, 73.0479, "Islamabad, Pakistan")
    )
}
