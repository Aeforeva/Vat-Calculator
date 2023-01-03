package com.example.vatcalculator.adapters

import android.view.View
import android.widget.Button
import androidx.databinding.BindingAdapter

@BindingAdapter("buttonVisibility")
fun setVisibility(button: Button, show: Boolean) {
    if (show) button.visibility = View.VISIBLE else button.visibility = View.GONE
}

// Not yet used (For now i just passed true/false from viewModel directly to isEnabled in layout)
@BindingAdapter("availability")
fun setAvailability(view: View, isEnabled: Boolean) {
    view.isEnabled = isEnabled
}