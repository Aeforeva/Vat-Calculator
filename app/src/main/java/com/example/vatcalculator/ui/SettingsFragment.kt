package com.example.vatcalculator.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputFilter
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.vatcalculator.*
import com.example.vatcalculator.databinding.FragmentSettingsBinding
import com.example.vatcalculator.viewmodels.MainViewModel
import com.example.vatcalculator.viewmodels.MainViewModelFactory
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import kotlin.math.roundToInt

class SettingsFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            (activity?.application as VatApplication).database.CalculationDao()
        )
    }
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = requireActivity().getPreferences(AppCompatActivity.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.mainRateTaxEditText.filters = arrayOf<InputFilter>(InputFilterMinMax(0.01, 99.99))
        binding.mainRateTaxEditText.setOnKeyListener { taxView, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                var inputValue = binding.mainRateTaxEditText.text.toString().toDoubleOrNull() ?: 0.0
                inputValue = (inputValue * 100.0).roundToInt() / 100.0
                if (0 < inputValue && inputValue < 100) {
                    viewModel.mainTax = inputValue
                    binding.mainRateTax.hint = viewModel.taxToString(inputValue)
                    sharedPref.edit() { putInt(MAIN_TAX, (inputValue * 100).toInt()).apply() }
                    clearFocusAndHideKeyboard(taxView)
                } else {
                    Snackbar.make(binding.root, R.string.tax_err, Snackbar.LENGTH_LONG).show()
                    return@setOnKeyListener true
                }
            }
            false
        }

        binding.sideRateTaxEditText.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                var inputValue = binding.sideRateTaxEditText.text.toString().toDoubleOrNull() ?: 0.0
                inputValue = (inputValue * 100.0).roundToInt() / 100.0
                if (0 < inputValue && inputValue < 100) {
                    viewModel.sideTax = inputValue
                    binding.sideRateTax.hint = viewModel.taxToString(inputValue)
                    sharedPref.edit() { putInt(SIDE_TAX, (inputValue * 100).toInt()).apply() }
                    clearFocusAndHideKeyboard(view)
                } else {
                    Snackbar.make(binding.root, R.string.tax_err, Snackbar.LENGTH_LONG).show()
                    return@setOnKeyListener true
                }
            }
            false
        }

        binding.sideRateSwitch.setOnCheckedChangeListener { _, isOn ->
            viewModel.showSide.value = isOn
            sharedPref.edit() { putBoolean(SHOW_SIDE, isOn).apply() }
        }

        binding.historySwitch.setOnCheckedChangeListener { _, isOn ->
            viewModel.saveHistory.value = isOn
            sharedPref.edit() { putBoolean(SAVE_HISTORY, isOn).apply() }
        }

        binding.historySlider.addOnChangeListener(Slider.OnChangeListener { _, value, _ ->
            viewModel.setHistoryPeriod(value.toInt())
            viewModel.historyPeriodString.value = when (value.toInt()) {
                0 -> getString(R.string.one_day)
                1 -> getString(R.string.one_week)
                2 -> getString(R.string.one_month)
                3 -> getString(R.string.three_month)
                4 -> getString(R.string.six_month)
                else -> "30 sec"
//            else -> getString(R.string.one_year) // TODO
            }
            sharedPref.edit() { putInt(HISTORY_PERIOD, value.toInt()).apply() }
        })
        binding.historySlider.setLabelFormatter { value: Float ->
            return@setLabelFormatter when (value.toInt()) {
                0 -> getString(R.string.one_day)
                1 -> getString(R.string.one_week)
                2 -> getString(R.string.one_month)
                3 -> getString(R.string.three_month)
                4 -> getString(R.string.six_month)
                else -> "30 sec"
//            else -> getString(R.string.one_year) // TODO
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.deleteOldHistory()
        _binding = null
    }

    private fun clearFocusAndHideKeyboard(view: View) {
        view.clearFocus()
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}