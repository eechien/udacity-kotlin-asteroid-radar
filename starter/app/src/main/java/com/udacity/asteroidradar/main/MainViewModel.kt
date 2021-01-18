package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.database.AsteroidRepository
import com.udacity.asteroidradar.database.getDatabase
import kotlinx.coroutines.launch
import java.lang.Exception


enum class AsteroidFilter { TODAY, WEEK, SAVED }

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)


    private val _filter = MutableLiveData(AsteroidFilter.WEEK)

    init {
        getAsteroids()
        getPictureOfDay()
    }

    val asteroids = Transformations.switchMap(_filter) {
        when (it) {
            AsteroidFilter.TODAY -> asteroidRepository.todaysAsteroids
            AsteroidFilter.WEEK -> asteroidRepository.weeksAsteroids
            else -> asteroidRepository.asteroids
        }
    }


    private fun getAsteroids() {
        viewModelScope.launch {
            asteroidRepository.refreshAsteroids()
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

    fun updateFilter(newFilter: AsteroidFilter) {
        _filter.value = newFilter
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