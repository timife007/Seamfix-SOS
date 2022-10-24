package com.timife.seamfixsos.data.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.timife.seamfixsos.utils.LocationLiveData
import com.timife.seamfixsos.utils.SOSharedPref
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideSharedPreferencesHelper(context: Context): SOSharedPref {
        return SOSharedPref.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideLocationLiveData(context: Context): LocationLiveData {
        return LocationLiveData(context)
    }
}