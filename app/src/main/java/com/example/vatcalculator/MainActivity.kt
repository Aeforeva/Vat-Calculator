package com.example.vatcalculator

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.vatcalculator.viewmodels.MainViewModel
import com.example.vatcalculator.viewmodels.MainViewModelFactory

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(
            (application as VatApplication).database.CalculationDao()
        )
    }
    private lateinit var sharedPref: SharedPreferences
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPref = getPreferences(MODE_PRIVATE)
        viewModel.mainTax = sharedPref.getInt(MAIN_TAX, 2000).toDouble() / 100
        viewModel.sideTax = sharedPref.getInt(SIDE_TAX, 1000).toDouble() / 100
        viewModel.showSide.value = sharedPref.getBoolean(SHOW_SIDE, true)
        viewModel.saveHistory.value = sharedPref.getBoolean(SAVE_HISTORY, true)
        viewModel.conciseLayout.value = sharedPref.getBoolean(CONCISE_LAYOUT, false)
        viewModel.setHistoryPeriod(sharedPref.getInt(HISTORY_PERIOD, 1)) // default = 0-4, else
        // Set string here instead of viewModel, so i can simply use string resources
        viewModel.historyPeriodString.value = when (sharedPref.getInt(HISTORY_PERIOD, 1)) {
            0 -> getString(R.string.one_day)
            1 -> getString(R.string.one_week)
            2 -> getString(R.string.one_month)
            3 -> getString(R.string.three_month)
            4 -> getString(R.string.six_month)
            else -> "30 sec"
//            else -> getString(R.string.one_year)
        }
        viewModel.deleteOldHistory()

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupActionBarWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onNavigateUp()
    }
}