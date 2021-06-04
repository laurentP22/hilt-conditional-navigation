package com.example.hiltconditionalnavigation.di

import com.example.hiltconditionalnavigation.data.FakeUserRepository
import com.example.hiltconditionalnavigation.data.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Repositories binding to use in tests.
 *
 * Hilt will inject a [FakeUserRepository] instead of a [UserRepository].
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class TestModule {
    @Singleton
    @Binds
    abstract fun bindUserRepository(userRepository: FakeUserRepository): UserRepository
}
