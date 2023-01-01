package com.example.vatcalculator.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Calculations")
data class Calculation(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val withTax : Double,
    val withoutTax: Double,
    val tax: Double,
    val isSaved: Boolean
)