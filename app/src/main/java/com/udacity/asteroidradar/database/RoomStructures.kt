package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AsteroidDao {

    @Query("select * from asteroids order by closeApproachDate")
    fun getAsteroids(): LiveData<List<DbAsteroid>>

    @Query("select * from asteroids where closeApproachDate >= :startdate order by closeApproachDate")
    fun getUpcomingAsteroids(startdate : String): LiveData<List<DbAsteroid>>

    @Query("select * from asteroids where closeApproachDate >= :startdate and closeApproachDate <= :enddate order by closeApproachDate")
    fun getIntervalAsteroids(startdate : String,enddate : String): LiveData<List<DbAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DbAsteroid)

    @Query("delete from asteroids where closeApproachDate < :currentdate")
    fun deleteOldAsteroids(currentdate : String)
}

    @Database(entities = [DbAsteroid::class], version = 1)
    abstract class AsteroidsDatabase : RoomDatabase() {
        abstract val asteroidDao: AsteroidDao
    }

    private lateinit var INSTANCE: AsteroidsDatabase

    fun getDatabase(context: Context): AsteroidsDatabase {
        synchronized(AsteroidsDatabase::class.java) {
            if (!::INSTANCE.isInitialized) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    AsteroidsDatabase::class.java,
                    "asteroids"
                ).build()
            }
        }
        return INSTANCE
    }