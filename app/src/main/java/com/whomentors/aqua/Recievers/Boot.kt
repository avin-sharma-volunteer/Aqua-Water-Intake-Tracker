package com.whomentors.aqua.Recievers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.whomentors.aqua.Helpers.Alarm
import com.whomentors.aqua.AppUtils.Thisapp

class Boot : BroadcastReceiver() {
    private val alarm = Alarm()

    override fun onReceive(context: Context?, intent: Intent?) {

            if (intent != null && intent.action != null && intent.action == "android.intent.action.BOOT_COMPLETED") {
                val prefs = context!!.getSharedPreferences(Thisapp.USERS_SHARED_PREF, Thisapp.PRIVATE_MODE)
                val notificationFrequency = prefs.getInt(Thisapp.NOTIFICATION_FREQUENCY_KEY, 60)
                val notificationsNewMessage = prefs.getBoolean("notifications_new_message", true)
                alarm.cancelAlarm(context)
                if (notificationsNewMessage) {
                    alarm.setAlarm(context, notificationFrequency.toLong())
                }
            }
        }

}
