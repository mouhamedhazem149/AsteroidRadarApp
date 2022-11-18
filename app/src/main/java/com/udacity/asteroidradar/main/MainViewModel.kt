package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.AsteroidApiFilter
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDao
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.await
import kotlin.coroutines.coroutineContext

enum class UpdateStatus {
    Loading,
    Success,
    Fail
}

class MainViewModel(application: Application)  : AndroidViewModel(application) {

    val database = getDatabase(application)
    val repository = AsteroidsRepository(database)

    val asteroids: LiveData<List<Asteroid>> = repository.asteroids

    private val _status = MutableLiveData<UpdateStatus>()
    val status : LiveData<UpdateStatus>
    get() = _status

    private val _filter = MutableLiveData<AsteroidApiFilter>()
    val filter : LiveData<AsteroidApiFilter>
        get() = _filter

    private val _dayPicture = MutableLiveData<PictureOfDay>()
    val dayPicture : LiveData<PictureOfDay>
    get() = _dayPicture

    init {
        updateAsteroids()
    }

    fun updateAsteroids() {
        viewModelScope.launch {
            _status.value = UpdateStatus.Loading
            try {
                repository.refreshAsteroids()

                val picture = AsteroidApi.retrofitService.getImageOfTheDay().await()
                _dayPicture.value = picture

                _status.value = UpdateStatus.Success
            } catch (exception: Exception) {
                _status.value = UpdateStatus.Fail
            }
        }
    }

    fun updateFilter(filter: AsteroidApiFilter) {
        _filter.value = filter
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}