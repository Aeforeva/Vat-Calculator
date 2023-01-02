package com.example.vatcalculator.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
    var historyMax: Long = 20000

    var isAscend = false
    var isFilter = false

    fun setHistoryMax(option: Int) {
        when (option) {
            1 -> historyMax = 86400000 // 1 day
            2 -> historyMax = 604800000 // 1 week
            3 -> historyMax = 1209600000 // 2 week
            4 -> historyMax = 2678400000 // 1 month
        }
    }

    fun taxToString(tax: Double): String {
        return if ((tax * 100).toInt() % 100 == 0) "${tax.toInt()} %" else "$tax %"
    }

    @SuppressLint("SimpleDateFormat")
    fun getDate(timeStamp: Long): String {
        return SimpleDateFormat("dd.MM.yy").format(Date(timeStamp))
    }

    @SuppressLint("SimpleDateFormat")
    fun Long.getTime(): String {
        return SimpleDateFormat("HH:mm:ss").format(Date(this))
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

    fun deleteOldHistory(timeStampLimit: Long) {
        Log.d("OUT Cur", "${System.currentTimeMillis()} - ${System.currentTimeMillis().getTime()}")
        Log.d("OUT Old", "$timeStampLimit - ${timeStampLimit.getTime()}")
        viewModelScope.launch {
            Log.d(
                "IN Cur",
                "${System.currentTimeMillis()} - ${System.currentTimeMillis().getTime()}"
            )
            Log.d("IN Old", "$timeStampLimit - ${timeStampLimit.getTime()}")
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