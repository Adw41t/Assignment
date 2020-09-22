package com.projects.assignment

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate
import com.projects.assignment.data.NewsDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NewsApplication: Application() {
    var newsDatabase:NewsDatabase? = null

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
    }
}