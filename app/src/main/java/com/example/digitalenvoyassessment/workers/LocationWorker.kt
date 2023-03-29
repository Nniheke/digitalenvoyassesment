package com.example.digitalenvoyassessment.workers

import android.content.Context
import android.location.Location
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.digitalenvoyassesment.R
import com.example.digitalenvoyassessment.clients.DefaultLocationClient
import com.example.digitalenvoyassessment.clients.LocationClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext

// I chose CoroutineWorker because it provides interop with Kotlin Coroutines and allows us to call suspend functions in doWork. I chose callBackFlow as my return method because it allows elements
// to be produced by code running in a different context or concurrently & also returns a cold flow, so that block is called every time a terminal operator is applied to the flow and ensures thread-safety as well as context preservation

class LocationWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private var locationClient: LocationClient = DefaultLocationClient(
        applicationContext,
        LocationServices.getFusedLocationProviderClient(applicationContext)
    )

    override suspend fun doWork(): Result {
        return try {
            locationClient.getLocationUpdates(5000L)
                .catch { e -> e.printStackTrace() }
                .take(3).apply {
                    toastLocations(this)
                }
            return Result.success()
        } catch (throwable: Throwable) {
            Log.e(TAG, "Unable to get location")
            Result.failure()
        }
    }

    // Takes param locations as a flow and transforms them to a list of Location objects then displays as a custom toast message
    private suspend fun toastLocations(locations: Flow<Location>) {
        withContext(Dispatchers.Main) {
            val inflater = LayoutInflater.from(applicationContext)
            val layout = inflater.inflate(R.layout.custom_long_toast, null)
            val textView =
                layout.findViewById<TextView>(R.id.textview_toast_message)
            textView.text = locations.toList().toString()
            val toast = Toast(applicationContext)
            toast.duration = Toast.LENGTH_LONG
            toast.view = layout
            toast.show()
            //TODO: Shared Preferences: This is a good option if we need to store a small amount of data (such as a few recent locations), and if the data doesn't need to be queried or sorted in any particular way.
            //TODO: SQLite Database: This is a good option if we need to store a large amount of data (such as a history of all locations), and if we need to query or sort the data in various ways (such as by date, by location, or by user ID).
        }
    }

    companion object {
        const val TAG = "LocationWorker"
    }
}





