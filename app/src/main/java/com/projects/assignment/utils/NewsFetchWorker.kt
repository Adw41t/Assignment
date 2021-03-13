package com.addy.newzshots.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.projects.assignment.NewsApplication
import com.projects.assignment.module.newsRepositoryFieldInjectEntryPoint
import dagger.hilt.android.EntryPointAccessors

class NewsFetchWorker(appContext: Context, params: WorkerParameters) :
        CoroutineWorker(appContext, params) {
    companion object {
        const val WORK_NAME = "com.projects.assignment.utils.work.NewsFetchWorker"
    }
    override suspend fun doWork(): Result {
        try {
            val hiltEntryPoint =
                    EntryPointAccessors.fromApplication(applicationContext as NewsApplication, newsRepositoryFieldInjectEntryPoint::class.java)
            val repository = hiltEntryPoint.newsRepo()
            repository.getTopGeneralHeadlines()

        }
        catch (e: Exception) {
            return Result.retry()
        }
        return Result.success()
    }

}