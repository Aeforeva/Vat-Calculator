package com.example.vatcalculator.viewmodels

import android.annotation.SuppressLint
import android.text.Editable
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vatcalculator.room.CalculationDao
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(private val calculationDao: CalculationDao) : ViewModel() {

    var mainTax = 0.0
    var sideTax = 0.0
    var showSide = false
    var saveHistory = false
    var historyMax = 1

    fun taxToString(tax: Double): String {
        return if ((tax * 100).toInt() % 100 == 0) "${tax.toInt()} %" else "$tax %"
    }

    @SuppressLint("SimpleDateFormat")
    private fun Long.getDate(): String {
        return SimpleDateFormat("dd.MM.yy").format(Date(this))
    }

    @SuppressLint("SimpleDateFormat")
    private fun Long.getTime(): String {
        return SimpleDateFormat("HH:mm").format(Date(this))
    }

    fun saveCalculation(text: Editable?, text1: Editable?, text2: Editable?) {
        // TODO save to Room Database
        val tsLong = System.currentTimeMillis()
        Log.d("Time Stamp", tsLong.toString())
        Log.d("Time Stamp", tsLong.getDate())
        Log.d("Time Stamp", tsLong.getTime())
        Log.d("History 120", text.toString())
        Log.d("History 100", text1.toString())
        Log.d("History 20", text2.toString())
    }
}

class MainViewModelFactory(private val calculationDao: CalculationDao) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(calculationDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}