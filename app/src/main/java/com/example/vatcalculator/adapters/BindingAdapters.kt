package com.example.vatcalculator.adapters

import android.view.View
import android.widget.Button
import androidx.databinding.BindingAdapter

@BindingAdapter("buttonVisibility")
fun setVisibility(button: Button, show: Boolean) {
    if (show) button.visibility = View.VISIBLE else button.visibility = View.GONE
}