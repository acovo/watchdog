package net.acovo.watchdog

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class GuardService : Service() {

    private val CHANNEL_ID = "guard_service_channel"

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = "Guard Service Channel"
        val descriptionText = "This is the notification channel for Guard Service"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Guard Service")
            .setContentText("Running in the foreground")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Ensure you have a valid drawable ID.
            .build()

        // Use startForeground to call a notification
        startForeground(1, notification)

        // Service logic can be placed here, such as starting a thread or executing tasks.
        return START_STICKY
    }
}