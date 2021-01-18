package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AsteroidRepository(private val database: AsteroidsDatabase) {

    val asteroids: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao.getAsteroids()) {
        it.asDomainModel()
    }

    suspend fun refreshAsteroids(endDate: String) {
        withContext(Dispatchers.IO) {
            var dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            var todaysDateString = LocalDate.now().format(dateFormatter)
            var nasaResponse =  NasaApi.retrofitService.getAsteroids(
                startDate = todaysDateString,
                endDate = endDate
            )
            var asteroidList = stringToNetworkAsteroids(nasaResponse)
            database.asteroidDao.insertAll(*asteroidList.asDatabaseModel())
        }
    }
}