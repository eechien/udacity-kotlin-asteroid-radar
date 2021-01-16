package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

class MainViewModel : ViewModel() {

    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    init {
        getAsteroids()
    }

    private fun getAsteroids() {
        viewModelScope.launch {
            try {
                var dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                var todaysDateString = LocalDate.now().format(dateFormatter)
                var nasaResponse =  NasaApi.scalarRetrofitService.getAsteroids(
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

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayAsteroidComplete() {
        _navigateToSelectedAsteroid.value = null
    }
}