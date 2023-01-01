package com.example.vatcalculator

import android.app.Application
import com.example.vatcalculator.room.AppDatabase

class VatApplication: Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}