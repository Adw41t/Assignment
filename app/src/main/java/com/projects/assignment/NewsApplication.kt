package com.projects.assignment

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.*
import com.addy.newzshots.utils.NewsFetchWorker
import com.projects.assignment.data.NewsDatabase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class NewsApplication: Application() {
    var newsDatabase:NewsDatabase? = null

    private val applicationScope = CoroutineScope(Dispatchers.Default)
    var sharep: SharedPreferences?=null
    override fun onCreate() {
        super.onCreate()
        newsDatabase= NewsDatabase.getInstance(this)
        sharep= PreferenceManager.getDefaultSharedPreferences(this)
        val dn = sharep?.getBoolean(getString(R.string.dayNightTheme), true)?:true
        if (dn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        delayedInit()
    }

    private fun setupNewsFetchWorker(){
        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED) // you can add as many constraints as you want
                .build()
        val workRequest = PeriodicWorkRequest.Builder(NewsFetchWorker::class.java, 2, TimeUnit.HOURS, 15, TimeUnit.MINUTES)
                .setConstraints(constraints).build()
        WorkManager.getInstance()
                .enqueueUniquePeriodicWork(
                        NewsFetchWorker.WORK_NAME,
                        ExistingPeriodicWorkPolicy.KEEP,
                        workRequest)
    }

    private fun delayedInit() {
        applicationScope.launch {
            setupNewsFetchWorker()
        }
    }
}