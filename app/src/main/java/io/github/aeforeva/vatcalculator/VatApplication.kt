package io.github.aeforeva.vatcalculator

import android.app.Application
import io.github.aeforeva.vatcalculator.room.AppDatabase

class VatApplication: Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}