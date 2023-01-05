package com.example.vatcalculator.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.vatcalculator.databinding.CalculationItemBinding
import com.example.vatcalculator.room.Calculation
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class CalculationAdapter(private var onClicked: (Calculation) -> Unit, private var onLongClicked: (Calculation) -> Unit) :
    ListAdapter<Calculation, CalculationAdapter.CalculationViewHolder>(DiffCallback) {

    class CalculationViewHolder(var binding: CalculationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val lockView = binding.calcIsLocked
        fun bind(calculation: Calculation) {
            binding.apply {
                caclTime.text = getTime(calculation.timeStamp)
                caclDate.text = getDate(calculation.timeStamp)
                caclWithTax.text = trimString(calculation.withTax)
                caclWithoutTax.text = trimString(calculation.withoutTax)
                caclTax.text = trimString(calculation.tax)
                isLocked = calculation.isLocked
//                executePendingBindings() //TODO Do i need this in my case ?
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

@SuppressLint("SimpleDateFormat")
fun getTime(timeStamp: Long): String {
    return SimpleDateFormat("HH:mm").format(Date(timeStamp))
}

fun getDate(timeStamp: Long): String {
    return DateFormat.getDateInstance(DateFormat.SHORT).format(Date(timeStamp))
}

fun trimString(text: String): String {
    return if (text.length > 13) text.take(11) + "..." else text
}