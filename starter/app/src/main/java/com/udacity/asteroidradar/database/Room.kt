package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AsteroidDao {

    @Query("""
        SELECT * FROM databaseAsteroid
        ORDER BY closeApproachDate ASC
    """)
    fun getAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Query("""
        SELECT * FROM databaseAsteroid
        WHERE closeApproachDate = date('now')
        ORDER BY closeApproachDate ASC
    """)
    fun getTodaysAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Query("""
        SELECT * FROM databaseAsteroid
        WHERE closeApproachDate BETWEEN date('now') AND date('now', '+7 days')
        ORDER BY closeApproachDate ASC
    """)
    fun getWeeksAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroid: DatabaseAsteroid)

    @Query("DELETE FROM databaseAsteroid WHERE closeApproachDate < date('now')")
    fun deletePastAsteroids(): Int
}

@Database(entities = [DatabaseAsteroid::class], version = 1)
abstract class AsteroidsDatabase: RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

private lateinit var INSTANCE: AsteroidsDatabase

fun getDatabase(context: Context): AsteroidsDatabase {
    synchronized(AsteroidsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AsteroidsDatabase::class.java,
                "asteroids"
            ).build()
        }
    }
    return INSTANCE
}