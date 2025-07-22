package com.adbcontroller.sms

import android.content.Context
import android.telephony.SmsManager
import android.util.Log

class SmsSender(private val context: Context) {

    fun sendSms(phone: String, text: String) {
        try {
            val smsManager = context.getSystemService(SmsManager::class.java)

            val parts = smsManager.divideMessage(text)
            smsManager.sendMultipartTextMessage(phone, null, parts, null, null)

            Log.i("SmsSender", "✅ SMS sent to $phone: $text")
        } catch (e: Exception) {
            Log.e("SmsSender", "❌ Failed to send SMS", e)
        }
    }
}