package com.example.validtest.repository.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
//Clase para conexiones http con retrofit
class ConnectionService {
    fun getRetrofit(url: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}