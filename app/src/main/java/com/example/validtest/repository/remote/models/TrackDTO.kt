package com.example.validtest.repository.remote.models

import com.google.gson.annotations.SerializedName
//Modelo que mapea el JSON obtenido del webservice de top tracks
data class TrackDTO(
    @SerializedName("tracks") var tracks:Tracks
)
data class Tracks(
    @SerializedName("track") var track:List<Song>?,
    @SerializedName("@attr") var attr:Attr2?,
    @SerializedName("error") var error:String?,
    @SerializedName("message") var message:String?
)
data class Song(
    @SerializedName("name") var name:String?,
    @SerializedName("duration") var duration:String?,
    @SerializedName("listeners") var listeners:String?,
    @SerializedName("mbid") var mbid:String?,
    @SerializedName("url") var url:String?,
    @SerializedName("streamable") var streamable:Streamable?,
    @SerializedName("artist") var artist:ArtistT?,
    @SerializedName("image") var image:List<Image>?,
    @SerializedName("@attr") var attr:Attr1?,
)
data class Streamable(
    @SerializedName("#text") var text:String?,
    @SerializedName("fulltrack") var fullTrack:String?
)
data class ArtistT(
    @SerializedName("name") var name:String?,
    @SerializedName("mbid") var mbid:String?,
    @SerializedName("url") var url:String?
)
data class Image(
    @SerializedName("#text") var text:String?,
    @SerializedName("size") var size:String?
)
data class Attr1(
    @SerializedName("rank") var rank:String?
)
data class Attr2(
    @SerializedName("country") var country:String?,
    @SerializedName("page") var page:String?,
    @SerializedName("perPage") var perPage:String?,
    @SerializedName("totalPages") var totalPages:String?,
    @SerializedName("total") var total:String?
)