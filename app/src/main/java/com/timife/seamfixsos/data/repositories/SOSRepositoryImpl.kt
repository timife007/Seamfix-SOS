package com.timife.seamfixsos.data.repositories

import com.timife.seamfixsos.data.remote.SOSApi
import com.timife.seamfixsos.data.toRemoteSOSItem
import com.timife.seamfixsos.domain.model.SOSItem
import com.timife.seamfixsos.domain.repositories.SOSRepository
import com.timife.seamfixsos.utils.Resource
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class SOSRepositoryImpl @Inject constructor(
    private val api:SOSApi
):SOSRepository {
    override suspend fun sendSOSDetails(sosItem: SOSItem): Resource<Boolean> {
       return try {
           Resource.Loading<Boolean>(isLoading = true)
            api.sendSOS(sosItem.toRemoteSOSItem())
           Resource.Success(true)
        }catch (e:IOException){
            Resource.Error<Boolean>(e.message)
           Resource.Loading(isLoading = false)
       }catch (e:HttpException){
           Resource.Error<Boolean>(e.message)
           Resource.Loading(isLoading = false)

       }catch (e:Exception){
           Resource.Error<Boolean>(e.message)
           Resource.Loading(isLoading = false)

       }
    }
}