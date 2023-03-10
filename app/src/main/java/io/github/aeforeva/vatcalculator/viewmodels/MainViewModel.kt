package io.github.aeforeva.vatcalculator.viewmodels

import androidx.lifecycle.*
import io.github.aeforeva.vatcalculator.room.Calculation
import io.github.aeforeva.vatcalculator.room.CalculationDao
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*

class MainViewModel(private val calculationDao: CalculationDao) : ViewModel() {

    var mainTax = 20.0
    var sideTax = 10.0
    val showSide = MutableLiveData(true)
    val saveHistory = MutableLiveData(true)
    val conciseLayout = MutableLiveData(false)
    val historyPeriod = MutableLiveData(1)
    val historyPeriodString = MutableLiveData("")
    private var historyMilliseconds: Long = 31536000000 // History period in milliseconds
    private var deletedCalculation: Calculation? = null

    fun setHistoryPeriod(period: Int) {
        historyPeriod.value = period
        historyMilliseconds = when (period) {
            0 -> 86400000 // 1 day
            1 -> 604800000 // 1 Week
            2 -> 2678400000 // 1 Month
            3 -> 7884000000 // 3 Month
            4 -> 15768000000 // 6 Month
            else -> 31536000000 // 1 Year
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

    // Only for dialog, for item in list corresponding functions in CalculationAdapter
    fun getTime(timeStamp: Long): String {
        return DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(timeStamp))
    }

    // Only for dialog, for item in list corresponding functions in CalculationAdapter
    fun getDate(timeStamp: Long): String {
        return DateFormat.getDateInstance(DateFormat.LONG).format(Date(timeStamp))
    }

    fun updateCalculation(calculation: Calculation) {
        viewModelScope.launch {
            calculationDao.update(calculation)
        }
    }

    fun deleteCalculation(calculation: Calculation) {
        deletedCalculation = calculation
        viewModelScope.launch {
            calculationDao.delete(calculation)
        }
    }

    fun undoRecordDelete() {
        viewModelScope.launch {
            deletedCalculation?.let { calculationDao.insert(it) }
        }
    }

    fun deleteUnlocked() {
        viewModelScope.launch {
            calculationDao.deleteUnlocked()
        }
    }

    fun deleteAll() {
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

    var isFilterOn = false
    var isSortAsc = false
    val historyAsc: LiveData<List<Calculation>> = calculationDao.getAllAsc().asLiveData()
    val historyDesc: LiveData<List<Calculation>> = calculationDao.getAllDesc().asLiveData()
    val historyLockedAsc: LiveData<List<Calculation>> = calculationDao.getLockedAsc().asLiveData()
    val historyLockedDesc: LiveData<List<Calculation>> = calculationDao.getLockedDesc().asLiveData()

    /** This will invoke before viewModel gets historyMax from sharedPref !!! (test on def values) */
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