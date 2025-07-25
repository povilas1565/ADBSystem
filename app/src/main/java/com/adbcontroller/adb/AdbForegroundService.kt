package com.adbcontroller.adb

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.adbcontroller.gps.GpsSpoofer
import com.adbcontroller.permissions.PermissionManager
import com.adbcontroller.sms.SmsSender

class AdbForegroundService : Service() {

    override fun onCreate() {
        super.onCreate()
        startForeground(1, createNotification())
        Log.i("AdbService", "✅ ForegroundService запущен и готов к командам")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("AdbService", "🚀 Получена команда через ADB")

        val command = intent?.getStringExtra("command")
        Log.w("AdbService", "📩 command=$command")

        when (command) {
            "send_sms" -> {
                val phone = intent.getStringExtra("phone")
                val text = intent.getStringExtra("text")
                if (!phone.isNullOrEmpty() && !text.isNullOrEmpty()) {
                    SmsSender(this).sendSms(phone, text)
                } else {
                    Log.e("AdbService", "❌ Нет номера или текста!")
                }
            }

            "start_gps" -> {
                val lat = intent.getDoubleExtra("lat", 0.0)
                val lon = intent.getDoubleExtra("lon", 0.0)
                GpsSpoofer(this).startMockLocation(lat, lon)
            }

            "stop_gps" -> {
                GpsSpoofer(this).stopMockLocation()
            }

            "request_permissions" -> {
                PermissionManager(this).requestAllPermissions()
            }

            "set_default_sms" -> {
                PermissionManager(this).setDefaultSmsApp()
            }

            "restore_default_sms" -> {
                PermissionManager(this).restoreDefaultSmsApp()
            }

            "restore_gps" -> {
                GpsSpoofer(this).restoreLastLocation()
            }

            else -> {
                Log.e("AdbService", "❌ Неизвестная команда!")
            }
        }

        return START_NOT_STICKY
    }

    private fun createNotification(): Notification {
        val channelId = "adb_service_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "ADB Foreground Service",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }

        return Notification.Builder(this, channelId)
            .setContentTitle("ADB Controller")
            .setContentText("Сервис запущен и ждёт команды")
            .setSmallIcon(android.R.drawable.ic_menu_manage)
            .setOngoing(true)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}