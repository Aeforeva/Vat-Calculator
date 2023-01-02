package com.example.vatcalculator.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.vatcalculator.R
import com.example.vatcalculator.SHOW_SIDE
import com.example.vatcalculator.VatApplication
import com.example.vatcalculator.databinding.FragmentCalculationBinding
import com.example.vatcalculator.databinding.FragmentSettingsBinding
import com.example.vatcalculator.viewmodels.MainViewModel
import com.example.vatcalculator.viewmodels.MainViewModelFactory

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
        sharedPref.edit() { putBoolean(SHOW_SIDE, true).apply() }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        binding.sideRateCheckBox.setOnCheckedChangeListener { _, isOn ->
            viewModel.showSide = isOn
        }

        binding.historySwitch.setOnCheckedChangeListener { _, isOn ->
            if (isOn) {
                Toast.makeText(context, "true", Toast.LENGTH_SHORT).show()
                viewModel.saveHistory = true
            } else {
                Toast.makeText(context, "false", Toast.LENGTH_SHORT).show()
                viewModel.saveHistory = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}