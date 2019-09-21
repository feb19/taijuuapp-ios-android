package jp.feb19.taijuu

import android.app.Notification
import android.app.NotificationChannel
import android.content.Context.NOTIFICATION_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class AlarmReceiver : BroadcastReceiver() {

    companion object {
        var NOTIFICATION_ID = "taijuu.notification"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.i("TaijuuApp","onReceive")

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_ONE_SHOT
        )

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(NOTIFICATION_ID,
                "Taijuu", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
            NotificationCompat.Builder(context, NOTIFICATION_ID)
        } else {
            NotificationCompat.Builder(context)
        }
        builder.setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(pendingIntent)
            .setContentText("12 時間後のリマインドです！")
            .setContentTitle("体重を記録しましょう")

        notificationManager.notify(1, builder.build())
    }
}