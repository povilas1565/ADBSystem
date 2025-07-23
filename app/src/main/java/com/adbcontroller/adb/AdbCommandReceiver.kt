package com.adbcontroller.adb

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.adbcontroller.gps.GpsSpoofer
import com.adbcontroller.permissions.PermissionManager
import com.adbcontroller.sms.SmsSender

class AdbCommandReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.w("AdbReceiver", "🚀 ВХОД В onReceive AdbCommandReceiver -> action=${intent.action}")

        val command = intent.getStringExtra("command")
        if (command == null) {
            Log.e("AdbReceiver", "❌ Нет команды в интенте!")
            return
        }

        Log.i("AdbReceiver", "✅ Команда получена: $command")

        // 🚀 ВСЕГДА СТАРТУЕМ ForegroundService, иначе Android 8+ может заблокировать
        try {
            val serviceIntent = Intent(context, AdbForegroundService::class.java).apply {
                action = "com.adbcontroller.ADB_COMMAND"
                putExtras(intent.extras ?: return)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.w("AdbReceiver", "🚀 Стартуем ForegroundService для выполнения команды")
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        } catch (e: Exception) {
            Log.e("AdbReceiver", "❌ Ошибка запуска ForegroundService: ${e.message}")
        }
    }
}
