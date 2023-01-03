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
    val showSide = MutableLiveData(false)
    val saveHistory = MutableLiveData(false)
    val historyPeriod = MutableLiveData(0)
    val historyPeriodString = MutableLiveData("")
    private var historyMilliseconds: Long = 31536000000 // History period in milliseconds

    fun setHistoryPeriod(period: Int) {
        historyPeriod.value = period
        historyMilliseconds = when (period) {
            0 -> 86400000 // 1 day
            1 -> 604800000 // 1 Week
            2 -> 2678400000 // 1 Month
            3 -> 7884000000 // 3 Month
            4 -> 15768000000 // 6 Month
            else -> 30_000 // 30sec
//            else -> 31536000000 // 1 Year
        }
    }


    fun taxToString(tax: Double): String {
        return if ((tax * 100).toInt() % 100 == 0) "${tax.toInt()} %" else "$tax %"
    }

    fun saveCalculation(withTax: String, withoutTax: String, tax: String) {
        val timeStamp = System.currentTimeMillis()
        viewModelScope.launch {
            calculationDao.insert(Calculation(timeStamp, withTax, withoutTax, tax, false))
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

    fun deleteOldHistory() {
        val timeStampLimit = System.currentTimeMillis() - historyMilliseconds
        viewModelScope.launch {
            calculationDao.deleteOld(timeStampLimit)
        }
    }

    val historyAsc: LiveData<List<Calculation>> = calculationDao.getAllAsc().asLiveData()
    val historyDesc: LiveData<List<Calculation>> = calculationDao.getAllDesc().asLiveData()
    val historyLockedAsc: LiveData<List<Calculation>> = calculationDao.getLockedAsc().asLiveData()
    val historyLockedDesc: LiveData<List<Calculation>> = calculationDao.getLockedDesc().asLiveData()

    /** This will invoke before viewModel refresh historyMax from sharedPref !!! (test on def values) */
//    init {
//        deleteOldHistory(System.currentTimeMillis() - historyMax)
//    }
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