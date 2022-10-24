package com.timife.seamfixsos.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.timife.seamfixsos.domain.model.SOSItem
import com.timife.seamfixsos.domain.repositories.SOSRepository
import com.timife.seamfixsos.utils.Resource
import com.timife.seamfixsos.utils.SOSharedPref
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SOSWorker @Inject constructor(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    @Inject
    lateinit var sosRepository: SOSRepository

    private var sharedPref = SOSharedPref.getInstance(context)

    override suspend fun doWork(): Result {
        val sosItem = sharedPref.getSOSItem()

        return withContext(Dispatchers.IO) {
            when (val result = sosItem?.let { sosRepository.sendSOSDetails(it) }) {
                is Resource.Success<*> -> {
                    if (result.data != false) {
                        Result.success()
                    } else {
                        Result.failure()
                    }
                }
                else -> Result.failure()
            }
        }
    }

}