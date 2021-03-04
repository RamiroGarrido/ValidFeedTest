package com.example.validtest.repository.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.validtest.repository.local.entities_db.ArtistEntity

//interfaz con las operaciones de base de datos para la tabla de artista
@Dao
interface ArtistDAO {
    @Insert
    suspend fun insertArtist(artist: ArtistEntity)
    @Insert
    suspend fun insertArtists(movies: List<ArtistEntity>)
    @Update
    suspend fun updateArtist(artist: ArtistEntity):Int
    @Update
    suspend fun updateArtists(artists: List<ArtistEntity>):Int
    @Query(value = "select * from artist_table where country like :country and page = :page")
    fun getArtistsByCountryYPage(country:String, page:String): List<ArtistEntity?>
    @Query("delete from artist_table")
    suspend fun clear()
}