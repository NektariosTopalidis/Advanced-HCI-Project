package gr.nektariostop.ergasiaadvancedhci

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

class ShelfItApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)

        val channel = NotificationChannel(
            "alteration",
            "Alteration Notification",
            NotificationManager.IMPORTANCE_HIGH
        )

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}