package com.example.validtest.repository.local.entities_db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
//Entidad para guardar los datos de track
@Entity(tableName = "track_table")
data class TrackEntity(
    var name:String?,
    var duration:String?,
    var listeners:String?,
    var mbid:String?,
    var url:String?,
    var streamable:String?,
    @ColumnInfo(name="artist_name")
    var artistName:String?,
    @ColumnInfo(name="artist_mbid")
    var artistMbid:String?,
    @ColumnInfo(name="artist_url")
    var artistUrl:String?,
    var image:ByteArray?,
    var rank:String?,
    var country:String?,
    var page: String?,
    var perPage: String?,
    @ColumnInfo(name="total_pages")
    var totalPages: String?,
    @ColumnInfo(name="total_results")
    var totalResults: String?
)
{
    @PrimaryKey(autoGenerate = true)
    var id: Long=0L
}