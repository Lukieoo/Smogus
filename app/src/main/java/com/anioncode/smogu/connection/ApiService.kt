package com.anioncode.retrofit2

import com.anioncode.smogu.model.ModelAll.FindAll
import com.anioncode.smogu.model.ModelIndex.ModelIndex
import com.anioncode.smogu.model.ModelSensor.SensorsName
import com.anioncode.smogu.model.ModelSensorId.SensorbyID
import io.reactivex.rxjava3.core.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("station/findAll")
    fun  findAll(): Call<List<FindAll>>

    //RXJAVA2
    @GET("station/findAll")
    fun  findAllRX(): Observable<List<FindAll>>

    @GET("station/sensors/{stationId}")
    fun getData(@Path("stationId") endpoint: String?): Call<List<SensorsName>>

    //RXJAVA2
    @GET("station/sensors/{stationId}")
    fun getDataRX(@Path("stationId") endpoint: String?): Observable<List<SensorsName>>

    @GET("data/getData/{sensorId}")
    fun getSensor(@Path("sensorId") endpoint: String?): Call<SensorbyID>

    //RXJAVA2
    @GET("data/getData/{sensorId}")
    fun getSensorRX(@Path("sensorId") endpoint: String?): Observable<SensorbyID>

    @GET("aqindex/getIndex/{stationId}")
    fun getIndex(@Path("stationId") endpoint: String?): Call<ModelIndex>

    //RXJAVA2
    @GET("aqindex/getIndex/{stationId}")
    fun getIndexRX(@Path("stationId") endpoint: String?): Observable<ModelIndex>
}