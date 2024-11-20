package net.acovo.watchdog

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import java.io.FileDescriptor
import java.io.PrintWriter
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicInteger

class GuardService : Service() {

    private val TAG = "GuardService"
    private val CHANNEL_ID = "net.acovo.watchdog"
    private var mCount = AtomicInteger(0)
    private val mCounterBinder = CounterBinder()

    private val executorService: ExecutorService = Executors.newFixedThreadPool(5)

    inner class CounterBinder: Binder() {
        private val TAG = "CounterBinder"

        fun getCount():Int?{
            Log.d(TAG,"getCount")
            mCount.incrementAndGet()
            return mCount.get()
        }

        fun getService(): GuardService = this@GuardService
    }

    override fun attachBaseContext(newBase: Context?) {
        Log.d(TAG,"attachBaseContext")
        super.attachBaseContext(newBase)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.d(TAG,"onConfigurationChanged")
        super.onConfigurationChanged(newConfig)
    }

    override fun onLowMemory() {
        Log.e(TAG,"onLowMemory")
        super.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        Log.w(TAG,"onTrimMemory")
        super.onTrimMemory(level)
    }

    override fun onCreate() {
        Log.d(TAG,"onCreate")
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }

    override fun onStart(intent: Intent?, startId: Int) {
        Log.d(TAG,"onStart")
        super.onStart(intent, startId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        Log.d(TAG,"createNotificationChannel")
        val name = "Guard Service Channel"
        val descriptionText = "This is the notification channel for Guard Service"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.d(TAG,"onBind")
        //TODO("Return the communication channel to the service.")
        return mCounterBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG,"onUnbind")
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {
        Log.d(TAG,"onRebind")
        super.onRebind(intent)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.d(TAG,"onTaskRemoved")
        super.onTaskRemoved(rootIntent)
    }

    override fun dump(fd: FileDescriptor?, writer: PrintWriter?, args: Array<out String>?) {
        Log.d(TAG,"dump")
        super.dump(fd, writer, args)
    }

    override fun onTimeout(startId: Int) {
        Log.d(TAG,"onTimeout")
        super.onTimeout(startId)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG,"onStartCommand")

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

    override fun onDestroy() {
        Log.d(TAG,"onDestroy")
        super.onDestroy()
        executorService.shutdownNow()
    }

    companion object {
        const val ACTION_START_TASK = "com.example.ACTION_START_TASK"
        const val EXTRA_MESSAGE = "com.example.EXTRA_MESSAGE"
    }

    private fun executeTask(task: Runnable):Future<*> {
        return executorService.submit(task)
        //executorService.execute(task)
    }

    fun watch(package_name: String,interval:Int):String {
        val runnable = Runnable {
            println("Foreground Service received message: $package_name")
            try {
                Thread.sleep(5000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        executeTask(runnable)

        return ""
    }

    fun unwatch(package_name: String) {

    }
}