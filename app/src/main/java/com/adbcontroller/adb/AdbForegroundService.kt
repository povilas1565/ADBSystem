package com.adbcontroller.adb

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log

class AdbForegroundService: Service() {

    override fun onCreate() {
        super.onCreate()
        startForeground(1, createNotification())
        Log.i("AdbService", "✅ ForegroundService запущен")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("AdbService", "📩 Service получил intent: $intent")

        // Если пришла команда сразу из BootReceiver – отправляем в AdbCommandReceiver
        if (intent?.action == "com.adbcontroller.ADB_COMMAND") {
            AdbCommandReceiver().onReceive(applicationContext, intent)
        }

        return START_STICKY
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
            .setContentText("Сервис запущен и ждёт ADB команд")
            .setSmallIcon(android.R.drawable.ic_menu_manage)
            .setOngoing(true)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}