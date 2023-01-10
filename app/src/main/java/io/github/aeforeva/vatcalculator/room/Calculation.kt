package io.github.aeforeva.vatcalculator.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Calculations")
data class Calculation(
    @PrimaryKey(autoGenerate = false) val timeStamp: Long,
    val withTax : String,
    val withoutTax: String,
    val tax: String,
    var isLocked: Boolean
)