package com.timife.seamfixsos.data.remote

import com.timife.seamfixsos.data.remote.model.RemoteSOSItem
import retrofit2.http.POST

interface SOSApi{
    @POST("api/v1/create")
    suspend fun sendSOS(sosItem: RemoteSOSItem)
}