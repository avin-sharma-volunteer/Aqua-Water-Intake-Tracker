package com.whomentors.aqua.Recievers

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.whomentors.aqua.R
import com.whomentors.aqua.AppUtils.Thisapp

class Notifier : BroadcastReceiver() {
    var manager: NotificationManager? = null
    var myNotication2: android.app.Notification? = null

    override fun onReceive(context: Context, intent: Intent) {

        val mychannelid = "Channel_id"
        val mychannelname = "Notification"

        val prefs = context.getSharedPreferences(Thisapp.USERS_SHARED_PREF, Thisapp.PRIVATE_MODE)
        val notificationsTone = prefs.getString(
            Thisapp.NOTIFICATION_TONE_URI_KEY, RingtoneManager.getDefaultUri(
                RingtoneManager.TYPE_NOTIFICATION
            ).toString()
        )

        val title = context.resources.getString(R.string.app_name)
        val messageToShow = prefs.getString(
            Thisapp.NOTIFICATION_MSG_KEY,
            context.resources.getString(R.string.pref_notification_message_value)
        )

        val nHelper =
            com.whomentors.aqua.Helpers.Notification(context)
        val decodeResource =
            BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
        @SuppressLint("ResourceType") val nBuilder = messageToShow?.let {
            nHelper
                .getNotification(title, it, notificationsTone)
                ?.setSmallIcon(R.mipmap.ic_launcher)
                ?.setStyle(NotificationCompat.BigPictureStyle().bigPicture(decodeResource))
        }

        manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(mychannelid, mychannelname, NotificationManager.IMPORTANCE_HIGH)
            channel.enableVibration(true)
            channel.lightColor = Color.BLUE
            channel.enableLights(true)
            channel.setShowBadge(true)
            manager!!.createNotificationChannel(channel)
        }
        myNotication2 = if (Build.VERSION.SDK_INT >= 21) {
            nBuilder!!.build()
        } else {
            nBuilder!!.getNotification()
        }
        manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nBuilder!!.setChannelId(mychannelid)
        }

        manager!!.notify(mychannelid, 1, nBuilder.build())





    }
}