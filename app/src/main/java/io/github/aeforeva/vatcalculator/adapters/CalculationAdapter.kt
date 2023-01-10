package io.github.aeforeva.vatcalculator.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.aeforeva.vatcalculator.databinding.CalculationItemBinding
import io.github.aeforeva.vatcalculator.room.Calculation
import java.text.DateFormat
import java.util.*

class CalculationAdapter(
    private var onClicked: (Calculation) -> Unit,
    private var onLongClicked: (Calculation) -> Unit
) :
    ListAdapter<Calculation, CalculationAdapter.CalculationViewHolder>(DiffCallback) {

    class CalculationViewHolder(private var binding: CalculationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val lockView = binding.calcIsLocked
        fun bind(calculation: Calculation) {
            binding.apply {
                calcTime.text = getTime(calculation.timeStamp)
                calcDate.text = getDate(calculation.timeStamp)
                calcWithTax.text = calculation.withTax
                calcWithoutTax.text = calculation.withoutTax
                calcTax.text = calculation.tax
                isLocked = calculation.isLocked
//                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalculationViewHolder {
        val layout =
            CalculationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalculationViewHolder(layout)
    }

    override fun onBindViewHolder(holder: CalculationViewHolder, position: Int) {
        val current = getItem(position)
        holder.lockView.setOnClickListener {
            onClicked(current)
            notifyItemChanged(position)
        }
        holder.itemView.setOnLongClickListener {
            onLongClicked(current)
            true
        }
        holder.bind(current)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Calculation>() {
            override fun areItemsTheSame(oldItem: Calculation, newItem: Calculation): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Calculation, newItem: Calculation): Boolean {
                return oldItem.timeStamp == newItem.timeStamp
            }
        }
    }
}

fun getTime(timeStamp: Long): String {
    return DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(timeStamp))
}

fun getDate(timeStamp: Long): String {
    return DateFormat.getDateInstance(DateFormat.SHORT).format(Date(timeStamp))
}