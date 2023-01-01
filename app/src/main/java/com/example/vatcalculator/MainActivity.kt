package com.example.vatcalculator

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
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
        viewModel.showSide = sharedPref.getBoolean(SHOW_SIDE, true)
        viewModel.saveHistory = sharedPref.getBoolean(SAVE_HISTORY, true)
        viewModel.historyMax = sharedPref.getInt(HISTORY_MAX, 100)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupActionBarWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onNavigateUp()
    }
}