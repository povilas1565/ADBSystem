package com.adbcontroller.adb

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.adbcontroller.gps.GpsSpoofer
import com.adbcontroller.permissions.PermissionManager
import com.adbcontroller.sms.SmsSender

class AdbCommandReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.i("AdbReceiver", "📩 onReceive вызван! intent=$intent")

        val command = intent.getStringExtra("command")
        Log.i("AdbReceiver", "✅ Команда получена: $command")

        if (command == null) {
            Log.e("AdbReceiver", "❌ Нет команды в интенте!")
            return
        }

        when (command) {
            "send_sms" -> {
                val phone = intent.getStringExtra("phone")
                val text = intent.getStringExtra("text")

                // 🔥 ЛОГ ДО ВЫПОЛНЕНИЯ
                Log.w("AdbReceiver", "🚀 ВХОД В send_sms -> $phone : $text")

                if (phone != null && text != null) {
                    Log.i("AdbReceiver", "📨 Отправляем SMS: $phone -> $text")
                    SmsSender(context).sendSms(phone, text)
                } else {
                    Log.e("AdbReceiver", "❌ Ошибка: не передан номер или текст")
                }
            }

            "start_gps" -> {
                val lat = intent.getDoubleExtra("lat", 0.0)
                val lon = intent.getDoubleExtra("lon", 0.0)

                // 🔥 ЛОГ ДО ВЫПОЛНЕНИЯ
                Log.w("AdbReceiver", "🚀 ВХОД В start_gps -> $lat , $lon")

                Log.i("AdbReceiver", "📡 Старт GPS спуфинга: $lat, $lon")
                GpsSpoofer(context).startMockLocation(lat, lon)
            }

            "stop_gps" -> {
                Log.w("AdbReceiver", "🚀 ВХОД В stop_gps")
                Log.i("AdbReceiver", "🛑 Остановка GPS спуфинга")
                GpsSpoofer(context).stopMockLocation()
            }

            "request_permissions" -> {
                Log.w("AdbReceiver", "🚀 ВХОД В request_permissions")
                Log.i("AdbReceiver", "🔑 Запрашиваем все разрешения")
                PermissionManager(context).requestAllPermissions()
            }

            "set_default_sms" -> {
                Log.w("AdbReceiver", "🚀 ВХОД В set_default_sms")
                Log.i("AdbReceiver", "📱 Делаем приложение SMS по умолчанию")
                PermissionManager(context).setDefaultSmsApp()
            }

            "restore_default_sms" -> {
                Log.w("AdbReceiver", "🚀 ВХОД В restore_default_sms")
                Log.i("AdbReceiver", "📱 Восстанавливаем SMS приложение по умолчанию")
                PermissionManager(context).restoreDefaultSmsApp()
            }

            "restore_gps" -> {
                Log.w("AdbReceiver", "🚀 ВХОД В restore_gps")
                Log.i("AdbReceiver", "♻️ Восстанавливаем последнюю GPS локацию")
                GpsSpoofer(context).restoreLastLocation()
            }

            else -> {
                Log.e("AdbReceiver", "❌ Неизвестная команда: $command")
            }
        }
    }
}
