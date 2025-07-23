package com.adbcontroller.adb

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class InstallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.w("InstallReceiver", "🚀 Приложение установлено или обновлено! Запускаем ForegroundService")

        try {
            val serviceIntent = Intent(context, AdbForegroundService::class.java)
            context.startForegroundService(serviceIntent)
        } catch (e: Exception) {
            Log.e("InstallReceiver", "❌ Ошибка запуска сервиса: ${e.message}")
        }
    }
}