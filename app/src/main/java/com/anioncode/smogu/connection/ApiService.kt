package com.anioncode.retrofit2

import com.anioncode.smogu.ModelAll.FindAll
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("station/findAll")
    fun  findAll(): Call<List<FindAll>>
}