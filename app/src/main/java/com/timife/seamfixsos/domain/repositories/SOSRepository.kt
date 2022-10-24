package com.timife.seamfixsos.domain.repositories

import com.timife.seamfixsos.domain.model.SOSItem
import com.timife.seamfixsos.utils.Resource

interface SOSRepository {
    suspend fun sendSOSDetails(sosItem: SOSItem): Resource<Boolean>
}