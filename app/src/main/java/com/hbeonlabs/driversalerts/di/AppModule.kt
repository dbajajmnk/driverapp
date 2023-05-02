package com.hbeonlabs.driversalerts.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.hbeonlabs.driversalerts.data.local.db.AppDatabase
import com.hbeonlabs.driversalerts.data.local.db.LocationAndSpeedDao
import com.hbeonlabs.driversalerts.data.local.db.NotificationDao
import com.hbeonlabs.driversalerts.data.remote.api.AppApis
import com.hbeonlabs.driversalerts.utils.constants.EndPoints.BASE_URL
import com.hbeonlabs.driversalerts.utils.network.NetworkResultCallAdapterFactory
import com.hbeonlabs.driversalerts.utils.network.interceptors.AuthInterceptorImpl
import com.hbeonlabs.driversalerts.utils.network.interceptors.NetworkConnectionInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(
        networkConnectionInterceptor: NetworkConnectionInterceptor,
        authInterceptor: AuthInterceptorImpl,
        cache: Cache
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(networkConnectionInterceptor)
            //.addInterceptor(authInterceptor)
            .addInterceptor(onlineInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(NetworkResultCallAdapterFactory.create())
            .client(okHttpClient)
            .build()
    }


    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideCache(@ApplicationContext context: Context): Cache {
        val cacheSize = (5 * 1024 * 1024).toLong()
        return Cache(context.cacheDir, cacheSize)
    }

    var onlineInterceptor: Interceptor = Interceptor { chain ->
        val response: Response = chain.proceed(chain.request())
        val maxAge = 10 // read from cache for 60 seconds even if there is internet connection
        response.newBuilder()
            .header("Cache-Control", "public, max-age=$maxAge")
            .removeHeader("Pragma")
            .build()
    }

    @Provides
    @Singleton
    fun provideRoomDatabase(application: Application):AppDatabase{
        return Room.databaseBuilder(application, AppDatabase::class.java, "driver_alert_local_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideLocationDao(application: Application, database: AppDatabase): LocationAndSpeedDao {
        return database.getLocationAndSpeedDao()
    }

    @Provides
    @Singleton
    fun provideNotificationDao(application: Application, database: AppDatabase): NotificationDao {
        return database.getNotificationDao()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(
        retrofit: Retrofit
    ): AppApis = retrofit.create(AppApis::class.java)

}