package com.example.validtest.repository.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.validtest.repository.local.entities_db.TrackEntity

//interfaz con las operaciones de base de datos para la tabla de track
@Dao
interface TrackDAO {
    @Insert
    suspend fun insertTracks(tracks: List<TrackEntity>)
    @Insert
    suspend fun insertTracks(track: TrackEntity)
    @Update
    suspend fun updateTracks(tracks: List<TrackEntity>):Int
    @Update
    suspend fun updateTrack(tracks: TrackEntity):Int
    @Query(value = "select * from track_table where country like :country and page = :page")
    fun getTracksByCountryYPage(country:String, page:String): List<TrackEntity?>
    @Query("delete from track_table")
    suspend fun deleteTrackTable()
}