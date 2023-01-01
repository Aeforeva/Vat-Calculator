package com.example.vatcalculator.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.*
import com.example.vatcalculator.room.Calculation
import com.example.vatcalculator.room.CalculationDao
import kotlinx.coroutines.launch
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

    fun saveCalculation(withTax: String, withoutTax: String, tax: String) {
        val tsLong = System.currentTimeMillis()
        Log.d("Time Stamp", tsLong.toString())
        Log.d("Time Stamp to Date", tsLong.getDate())
        Log.d("Time Stamp to Time", tsLong.getTime())
        viewModelScope.launch {
            calculationDao.insert(Calculation(tsLong, withTax, withoutTax, tax, false))
        }
    }

    fun updateCalculation(calculation: Calculation) {
        viewModelScope.launch {
            calculationDao.update(calculation)
        }
    }

    fun deleteCalculation(calculation: Calculation) {
        viewModelScope.launch {
            calculationDao.delete(calculation)
        }
    }

    fun deleteHistory() {
        viewModelScope.launch {
            calculationDao.deleteAll()
        }
    }

    val historyAsc: LiveData<List<Calculation>> = calculationDao.getCalculationsAsc().asLiveData()
    val historyDesc: LiveData<List<Calculation>> = calculationDao.getCalculationsDesc().asLiveData()

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