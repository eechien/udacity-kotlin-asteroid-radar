package com.udacity.asteroidradar.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AsteroidRepository(private val database: AsteroidsDatabase) {

    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids()) {
            it.asDomainModel()
        }

    val todaysAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getTodaysAsteroids()) {
            it.asDomainModel()
        }

    val weeksAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getWeeksAsteroids()) {
            it.asDomainModel()
        }

    suspend fun refreshAsteroids(startDate: String, endDate: String) {
        withContext(Dispatchers.IO) {
            var nasaResponse = NasaApi.retrofitService.getAsteroids(
                startDate = startDate,
                endDate = endDate
            )
            var asteroidList = stringToNetworkAsteroids(nasaResponse)
            database.asteroidDao.insertAll(*asteroidList.asDatabaseModel())
        }
    }
}