package net.the_okazakis.workoutstrech

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.*
import androidx.core.app.NotificationCompat

class WorkoutService : Service() {

    private val binder = LocalBinder()
    private var soundPool: SoundPool? = null

    inner class LocalBinder : Binder() {
        fun getService(): WorkoutService = this@WorkoutService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        val aa = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()
        soundPool = SoundPool.Builder()
            .setAudioAttributes(aa)
            .setMaxStreams(10)
            .build()
        
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Workout Service Channel",
                NotificationManager.IMPORTANCE_LOW,
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    fun startForegroundService(workoutName: String) {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("ワークアウト中")
            .setContentText(workoutName)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    fun stopForegroundService() {
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    fun getSoundPool(): SoundPool? = soundPool

    fun playSound(soundId: Int, volume: Float) {
        android.util.Log.d("WorkoutService", "playSound called: id=$soundId, vol=$volume")
        soundPool?.let {
            val result = it.play(soundId, volume, volume, 1, 0, 1.0f)
            if (result == 0) {
                android.util.Log.e("WorkoutService", "playSound failed: soundId $soundId might not be loaded")
            }
        } ?: run {
            android.util.Log.e("WorkoutService", "playSound failed: soundPool is null")
        }
    }

    override fun onDestroy() {
        soundPool?.release()
        soundPool = null
        super.onDestroy()
    }

    companion object {
        const val CHANNEL_ID = "WorkoutServiceChannel"
        const val NOTIFICATION_ID = 1
    }
}
