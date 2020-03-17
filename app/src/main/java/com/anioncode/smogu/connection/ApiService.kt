package com.anioncode.retrofit2

import com.anioncode.smogu.ModelAll.FindAll
import com.anioncode.smogu.ModelSensor.SensorsName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("station/findAll")
    fun  findAll(): Call<List<FindAll>>

    @GET("station/sensors/{stationId}")
    fun getData(@Path("stationId") endpoint: String?): Call<List<SensorsName>>
}