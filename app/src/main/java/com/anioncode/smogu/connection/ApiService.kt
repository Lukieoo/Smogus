package com.anioncode.retrofit2

import com.anioncode.smogu.Model.ModelAll.FindAll
import com.anioncode.smogu.Model.ModelIndex.ModelIndex
import com.anioncode.smogu.Model.ModelSensor.SensorsName
import com.anioncode.smogu.Model.ModelSensorId.SensorbyID
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("station/findAll")
    fun  findAll(): Call<List<FindAll>>

    @GET("station/sensors/{stationId}")
    fun getData(@Path("stationId") endpoint: String?): Call<List<SensorsName>>

    @GET("data/getData/{sensorId}")
    fun getSensor(@Path("sensorId") endpoint: String?): Call<List<SensorbyID>>

    @GET("aqindex/getIndex/{stationId}")
    fun getIndex(@Path("stationId") endpoint: String?): Call<ModelIndex>

}