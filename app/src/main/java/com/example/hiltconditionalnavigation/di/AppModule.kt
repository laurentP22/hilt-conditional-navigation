package com.example.hiltconditionalnavigation.di

import com.example.hiltconditionalnavigation.data.repository.UserRepository
import com.example.hiltconditionalnavigation.data.repository.UserRepositoryImpl
import com.example.hiltconditionalnavigation.data.source.UserDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}

/**
 * The binding for PlantRepository is on its own module so that we can replace it easily in tests.
 */
@Module
@InstallIn(SingletonComponent::class)
object HiltConditionalNavigationModule {

    @Singleton
    @Provides
    fun provideUserRepository(
        userDataSource: UserDataSource,
        ioDispatcher: CoroutineDispatcher
    ): UserRepository = UserRepositoryImpl(userDataSource,ioDispatcher)
}
