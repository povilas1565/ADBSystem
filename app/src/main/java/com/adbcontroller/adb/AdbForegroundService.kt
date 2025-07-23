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

class AdbForegroundService: Service() {

    override fun onCreate() {
        super.onCreate()
        startForeground(1, createNotification())
        Log.i("AdbService", "✅ ForegroundService запущен")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.w("AdbService", "🚀 ForegroundService уже жив и получил команду!")
        Log.i("AdbService", "📩 Service получил intent: $intent")

        if (intent?.action == "com.adbcontroller.ADB_COMMAND") {
            handleCommand(intent)
        } else {
            Log.w("AdbService", "⚠️ Неизвестное или пустое действие: ${intent?.action}")
        }

        return START_STICKY
    }

    private fun handleCommand(intent: Intent) {
        val command = intent.getStringExtra("command")
        Log.i("AdbService", "✅ Выполняем команду: $command")

        when (command) {
            "send_sms" -> {
                val phone = intent.getStringExtra("phone")
                val text = intent.getStringExtra("text")
                if (phone != null && text != null) {
                    Log.w("AdbService", "📨 Отправляем SMS: $phone -> $text")
                    SmsSender(this).sendSms(phone, text)
                } else {
                    Log.e("AdbService", "❌ Ошибка: не передан номер или текст")
                }
            }

            "start_gps" -> {
                val lat = intent.getDoubleExtra("lat", 0.0)
                val lon = intent.getDoubleExtra("lon", 0.0)
                Log.w("AdbService", "📡 Старт GPS спуфинга: $lat, $lon")
                GpsSpoofer(this).startMockLocation(lat, lon)
            }

            "stop_gps" -> {
                Log.w("AdbService", "🛑 Остановка GPS спуфинга")
                GpsSpoofer(this).stopMockLocation()
            }

            "request_permissions" -> {
                Log.w("AdbService", "🔑 Запрашиваем все разрешения")
                PermissionManager(this).requestAllPermissions()
            }

            "set_default_sms" -> {
                Log.w("AdbService", "📱 Делаем приложение SMS по умолчанию")
                PermissionManager(this).setDefaultSmsApp()
            }

            "restore_default_sms" -> {
                Log.w("AdbService", "📱 Восстанавливаем SMS приложение по умолчанию")
                PermissionManager(this).restoreDefaultSmsApp()
            }

            "restore_gps" -> {
                Log.w("AdbService", "♻️ Восстанавливаем последнюю GPS локацию")
                GpsSpoofer(this).restoreLastLocation()
            }

            else -> {
                Log.e("AdbService", "❌ Неизвестная команда: $command")
            }
        }
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