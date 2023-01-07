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

    @Query("DELETE FROM Calculations")
    suspend fun deleteAll()

    @Query("DELETE FROM Calculations WHERE isLocked = 0")
    suspend fun deleteUnlocked()

    @Query("DELETE FROM Calculations WHERE isLocked = 0 AND timeStamp < :timeStampLimit")
    suspend fun deleteOld(timeStampLimit: Long)

    @Query("SELECT * from Calculations ORDER BY timeStamp ASC")
    fun getAllAsc(): Flow<List<Calculation>>

    @Query("SELECT * from Calculations ORDER BY timeStamp DESC")
    fun getAllDesc(): Flow<List<Calculation>>

    @Query("SELECT * from Calculations Where isLocked = 1 ORDER BY timeStamp ASC")
    fun getLockedAsc(): Flow<List<Calculation>>

    @Query("SELECT * from Calculations Where isLocked = 1 ORDER BY timeStamp DESC")
    fun getLockedDesc(): Flow<List<Calculation>>
}