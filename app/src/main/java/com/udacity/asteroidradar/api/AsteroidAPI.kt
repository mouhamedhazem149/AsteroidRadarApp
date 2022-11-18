package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

enum class AsteroidApiFilter(val value: String) {
    SHOW_WEEK("week"),
    SHOW_TODAY("today"),
    SHOW_SAVED("saved")
}

    interface AsteroidApiService {
        @GET("neo/rest/v1/feed?")
        fun getAsteroidsJSON(@Query("start_date") startDate : String ,@Query("end_date") endDate : String,@Query("api_key") api_key : String =Constants.API_KEY) : Call<String>

        @GET("planetary/apod?api_key=${Constants.API_KEY}")
        fun getImageOfTheDay() : Call<PictureOfDay>
    }

    object AsteroidApi {

        private val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        private val retrofit = Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(Constants.BASE_URL)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        val retrofitService: AsteroidApiService by lazy {
            retrofit.create(AsteroidApiService::class.java)
        }
    }

