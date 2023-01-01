package com.example.vatcalculator.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(calculation: Calculation)

    @Update
    suspend fun update(calculation: Calculation)

    @Delete
    suspend fun delete(calculation: Calculation)

    @Query("DELETE FROM Calculations WHERE isLocked = 0")
    suspend fun deleteAll()

    @Query("SELECT * from Calculations ORDER BY timeStamp ASC")
    fun getCalculationsAsc(): Flow<List<Calculation>>

    @Query("SELECT * from Calculations ORDER BY timeStamp DESC")
    fun getCalculationsDesc(): Flow<List<Calculation>>
}