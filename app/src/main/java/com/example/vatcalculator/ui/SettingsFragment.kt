package com.example.vatcalculator.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.vatcalculator.R
import com.example.vatcalculator.SHOW_SIDE
import com.example.vatcalculator.VatApplication
import com.example.vatcalculator.databinding.FragmentCalculationBinding
import com.example.vatcalculator.viewmodels.MainViewModel
import com.example.vatcalculator.viewmodels.MainViewModelFactory

class SettingsFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            (activity?.application as VatApplication).database.CalculationDao()
        )
    }
    private var _binding: FragmentCalculationBinding? = null
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}