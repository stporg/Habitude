package com.example.habitude

import android.app.Application
import android.util.Log
import androidx.work.*
import com.example.habitude.background.UpdateDataWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import java.util.*

/**
 *  The recurring task of updating the habits every day is placed in the 'Application' class as it in the
 *  base class for maintaining global application state. It is instantiated before any other component and
 *  provides a convenient entry point to perform initialisation tasks.
 */

@HiltAndroidApp
class HabitudeApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        try {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<UpdateDataWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(applicationContext)
                .enqueueUniquePeriodicWork(
                    "UpdateDataWorker",
                    ExistingPeriodicWorkPolicy.KEEP,
                    workRequest)
        } catch (e: Exception) {
            Log.e("WorkManager", "Error scheduling the work")
        }
    }

    private fun calculateInitialDelay(): Long {
        val currentTimeMillis = System.currentTimeMillis()

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTimeMillis

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        // If the target time has already passed today, move it to the next day
        if (calendar.timeInMillis <= currentTimeMillis) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val targetTimeMillis = calendar.timeInMillis

        val delayMillis = targetTimeMillis - currentTimeMillis

        return delayMillis
    }
}