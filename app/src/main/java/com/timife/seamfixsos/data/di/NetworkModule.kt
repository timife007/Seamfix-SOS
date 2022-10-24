package com.timife.seamfixsos.data.di

import android.app.Application
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.timife.seamfixsos.data.remote.SOSApi
import com.timife.seamfixsos.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideApi(app: Application):SOSApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(
                        ChuckerInterceptor.Builder(app)
                            .collector(
                                ChuckerCollector(
                                    app,
                                    showNotification = true,
                                    retentionPeriod = RetentionManager.Period.ONE_WEEK
                                )
                            )
                            .maxContentLength(250000L)
                            .redactHeaders(emptySet())
                            .alwaysReadResponseBody(false)
                            .build()
                    ).build()
            )
            .build()
            .create(SOSApi::class.java)
    }

}