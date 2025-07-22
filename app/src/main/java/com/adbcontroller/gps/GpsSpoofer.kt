package com.adbcontroller.gps

import android.util.Log
import android.content.Context
import android.content.SharedPreferences
import android.location.Location

import android.location.LocationManager
import android.location.provider.ProviderProperties
import android.os.Build


class GpsSpoofer(private val context: Context) {

    private val provider = LocationManager.GPS_PROVIDER
    private var locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val prefs: SharedPreferences =
        context.getSharedPreferences("gps_spoofer", Context.MODE_PRIVATE)

    fun startMockLocation(lat: Double, lon: Double) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                Log.e("GpsSpoofer", "❌ Works only on Android 12+")
                return
            }

            // ✅ Автоматически пытаемся назначить Mock Location App
            try {
                val process = Runtime.getRuntime().exec(
                    arrayOf("sh", "-c", "settings put secure mock_location_app ${context.packageName}")
                )
                process.waitFor()
                if (process.exitValue() == 0) {
                    Log.i("GpsSpoofer", "✅ Mock location app set automatically")
                } else {
                    Log.w("GpsSpoofer", "⚠️ Failed to set mock location app, set manually")
                }
            } catch (_: Exception) {
            }

            // Удаляем старый тестовый провайдер
            try {
                locationManager.removeTestProvider(provider)
            } catch (_: Exception) {
            }

            val props = ProviderProperties.Builder()
                .setAccuracy(ProviderProperties.ACCURACY_FINE)
                .setPowerUsage(ProviderProperties.POWER_USAGE_LOW)
                .setHasMonetaryCost(false)
                .build()

            locationManager.addTestProvider(provider, props)
            locationManager.setTestProviderEnabled(provider, true)

            val location = Location(provider).apply {
                latitude = lat
                longitude = lon
                altitude = 0.0
                accuracy = 3.0f
                time = System.currentTimeMillis()
                elapsedRealtimeNanos = System.nanoTime()
            }

            locationManager.setTestProviderLocation(provider, location)
            Log.i("GpsSpoofer", "✅ Mock location set: $lat, $lon")

            // ✅ Сохраняем координаты для восстановления после ребута
            prefs.edit().putFloat("last_lat", lat.toFloat())
                .putFloat("last_lon", lon.toFloat())
                .apply()

        } catch (e: Exception) {
            Log.e("GpsSpoofer", "❌ Failed to set mock location", e)
        }
    }

    fun restoreLastLocation() {
        val lat = prefs.getFloat("last_lat", 0f).toDouble()
        val lon = prefs.getFloat("last_lon", 0f).toDouble()
        if (lat != 0.0 && lon != 0.0) {
            startMockLocation(lat, lon)
            Log.i("GpsSpoofer", "✅ Restored last location after reboot")
        }
    }

    fun stopMockLocation() {
        try {
            locationManager.setTestProviderEnabled(provider, false)
            locationManager.removeTestProvider(provider)
            Log.i("GpsSpoofer", "✅ Mock location stopped")
        } catch (e: Exception) {
            Log.e("GpsSpoofer", "❌ Failed to stop mock location", e)
        }
    }
}