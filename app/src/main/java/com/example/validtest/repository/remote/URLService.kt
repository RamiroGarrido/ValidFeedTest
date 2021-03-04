package com.example.validtest.repository.remote

import com.example.validtest.repository.remote.models.ArtistDTO
import com.example.validtest.repository.remote.models.TrackDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
//Servicios web con los cuales se conectara
interface URLService {

    @GET("?method=geo.gettopartists&format=json")
    suspend fun getTopArtists(@Query("api_key")api_key:String, @Query("country")country:String, @Query("page")page:String): Response<ArtistDTO>
    @GET("?method=geo.gettoptracks&format=json")
    suspend fun getTopTracks(@Query("api_key")api_key:String, @Query("country")country:String, @Query("page")page:String): Response<TrackDTO>
}