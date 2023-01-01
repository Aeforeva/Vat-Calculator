package com.example.vatcalculator.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Calculations")
data class Calculation(
    @PrimaryKey(autoGenerate = false) val timeStamp: Long,
    val withTax : Double,
    val withoutTax: Double,
    val tax: Double,
    val isSaved: Boolean
)