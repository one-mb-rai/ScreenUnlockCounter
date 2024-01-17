/**
 * ScreenUnlockService is an Android service that runs in the foreground,
 * monitors screen unlock events, and displays a notification.
 */
package com.onemb.screenunlockcounter

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * ScreenUnlockService is an Android service that runs in the foreground,
 * monitors screen unlock events, and displays a notification.
 */
class ScreenUnlockService : Service() {

    private val screenUnlockReceiver = ScreenUnlockCounterReceiver()

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "ScreenUnlockServiceChannel"
        private const val NOTIFICATION_ID = 1
    }

    /**
     * Returns null since the service does not provide binding.
     */
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    /**
     * Called when the service is created. Initiates foreground service and registers
     * the screen unlock receiver.
     */
    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        registerScreenUnlockReceiver()
    }

    /**
     * Called when the service is destroyed. Unregisters the screen unlock receiver.
     */
    override fun onDestroy() {
        super.onDestroy()
        unregisterScreenUnlockReceiver()
    }

    /**
     * Registers the screen unlock receiver to monitor screen unlock events.
     */
    private fun registerScreenUnlockReceiver() {
        val filter = IntentFilter(Intent.ACTION_USER_PRESENT)
        registerReceiver(screenUnlockReceiver, filter)
    }

    /**
     * Unregisters the screen unlock receiver.
     */
    private fun unregisterScreenUnlockReceiver() {
        unregisterReceiver(screenUnlockReceiver)
    }

    /**
     * Starts the service in the foreground, creates a notification channel, and
     * displays a notification with low priority.
     */
    private fun startForegroundService() {
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Screen Unlock Widget Service")
            .setContentText("Service is running with minimal power")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setSound(null).setVibrate(longArrayOf(0))
            .build()

        // Check for notification permission before notifying
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
        startForeground(NOTIFICATION_ID, notification)
    }

    /**
     * Creates a notification channel for the foreground service.
     */
    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Foreground Service Channel",
            NotificationManager.IMPORTANCE_LOW
        )

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(serviceChannel)
    }
}
