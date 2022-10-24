package com.timife.seamfixsos.data.remote.model


import com.squareup.moshi.Json

data class RemoteSOSItem(
    @field:Json(name = "phoneNumbers")
    val phoneNumbers: List<String>,
    @field:Json(name = "image")
    val image: String,
    @field:Json(name = "locationItem")
    val location: RemoteLocation
)