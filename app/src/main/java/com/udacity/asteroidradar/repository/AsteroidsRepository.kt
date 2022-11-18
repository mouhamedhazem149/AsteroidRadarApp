package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.AsteroidApiFilter
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.asDatabaseModel
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.await
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

class AsteroidsRepository(private val database: AsteroidsDatabase) {

    val dateFormatter = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())

    val today = dateFormatter.format(Calendar.getInstance().time)

    private var paramStart = today
    private var paramEnd = ""

    var asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getUpcomingAsteroids(today)) {
            it.asDomainModel()
        }

    suspend fun deleteOldAsteroids() {
        withContext(Dispatchers.IO) {
            database.asteroidDao.deleteOldAsteroids(today)
        }
    }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val parsedAsteroids = parseAsteroidsJsonResult(
                JSONObject(
                    AsteroidApi.retrofitService.getAsteroidsJSON(paramStart, paramEnd).await()
                )
            )

            database.asteroidDao.insertAll(*(parsedAsteroids.asDatabaseModel().toTypedArray()))
        }
    }
}