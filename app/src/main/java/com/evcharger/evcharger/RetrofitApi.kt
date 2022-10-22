package com.evcharger.evcharger

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


object RetrofitApi {
	private val okHttpClient: OkHttpClient by lazy {
		OkHttpClient.Builder()
			.addInterceptor(HttpLoggingInterceptor().apply {
				level = HttpLoggingInterceptor.Level.BODY
			})
			.build()
	}

	private const val BASE_URL =
		"http://openapi.kepco.co.kr/"

	private val retrofit: Retrofit by lazy {
		Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create())
			.client(okHttpClient)
			.baseUrl(BASE_URL)
			.build()
	}

	val chargerService: ChargerService by lazy {
		retrofit.create(ChargerService::class.java)
	}
}


