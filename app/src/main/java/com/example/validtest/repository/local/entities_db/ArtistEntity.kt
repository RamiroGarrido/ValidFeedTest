package com.example.validtest.repository.local.entities_db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
//Entidad para guardar los datos de artista
@Entity(tableName = "artist_table")
data class ArtistEntity(
    var name: String?,
    var listeners: String?,
    var mbid: String?,
    var url: String?,
    var streamable: String?,
    var image: ByteArray?,
    var country:String?,
    var page: String?,
    var perPage: String?,
    @ColumnInfo(name = "total_pages")
    var totalPages: String?,
    @ColumnInfo(name = "total_results")
    var totalResults: String?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L
}