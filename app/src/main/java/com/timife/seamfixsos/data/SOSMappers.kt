package com.timife.seamfixsos.data

import com.timife.seamfixsos.data.remote.model.RemoteLocation
import com.timife.seamfixsos.data.remote.model.RemoteSOSItem
import com.timife.seamfixsos.domain.model.LocationItem
import com.timife.seamfixsos.domain.model.SOSItem

fun SOSItem.toRemoteSOSItem():RemoteSOSItem{
    return RemoteSOSItem(
        phoneNumbers = phoneNumbers,
        image = image,
        location = locationItem.toRemoteLocation()
    )
}

fun LocationItem.toRemoteLocation():RemoteLocation{
    return RemoteLocation(
        longitude = longitude,
        latitude = latitude
    )
}