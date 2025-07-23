package com.adbcontroller.adb

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.w("BootReceiver", "🚀 ВХОД В onReceive BootReceiver -> action=${intent.action}")

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.i("BootReceiver", "✅ Телефон перезагружен – запускаем ForegroundService")

            try {
                val serviceIntent = Intent(context, AdbForegroundService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
                Log.w("BootReceiver", "🚀 ForegroundService успешно стартовал")
            } catch (e: Exception) {
                Log.e("BootReceiver", "❌ Ошибка запуска сервиса: ${e.message}")
            }
        }
    }
}