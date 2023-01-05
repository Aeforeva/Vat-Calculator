package com.example.vatcalculator.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import com.example.vatcalculator.R
import com.example.vatcalculator.VatApplication
import com.example.vatcalculator.adapters.CalculationAdapter
import com.example.vatcalculator.databinding.FragmentHistoryBinding
import com.example.vatcalculator.room.Calculation
import com.example.vatcalculator.viewmodels.MainViewModel
import com.example.vatcalculator.viewmodels.MainViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class HistoryFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            (activity?.application as VatApplication).database.CalculationDao()
        )
    }
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.deleteOldHistory()

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.layout_menu_history, menu)
                setIcon(menu.findItem(R.id.menu_filter))
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_sort -> {
                        viewModel.isSortAsc = !viewModel.isSortAsc
                        submitCorrespondingList()
                        true
                    }
                    R.id.menu_filter -> {
                        viewModel.isFilterOn = !viewModel.isFilterOn
                        activity?.invalidateOptionsMenu() // setIcon(menuItem) alternatively
                        submitCorrespondingList()
                        true
                    }
                    R.id.menu_delete -> {
                        dialogDeleteAll(requireContext())
                        true
                    }
                    else -> false
                }
            }
        }, this, Lifecycle.State.RESUMED)
    }

    private fun setIcon(menuItem: MenuItem?) {
//        if (menuItem == null)
//            return
        /** To redraw icon in correct color use app:iconTint="?attr/colorControlNormal" in menu layout */
        menuItem?.icon =
            if (viewModel.isFilterOn) {
                ContextCompat.getDrawable(this.requireContext(), R.drawable.menu_filter_on)
            } else {
                ContextCompat.getDrawable(this.requireContext(), R.drawable.menu_filter_off)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recycler.adapter = adapter
        submitCorrespondingList()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /** This is awkward, have to move it from onViewCreated to use it in onMenuItemSelected */
    private val adapter = CalculationAdapter({
        it.isLocked = !it.isLocked
        viewModel.updateCalculation(it)
    }, {
        Toast.makeText(context, it.timeStamp.toString(), Toast.LENGTH_SHORT).show()
        dialogOnLongClick(it, requireContext())
    })

    private fun dialogOnLongClick(calculation: Calculation, context: Context) {
        MaterialAlertDialogBuilder(context)
            .setIcon(R.drawable.menu_history)
            .setTitle("${viewModel.getTime(calculation.timeStamp)}   ${viewModel.getDate(calculation.timeStamp)}")
            .setMessage(
                "${calculation.withTax}   ${getString(R.string.sum_with_tax)}\n" +
                        "${calculation.withoutTax}   ${getString(R.string.sum_without_tax)}\n" +
                        "${calculation.tax}   ${getString(R.string.tax)}"
            )
            .setNeutralButton(getString(R.string.delete)) { dialog, _ ->
                viewModel.deleteCalculation(calculation)
                dialog.dismiss()
            }
            .setPositiveButton("Dismiss") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun dialogDeleteAll(context: Context) {
        MaterialAlertDialogBuilder(context)
            .setIcon(R.drawable.menu_delete)
            .setTitle(getString(R.string.delete_all))
            .setMessage("You about to delete all UNLOCKED history records, are you sure ?")
            .setNegativeButton(getString(android.R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(android.R.string.ok)) { dialog, _ ->
                Toast.makeText(context, "ok", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .show()
    }

    private fun submitCorrespondingList() {
        viewModel.historyAsc.removeObservers(viewLifecycleOwner)
        viewModel.historyDesc.removeObservers(viewLifecycleOwner)
        viewModel.historyLockedAsc.removeObservers(viewLifecycleOwner)
        viewModel.historyLockedDesc.removeObservers(viewLifecycleOwner)
        when {
            !viewModel.isSortAsc && !viewModel.isFilterOn -> {
                viewModel.historyDesc.observe(viewLifecycleOwner) {
                    adapter.submitList(it)
                }
            }
            !viewModel.isSortAsc && viewModel.isFilterOn -> {
                viewModel.historyLockedDesc.observe(viewLifecycleOwner) {
                    adapter.submitList(it)
                }
            }
            viewModel.isSortAsc && !viewModel.isFilterOn -> {
                viewModel.historyAsc.observe(viewLifecycleOwner) {
                    adapter.submitList(it)
                }
            }
            else -> {
                viewModel.historyLockedAsc.observe(viewLifecycleOwner) {
                    adapter.submitList(it)
                }
            }
        }
    }
}

/** LiveData version */
//        viewModel.history.observe(viewLifecycleOwner) {
//            adapter.submitList(it)
//        }
/** Flow version */
//        lifecycle.coroutineScope.launch {
//            viewModel.history.collect {
//                adapter.submitList(it)
//            }
//        }