package com.timife.seamfixsos.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import com.google.android.gms.location.*
import com.timife.seamfixsos.domain.model.LocationItem

class LocationLiveData(context: Context) : LiveData<LocationItem>() {

    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    override fun onInactive() {
        super.onInactive()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("MissingPermission")
    override fun onActive() {
        super.onActive()
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.also {
                    setLocationData(it)
                }
            }
        startLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback,null)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult ?: return
            for (location in locationResult.locations) {
                setLocationData(location)
            }
        }
    }

    private fun setLocationData(location: Location) {
        value = LocationItem(
            longitude = location.longitude.toString(),
            latitude = location.latitude.toString()
        )
    }

    companion object {
        val locationRequest = LocationRequest.Builder(100,5000).build()
    }
}