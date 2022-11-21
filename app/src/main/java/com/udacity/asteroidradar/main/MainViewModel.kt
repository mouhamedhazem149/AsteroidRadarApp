package com.udacity.asteroidradar.main

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.AsteroidApiFilter
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import retrofit2.await

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
        updateAsteroids(application.baseContext)
    }

    fun updateAsteroids(context: Context) {

        viewModelScope.launch {
            _status.value = UpdateStatus.Loading
            try {
                if (!isOnline(context))
                    throw Exception("Client is Offline")

                withTimeout(20000L) {
                    awaitAll(
                        async {
                            repository.deleteOldAsteroids()
                            repository.refreshAsteroids()
                        },
                        async {
                            _dayPicture.postValue(
                                AsteroidApi.retrofitService.getImageOfTheDay().await()
                            )
                        }
                    )
                }
                _status.value = UpdateStatus.Success
            } catch (exception: Exception) {
                _status.value = UpdateStatus.Fail
            }
        }
    }

    fun updateFilter(filter: AsteroidApiFilter) {
        _filter.value = filter
    }

    //COPYRIGHT
    //https://stackoverflow.com/questions/51141970/check-internet-connectivity-android-in-kotlin
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    return true
                }
            }
        }
        return false
    }
}