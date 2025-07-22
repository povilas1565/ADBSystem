package com.adbcontroller.sms

import android.content.Context
import android.telephony.SmsManager
import android.util.Log

class SmsSender(private val context: Context) {

    fun sendSms(phone: String, text: String) {
        Log.w("SmsSender", "🚀 ВХОД В sendSms -> phone=$phone , text=$text")
        try {
            val smsManager = context.getSystemService(SmsManager::class.java)

            val parts = smsManager.divideMessage(text)
            Log.w("SmsSender", "🚀 Разбили сообщение на ${parts.size} частей")
            smsManager.sendMultipartTextMessage(phone, null, parts, null, null)

            Log.i("SmsSender", "✅ SMS sent to $phone: $text")
        } catch (e: Exception) {
            Log.e("SmsSender", "❌ Failed to send SMS", e)
        }
    }
}