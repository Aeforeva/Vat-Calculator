package com.example.vatcalculator.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.vatcalculator.R
import com.example.vatcalculator.VatApplication
import com.example.vatcalculator.databinding.FragmentCalculationBinding
import com.example.vatcalculator.viewmodels.MainViewModel
import com.example.vatcalculator.viewmodels.MainViewModelFactory
import java.text.NumberFormat

class CalculationFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            (activity?.application as VatApplication).database.CalculationDao()
        )
    }
    private var _binding: FragmentCalculationBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.layout_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_history -> {
                        val action =
                            CalculationFragmentDirections.actionCalculationFragmentToHistoryFragment()
                        findNavController().navigate(action)
                        true
                    }
                    R.id.menu_settings -> {
                        val action =
                            CalculationFragmentDirections.actionCalculationFragmentToSettingsFragment()
                        findNavController().navigate(action)
                        true
                    }
                    else -> false
                }
            }
        }, this, Lifecycle.State.RESUMED)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCalculationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        binding.sumWithTaxEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                clearEditTexts()
                lastFocusedEditText = R.id.sum_with_tax_edit_text
            }
        }
        binding.sumWithoutTaxEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                clearEditTexts()
                lastFocusedEditText = R.id.sum_without_tax_edit_text
            }
        }
        binding.taxEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                clearEditTexts()
                lastFocusedEditText = R.id.tax_edit_text
            }
        }
        binding.calcSide.setOnClickListener { calcTax(viewModel.sideTax) }
        binding.calsMain.setOnClickListener { calcTax(viewModel.mainTax) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Calculator logic
     */

    private var lastFocusedEditText: Int? = null

    private fun clearEditTexts() {
        binding.sumWithTaxEditText.text = null
        binding.sumWithoutTaxEditText.text = null
        binding.taxEditText.text = null
    }

    private fun clearFocus() {
        binding.sumWithTaxEditText.clearFocus()
        binding.sumWithoutTaxEditText.clearFocus()
        binding.taxEditText.clearFocus()
    }

    private fun currencyFormatter(num: Double): String {
        return NumberFormat.getCurrencyInstance().format(num)
    }

    /** calc fun that take tax rate as param (20% = 20) */
    private fun calcTax(taxInput: Double) {
        val tax = taxInput / 100
        // Get doubles from all 3 EditText
        val a = binding.sumWithTaxEditText.text.toString().toDoubleOrNull()
        val b = binding.sumWithoutTaxEditText.text.toString().toDoubleOrNull()
        val c = binding.taxEditText.text.toString().toDoubleOrNull()
        // Check user input
        if (a == null && b == null && c == null) {
            Toast.makeText(context, getString(R.string.empty_input_msg), Toast.LENGTH_SHORT).show()
            return
        }
        // select what to calc based on last focused EditText
        when (lastFocusedEditText) {
            R.id.sum_with_tax_edit_text -> {
                binding.sumWithTaxEditText.setText(currencyFormatter(a!!))
                binding.sumWithoutTaxEditText.setText(currencyFormatter(a / (1 + tax)))
                binding.taxEditText.setText(currencyFormatter(a - a / (1 + tax)))
            }
            R.id.sum_without_tax_edit_text -> {
                binding.sumWithTaxEditText.setText(currencyFormatter(b!! * (1 + tax)))
                binding.sumWithoutTaxEditText.setText(currencyFormatter(b))
                binding.taxEditText.setText(currencyFormatter(b * tax))
            }
            R.id.tax_edit_text -> {
                binding.sumWithTaxEditText.setText(currencyFormatter(c!! * (1 + 1 / tax)))
                binding.sumWithoutTaxEditText.setText(currencyFormatter(c / tax))
                binding.taxEditText.setText(currencyFormatter(c))
            }
        }
        clearFocus()

        /** Save to database if enabled */
        if (viewModel.saveHistory) {
            viewModel.saveCalculation(
                binding.sumWithTaxEditText.text,
                binding.sumWithoutTaxEditText.text,
                binding.taxEditText.text
            )
        }
    }
}