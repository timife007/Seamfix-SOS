package com.timife.seamfixsos.domain.model


data class SOSItem(
    val phoneNumbers: List<String>,
    val image: String,
    val locationItem: LocationItem
)