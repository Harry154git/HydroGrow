package com.pemrogamanmobile.hydrogrow.data.remote.service.gemini

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor

object GeminiApiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    fun create(apiKey: String): GeminiApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(logging) // <-- tambahkan di sini
            .addInterceptor { chain ->
                val originalUrl = chain.request().url
                val newUrl = originalUrl.newBuilder()
                    .addQueryParameter("key", apiKey)
                    .build()

                val newRequest = chain.request().newBuilder().url(newUrl).build()
                chain.proceed(newRequest)
            }
            .build()


        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiApiService::class.java)
    }
}
