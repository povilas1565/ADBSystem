package com.adbcontroller.adb

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.w("BootReceiver", "🚀 ВХОД В onReceive BootReceiver -> action=${intent.action}")
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            Log.i("BootReceiver", "✅ Телефон перезагружен – запускаем сервис")

            try {
                val serviceIntent = Intent(context, AdbForegroundService::class.java)
                Log.w("BootReceiver", "🚀 Пытаемся запустить ForegroundService")
                context.startForegroundService(serviceIntent)
            } catch (e: Exception) {
                Log.e("BootReceiver", "❌ Ошибка запуска сервиса: ${e.message}")
            }
        }
    }
}