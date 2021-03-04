package com.example.validtest.repository.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.validtest.repository.local.daos.ArtistDAO
import com.example.validtest.repository.local.daos.TrackDAO
import com.example.validtest.repository.local.entities_db.ArtistEntity
import com.example.validtest.repository.local.entities_db.TrackEntity
//Base de datos para la persistencia de datos offline
//Se utiliza el patron singleton.
@Database(
    entities = [TrackEntity::class, ArtistEntity::class],
    version = 2,
    exportSchema = false
)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun trackDAO(): TrackDAO
    abstract fun artistDAO(): ArtistDAO
    //ALLOW ACCESSING THE OBJECT WITHOUT AN INSTANCE
    companion object {
        @Volatile
        private var INSTANCE: LocalDatabase? = null

        fun getInstance(context: Context): LocalDatabase {
            //ALLOW ONLY ONE THEAD AT TIME TO ACCESS
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        LocalDatabase::class.java,
                        "local_database"
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

}