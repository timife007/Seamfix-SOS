package com.timife.seamfixsos.utils

import android.app.Activity
import android.content.Context
import android.location.LocationManager
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.SettingsClient
import com.timife.seamfixsos.utils.Constants.GPS_REQUEST_CHECK_SETTINGS
import timber.log.Timber
import javax.inject.Inject

class GpsUtil @Inject constructor(private val context: Context) {
    private val settingsClient: SettingsClient = LocationServices.getSettingsClient(context)
    private val locationSettingsRequest: LocationSettingsRequest
    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    init {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(LocationLiveData.locationRequest)
        locationSettingsRequest = builder.build()
        builder.setAlwaysShow(true)
    }

    fun turnGPSOn(OnGpsListener: OnGpsListener?) {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGpsListener?.gpsStatus(true)
        } else {
            settingsClient
                .checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(context as Activity) {
                    OnGpsListener?.gpsStatus(true)
                }.addOnFailureListener(context) { exception ->

                    when ((exception as ApiException).statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            try {
                                val resApiException = exception as ResolvableApiException
                                resApiException.startResolutionForResult(context, GPS_REQUEST_CHECK_SETTINGS)
                            } catch (sendIntentException: Exception) {
                                sendIntentException.printStackTrace()
                                Timber.i("PendingIntent unable to execute request. ")
                            }
                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            val errorMessage =
                                "Location settings are inadequate, and cannot be " + "fixed here. Fix in Settings."
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                }
        }
    }

    interface OnGpsListener {
        fun gpsStatus(isGPSEnabled: Boolean)
    }
}