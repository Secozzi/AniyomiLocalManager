package xyz.secozzi.aniyomilocalmanager.di

import android.content.Context
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.dsl.module
import java.io.File
import java.util.concurrent.TimeUnit

val NetworkModule = module {
    single { getOkHttpClient(get()) }
}

fun getOkHttpClient(context: Context) : OkHttpClient {
    return OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .callTimeout(2, TimeUnit.MINUTES)
        .cache(
            Cache(
                directory = File(context.cacheDir, "network_cache"),
                maxSize = 10L * 1024 * 1024, // 10 MiB
            ),
        )
        .build()
}
