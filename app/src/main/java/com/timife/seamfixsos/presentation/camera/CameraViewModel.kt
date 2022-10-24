package com.timife.seamfixsos.presentation.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timife.seamfixsos.domain.model.SOSItem
import com.timife.seamfixsos.domain.repositories.SOSRepository
import com.timife.seamfixsos.utils.LocationLiveData
import com.timife.seamfixsos.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val sosRepository: SOSRepository
) : ViewModel() {

    @Inject
    lateinit var locationLiveData: LocationLiveData

    fun fetchLocationLiveData() = locationLiveData

    private val _result = MutableLiveData<Resource<Boolean>>()
    val result: LiveData<Resource<Boolean>>
    get() = _result



    fun sendSOS(sosItem: SOSItem){
        viewModelScope.launch {
            when(sosRepository.sendSOSDetails(sosItem)){
                is Resource.Success -> {
                    _result.value = Resource.Success(true)
                }
                is Resource.Loading ->{
                    _result.value = Resource.Loading(true)
                }
                is Resource.Error ->{
                    _result.value = Resource.Error("Unable to send SOS; Retry")
                }
            }
        }
    }
}

