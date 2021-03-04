package com.example.validtest.repository.remote.models

import com.google.gson.annotations.SerializedName
//Modelo que mapea el JSON obtenido del webservice de top artists
data class ArtistDTO(
    @SerializedName("topartists") var artists:Artist
)
data class Artist(
    @SerializedName("artist") var artist:List<ArtistA>,
    @SerializedName("@attr") var attr:Attr2?,
    @SerializedName("error") var error:Int?,
    @SerializedName("message") var message:String?
)
data class ArtistA(
    @SerializedName("name") var name:String?,
    @SerializedName("listeners") var listeners:String?,
    @SerializedName("mbid") var mbid:String?,
    @SerializedName("url") var url:String?,
    @SerializedName("streamable") var streamable:String?,
    @SerializedName("image") var image:List<Image>?
)