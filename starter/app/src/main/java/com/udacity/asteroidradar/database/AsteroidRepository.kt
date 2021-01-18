package com.udacity.asteroidradar.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            var dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            var today = LocalDate.now()
            var todaysDateString = today.format(dateFormatter)
            var weekFromTodayString = today.plusDays(7).format(dateFormatter)
            try {
                var nasaResponse = NasaApi.retrofitService.getAsteroids(
                    startDate = todaysDateString,
                    endDate = weekFromTodayString
                )
                var asteroidList = stringToNetworkAsteroids(nasaResponse)
                database.asteroidDao.insertAll(*asteroidList.asDatabaseModel())
            } catch (e: Exception) {
                Log.e("AsteroidRepository", "Issue querying Nasa API for asteroids.", e)
            }

        }
    }
}