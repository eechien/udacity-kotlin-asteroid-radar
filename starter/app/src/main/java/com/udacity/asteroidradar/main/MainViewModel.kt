package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.asteroidradar.Asteroid

class MainViewModel : ViewModel() {

    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    init {
        getAsteroids()
    }

    private fun getAsteroids() {
        // TODO with the Nasa API Service
        _asteroids.value = arrayListOf(
            Asteroid(2, "68347 (2001 KB67)", "2020-02-08", 19.0, 20.0, 21.0, 22.0, false),
            Asteroid(3, "(2015 XK351)", "2020-02-08", 23.0, 24.0, 25.0, 26.0, true)
        )
    }
}