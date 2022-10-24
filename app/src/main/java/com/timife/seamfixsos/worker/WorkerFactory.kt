package com.timife.seamfixsos.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.timife.seamfixsos.domain.model.SOSItem
import com.timife.seamfixsos.domain.repositories.SOSRepository

class SOSWorkerFactory(private val repository: SOSRepository,private val sosItem:SOSItem) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {

        return when (workerClassName) {
            SOSWorker::class.java.name -> {
                SOSWorker(appContext, workerParameters)
            }

            else ->
                // Return null, so that the base class can delegate to the default WorkerFactory.
                null
        }
    }
}