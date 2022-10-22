package com.evcharger.evcharger

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ChargerService {

	@GET("ChargerInfo")
	fun getChargerData(@Query("KEY")KEY : String,
		               @Query("Type") Type : String): Call<ChargerInfo>
}