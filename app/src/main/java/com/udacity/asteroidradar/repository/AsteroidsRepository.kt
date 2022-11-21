package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.await
import java.text.SimpleDateFormat
import java.util.*

class AsteroidsRepository(private val database: AsteroidsDatabase) {

    val dateFormatter = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())

    val today = dateFormatter.format(Calendar.getInstance().time)

    var asteroids: LiveData<List<Asteroid>> = database.asteroidDao.getUpcomingAsteroids(today)

    suspend fun deleteOldAsteroids() {
        withContext(Dispatchers.IO) {
            database.asteroidDao.deleteOldAsteroids(today)
            database.asteroidDao.vacuum()
        }
    }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val paramStart = today
            val paramEnd = ""

            val parsedAsteroids = parseAsteroidsJsonResult(
                JSONObject(
                    AsteroidApi.retrofitService.getAsteroidsJSON(paramStart, paramEnd).await()
                )
            )

            try {
                database.asteroidDao.deleteAllAndInsert(*parsedAsteroids.toTypedArray())
                database.asteroidDao.vacuum()
            } catch (exc: Exception) {
                Log.i("ex", exc.message!!)
            }
        }
    }
}