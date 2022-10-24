package com.timife.seamfixsos.data.remote.model


import com.squareup.moshi.Json

data class RemoteLocation(
    @field:Json(name = "longitude")
    val longitude: String,
    @field:Json(name = "latitude")
    val latitude: String
)