package com.example.vatcalculator

import android.text.InputFilter
import android.text.Spanned

/**
 * Filter used in settings to limit input in tax rate edit texts.
 * Usage example: view.filters = arrayOf<InputFilter>(InputFilterMinMax(0.01, 99.99))
 * */

class InputFilterMinMax(minValue: Double, maxValue: Double) : InputFilter {
    private var min: Double = minValue
    private var max: Double = maxValue

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        try {
            val input = (dest.toString() + source.toString()).toDouble()
            val inputStr = dest.toString() + source.toString()
            if (isInRange(min, max, input, inputStr)) return null
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        return ""
    }

    private fun isInRange(min: Double, max: Double, input: Double, inputStr: String): Boolean {
        return if (inputStr.length < 4 && input < max) true else
            if (inputStr.contains(".") && inputStr.split(".").size > 1) {
                input in min..max && inputStr.split(".")[1].length < 3
            } else {
                input in min..max
            }
    }
}

//TODO checkout about inputStr.split(".").toTypedArray(), do i need it here ?