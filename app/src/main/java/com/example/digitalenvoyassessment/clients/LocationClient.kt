package com.example.digitalenvoyassessment.clients

import android.location.Location
import kotlinx.coroutines.flow.Flow

//Abstracts Location Updates
interface LocationClient {
    fun getLocationUpdates(interval: Long): Flow<Location>

    // Handles exceptions
    class LocationException(message: String) : Exception()
}