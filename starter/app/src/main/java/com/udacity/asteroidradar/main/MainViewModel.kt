package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainViewModel : ViewModel() {

    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    init {
        getAsteroids()
        getPictureOfDay()
    }

    private fun getAsteroids() {
        viewModelScope.launch {
            try {
                var dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                var todaysDateString = LocalDate.now().format(dateFormatter)
                var nasaResponse =  NasaApi.retrofitService.getAsteroids(
                    startDate = todaysDateString,
                    endDate = todaysDateString // TODO add filter to change this
                )
                var nasaJson = JSONObject(nasaResponse)
                var listResult = parseAsteroidsJsonResult(nasaJson)
                if (listResult.size > 0) {
                    _asteroids.value = listResult
                }
            } catch (e: Exception) {
                _asteroids.value = listOf()
            }
        }
    }

    private fun getPictureOfDay() {
        viewModelScope.launch {
            try {
                _pictureOfDay.value = NasaApi.retrofitService.getPictureOfTheDay()
            } catch (e: Exception) {
                _pictureOfDay.value = null
            }
        }
    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayAsteroidComplete() {
        _navigateToSelectedAsteroid.value = null
    }
}