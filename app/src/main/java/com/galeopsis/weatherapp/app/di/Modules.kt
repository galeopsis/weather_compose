package com.galeopsis.weatherapp.app.di

import android.app.Application
import androidx.room.Room
import com.galeopsis.weatherapp.model.AppDatabase
import com.galeopsis.weatherapp.model.api.WeatherApi
import com.galeopsis.weatherapp.model.api.SystestNetworkLoggingInterceptor
import com.galeopsis.weatherapp.model.api.WeatherProxyInterceptor
import com.galeopsis.weatherapp.model.cities.CitiesRepository
import com.galeopsis.weatherapp.model.dao.WeatherDao
import com.galeopsis.weatherapp.model.repository.WeatherRepository
import com.galeopsis.weatherapp.model.settings.SettingsRepository
import com.galeopsis.weatherapp.viewmodel.AppViewModel
import com.galeopsis.weatherapp.viewmodel.CitiesViewModel
import com.galeopsis.weatherapp.viewmodel.MainViewModel
import com.galeopsis.weatherapp.viewmodel.SettingsViewModel
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TECHNICAL_BASE_URL = "http://weather-proxy.local/"

val viewModelModule = module {
    viewModel { AppViewModel(get()) }
    viewModel { MainViewModel(get(), get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { CitiesViewModel(get()) }
}

val apiModule = module {
    single<WeatherApi> { get<Retrofit>().create(WeatherApi::class.java) }
}

val netModule = module {
    single { provideCache(androidApplication()) }
    single { WeatherProxyInterceptor(get()) }
    single { SystestNetworkLoggingInterceptor() }
    single { provideHttpClient(get(), get(), get()) }
    single { provideGson() }
    single { provideRetrofit(get(), get()) }
}

val databaseModule = module {
    single { provideDatabase(androidApplication()) }
    single { provideDao(get()) }
}

val repositoryModule = module {
    single { SettingsRepository(androidApplication()) }
    single { CitiesRepository(androidApplication()) }
    single { WeatherRepository(get(), get(), get(), get()) }
}

private fun provideCache(application: Application): Cache {
    val cacheSize = 10L * 1024L * 1024L
    return Cache(application.cacheDir, cacheSize)
}

private fun provideHttpClient(
    cache: Cache,
    weatherProxyInterceptor: WeatherProxyInterceptor,
    systestNetworkLoggingInterceptor: SystestNetworkLoggingInterceptor
): OkHttpClient {
    return OkHttpClient.Builder()
        .cache(cache)
        .addInterceptor(weatherProxyInterceptor)
        .addInterceptor(systestNetworkLoggingInterceptor)
        .build()
}

private fun provideGson(): Gson {
    return GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
        .create()
}

private fun provideRetrofit(factory: Gson, client: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(TECHNICAL_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(factory))
        .client(client)
        .build()
}

private fun provideDatabase(application: Application): AppDatabase {
    return Room.databaseBuilder(application, AppDatabase::class.java, "weather.database")
        .fallbackToDestructiveMigration()
        .build()
}

private fun provideDao(database: AppDatabase): WeatherDao {
    return database.weatherDao
}
