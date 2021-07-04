package com.example.myphotoloaderapp.di

import android.app.Application
import androidx.room.Room
import com.example.myphotoloaderapp.data.local.database.FavoriteDatabase
import com.example.myphotoloaderapp.data.remote.PhotoApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(PhotoApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): PhotoApi =
        retrofit.create(PhotoApi::class.java)

    @Provides
    @Singleton
    fun provideDatabase(
        app: Application,
        callback: FavoriteDatabase.Callback
    ) = Room.databaseBuilder(app, FavoriteDatabase::class.java, "favorite_database")
        .fallbackToDestructiveMigration()
        .addCallback(callback)
        .build()

    @Provides
    fun provideTaskDao(db: FavoriteDatabase) = db.favoriteDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope