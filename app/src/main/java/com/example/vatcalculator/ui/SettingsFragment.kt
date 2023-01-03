package com.example.vatcalculator.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.vatcalculator.*
import com.example.vatcalculator.databinding.FragmentSettingsBinding
import com.example.vatcalculator.viewmodels.MainViewModel
import com.example.vatcalculator.viewmodels.MainViewModelFactory
import com.google.android.material.slider.Slider
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
                    Toast.makeText(context, getString(R.string.tax_err), Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(context, getString(R.string.tax_err), Toast.LENGTH_SHORT).show()
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
//            else -> getString(R.string.one_year)
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
//            else -> getString(R.string.one_year)
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

    //        binding.mainRateTaxEditText.filters = arrayOf<InputFilter>(MinMaxFilter(1, 100))
    // Custom class to define min and max for the edit text
    inner class MinMaxFilter(minValue: Int, maxValue: Int) : InputFilter {
        private var intMin: Int = minValue
        private var intMax: Int = maxValue

        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dStart: Int,
            dEnd: Int
        ): CharSequence? {
            try {
                val input = Integer.parseInt(dest.toString() + source.toString())
                if (isInRange(intMin, intMax, input)) {
                    return null
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            return ""
        }

        // Check if input c is in between min a and max b and
        // returns corresponding boolean
        private fun isInRange(a: Int, b: Int, c: Int): Boolean {
            return if (b > a) c in a..b else c in b..a
        }
    }
}