package com.timife.seamfixsos.data.di

import com.timife.seamfixsos.data.repositories.SOSRepositoryImpl
import com.timife.seamfixsos.domain.repositories.SOSRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class InterfaceModule {

    @Binds
    @Singleton
    abstract fun bindSOSRepository(sosRepositoryImpl: SOSRepositoryImpl):SOSRepository

}