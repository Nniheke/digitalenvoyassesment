package com.example.digitalenvoyassessment

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.example.digitalenvoyassesment.BuildConfig
import com.example.digitalenvoyassessment.extensions.hasLocationPermission
import com.example.digitalenvoyassessment.workers.LocationWorker
import java.util.concurrent.TimeUnit

class DigitalEnvoyApplication : Application() {

    //I selected WorkManager because it is the recommended library for persistent work & scheduled work is guaranteed to execute.
    // I selected PeriodicWorkRequest because it's a workRequest for repeating work.This work executes multiple times until it is cancelled.

    private val permissionState = MutableLiveData<Boolean>()

    val _permissonState : LiveData<Boolean> = permissionState

    override fun onCreate() {
        super.onCreate()
        checkPermissionsStartWorker()
    }

    //Checks if permissions are granted and starts location worker
    private fun checkPermissionsStartWorker() {
        permissionState.value = hasLocationPermission()
        if(permissionState.value == true){
            startLocationWorker()
        }
    }

    //Sets periodic work builder config and starts work manager instance
    fun startLocationWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val locationWork = PeriodicWorkRequestBuilder<LocationWorker>(
            if (BuildConfig.DEBUG) 20 else 1,
            if (BuildConfig.DEBUG) TimeUnit.MINUTES else TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            LOCATION_WORKER,
            ExistingPeriodicWorkPolicy.UPDATE,
            locationWork
        )
    }

    companion object {
        const val LOCATION_WORKER = "LOCATION_WORKER"
    }
}