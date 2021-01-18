package com.udacity.asteroidradar.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.network.NasaApi
import com.udacity.asteroidradar.database.AsteroidRepository
import com.udacity.asteroidradar.database.getDatabase
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception
import java.time.LocalDate
import java.time.format.DateTimeFormatter


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
            var dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            var today = LocalDate.now()
            var todaysDateString = today.format(dateFormatter)
            var weekFromTodayString = today.plusDays(7).format(dateFormatter)
            try {
                asteroidRepository.refreshAsteroids(todaysDateString, weekFromTodayString)
            } catch (e: HttpException) {
                Log.d("MainViewModel", "Unable to refresh Asteroids from Nasa API, check internet connection", e)
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