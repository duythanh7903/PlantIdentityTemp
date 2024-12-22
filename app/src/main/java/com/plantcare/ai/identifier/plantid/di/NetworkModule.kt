package com.plantcare.ai.identifier.plantid.di

import com.plantcare.ai.identifier.plantid.BuildConfig
import com.plantcare.ai.identifier.plantid.app.AppConstants.NAMED_LOCATION
import com.plantcare.ai.identifier.plantid.app.AppConstants.NAMED_PLANT
import com.plantcare.ai.identifier.plantid.app.AppConstants.NAMED_WEATHER
import com.plantcare.ai.identifier.plantid.data.network.service.LocationService
import com.plantcare.ai.identifier.plantid.data.network.service.PlantService
import com.plantcare.ai.identifier.plantid.data.network.service.WeatherService
import com.plantcare.ai.identifier.plantid.utils.network_adapter_factory.ResultWrapperCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder().addInterceptor(loggingInterceptor)
    } else {
        OkHttpClient.Builder()
    }.apply {
        readTimeout(30, TimeUnit.SECONDS)
        connectTimeout(30, TimeUnit.SECONDS)
        writeTimeout(30, TimeUnit.SECONDS)
        retryOnConnectionFailure(true)
    }.build()

    @Singleton
    @Provides
    @Named(NAMED_PLANT)
    fun providePlantRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(BuildConfig.PLANT_DOMAIN).addCallAdapterFactory(
                ResultWrapperCallAdapterFactory()
            ).client(okHttpClient).build()

    @Singleton
    @Provides
    @Named(NAMED_LOCATION)
    fun provideLocationRetrofit(okHttpClient: OkHttpClient) =
        Retrofit.Builder().addConverterFactory(
            MoshiConverterFactory.create()
        ).baseUrl(BuildConfig.LOCATION_DOMAIN).addCallAdapterFactory(
            ResultWrapperCallAdapterFactory()
        ).client(okHttpClient).build()

    @Singleton
    @Provides
    @Named(NAMED_WEATHER)
    fun provideWeatherRetrofit(okHttpClient: OkHttpClient) = Retrofit.Builder().addConverterFactory(
        MoshiConverterFactory.create()
    ).baseUrl(BuildConfig.WEATHER_DOMAIN).addCallAdapterFactory(
        ResultWrapperCallAdapterFactory()
    ).client(okHttpClient).build()

    @Provides
    @Singleton
    fun providePlantAcpiService(@Named(NAMED_PLANT) retrofit: Retrofit) =
        retrofit.create(PlantService::class.java)

    @Provides
    @Singleton
    fun provideLocationService(@Named(NAMED_LOCATION) retrofit: Retrofit) =
        retrofit.create(LocationService::class.java)


    @Provides
    @Singleton
    fun provideWeatherService(@Named(NAMED_WEATHER) retrofit: Retrofit) =
        retrofit.create(WeatherService::class.java)


}