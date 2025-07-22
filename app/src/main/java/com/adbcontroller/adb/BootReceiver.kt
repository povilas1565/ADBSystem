package com.adbcontroller.adb

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.adbcontroller.gps.GpsSpoofer
import com.adbcontroller.permissions.PermissionManager

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.i("BootReceiver", "✅ Phone rebooted, restoring permissions and GPS...")

            PermissionManager(context).requestAllPermissions()
            GpsSpoofer(context).restoreLastLocation()
        }
    }
}