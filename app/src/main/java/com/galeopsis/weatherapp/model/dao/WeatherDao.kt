package com.galeopsis.weatherapp.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.galeopsis.weatherapp.model.data.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(weatherEntity: WeatherEntity)

    @Query("SELECT * FROM weather")
    fun observeAll(): Flow<List<WeatherEntity>>

    @Query("DELETE FROM weather")
    suspend fun deleteAllData()
}
