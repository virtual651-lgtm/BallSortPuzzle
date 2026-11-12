/*
 * com.example.ballsort.di
 * Hilt bağımlılık enjeksiyon modülü.
 */
package com.example.ballsort.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.ballsort.data.DataStoreProgressRepository
import com.example.ballsort.data.DataStoreSettingsRepository
import com.example.ballsort.data.ProgressRepository
import com.example.ballsort.data.SettingsRepository
import com.example.ballsort.data.ballSortDataStore
import com.example.ballsort.platform.LanDuelManager
import com.example.ballsort.platform.LanDuelTransport
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProgressRepository(
        impl: DataStoreProgressRepository,
    ): ProgressRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: DataStoreSettingsRepository,
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindLanDuelTransport(
        impl: LanDuelManager,
    ): LanDuelTransport
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.ballSortDataStore

    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}

