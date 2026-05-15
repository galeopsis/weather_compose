package com.galeopsis.weatherapp.app

import android.app.Application
import com.galeopsis.weatherapp.app.di.apiModule
import com.galeopsis.weatherapp.app.di.databaseModule
import com.galeopsis.weatherapp.app.di.netModule
import com.galeopsis.weatherapp.app.di.repositoryModule
import com.galeopsis.weatherapp.app.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            androidLogger(Level.ERROR)
            modules(
                viewModelModule,
                repositoryModule,
                netModule,
                apiModule,
                databaseModule
            )
        }
    }
}
