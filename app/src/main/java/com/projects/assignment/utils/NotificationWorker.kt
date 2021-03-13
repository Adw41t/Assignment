package com.addy.newzshots.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.projects.assignment.NewsApplication
import com.projects.assignment.R
import com.projects.assignment.module.newsRepositoryFieldInjectEntryPoint
import com.projects.assignment.ui.MainActivity
import dagger.hilt.android.EntryPointAccessors


class NotificationWorker (appContext: Context, workerParams: WorkerParameters):
        Worker(appContext, workerParams) {

    override fun doWork(): Result {
        try {
            val hiltEntryPoint =
                    EntryPointAccessors.fromApplication(applicationContext as NewsApplication, newsRepositoryFieldInjectEntryPoint::class.java)
            val repository = hiltEntryPoint.newsRepo()
            val article = repository.getTopArticleFromDb1()
            val context = applicationContext
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            var details: String = "Read today's News"
            if(article!=null && article.title!=null){
                details = article.title
            }
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            val pending = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT)

            val builder = NotificationCompat.Builder(context.getApplicationContext(), context.getString(R.string.app_name)) //CHANNEL_ID)
                    .setContentTitle(article.source?.name)
                    .setContentText(details)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(details))
                    .setAutoCancel(true)
                    .setContentIntent(pending)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                builder.setSmallIcon(R.mipmap.ic_launcher)
            } else {
                builder.setSmallIcon(R.drawable.ic_notification)
                builder.color = ContextCompat.getColor(context, R.color.colorPrimary)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId: String = context.getString(R.string.app_name).toString() + "_id"
                val channelName: CharSequence = context.getString(R.string.app_name)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                builder.setChannelId(channelId)
                val notificationChannel = NotificationChannel(channelId, channelName, importance)
                nm.createNotificationChannel(notificationChannel)
            }

            val notify = builder.build()
            notify.flags = notify.flags or Notification.FLAG_AUTO_CANCEL
            nm.notify(1, notify)
        }
        catch (e: Exception) {
            return Result.retry()
        }
        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }
}