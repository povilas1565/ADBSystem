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
        val command = intent.getStringExtra("command") ?: return

        Log.i("AdbReceiver", "✅ Received ADB command: $command")

        when (command) {
            "send_sms" -> {
                val phone = intent.getStringExtra("phone") ?: return
                val text = intent.getStringExtra("text") ?: return
                SmsSender(context).sendSms(phone, text)
            }

            "start_gps" -> {
                val lat = intent.getDoubleExtra("lat", 0.0)
                val lon = intent.getDoubleExtra("lon", 0.0)
                GpsSpoofer(context).startMockLocation(lat, lon)
            }

            "stop_gps" -> {
                GpsSpoofer(context).stopMockLocation()
            }

            "request_permissions" -> {
                PermissionManager(context).requestAllPermissions()
            }

            "set_default_sms" -> {
                PermissionManager(context).setDefaultSmsApp()
            }

            "restore_default_sms" -> {
                PermissionManager(context).restoreDefaultSmsApp()
            }

            "restore_gps" -> {
                GpsSpoofer(context).restoreLastLocation()
            }

            else -> {
                Log.e("AdbReceiver", "❌ Unknown command: $command")
            }
        }
    }
}
