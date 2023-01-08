package com.example.vatcalculator.adapters

import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.vatcalculator.R

@BindingAdapter("buttonVisibility")
fun setVisibility(button: Button, show: Boolean) {
    if (show) button.visibility = View.VISIBLE else button.visibility = View.GONE
}

@BindingAdapter("viewVisibility")
fun setVisibility(view: View, hide: Boolean) {
    if (hide) view.visibility = View.GONE else view.visibility = View.VISIBLE
}

@BindingAdapter("lockStatus")
fun setLockImage(imageView: ImageView, isLocked: Boolean) {
    if (isLocked) {
        imageView.setImageResource(R.drawable.lock_close)
    } else {
        imageView.setImageResource(R.drawable.lock_open)
    }
}