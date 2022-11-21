package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.udacity.asteroidradar.Asteroid
import kotlin.reflect.jvm.internal.impl.types.checker.TypeCheckerContext.SupertypesPolicy.None

@Dao
interface AsteroidDao {

    @Query("select * from asteroids order by closeApproachDate")
    fun getAsteroids(): LiveData<List<Asteroid>>

    @Query("select * from asteroids where closeApproachDate >= :startdate order by closeApproachDate")
    fun getUpcomingAsteroids(startdate: String): LiveData<List<Asteroid>>

    @Query("select * from asteroids where closeApproachDate >= :startdate and closeApproachDate <= :enddate order by closeApproachDate")
    fun getIntervalAsteroids(startdate: String, enddate: String): LiveData<List<Asteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: Asteroid)

    @Transaction
    fun deleteAllAndInsert(vararg asteroids: Asteroid) {
        deleteAsteroids()
        insertAll(*asteroids)
    }

    @Query("delete from asteroids where closeApproachDate < :currentdate")
    fun deleteOldAsteroids(currentdate: String)

    @RawQuery
    fun vacuum(query: SupportSQLiteQuery = SimpleSQLiteQuery("VACUUM;")) : Int

    @Query("DELETE FROM asteroids")
    fun deleteAsteroids()
}

@Database(entities = [Asteroid::class], version = 1)
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