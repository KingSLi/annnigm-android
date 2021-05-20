package edu.phystech.annnigm


import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ScrollView

fun setHideKeyboardOnTouch(context: Context, view: View) {
    //Set up touch listener for non-text box views to hide keyboard.
    try {
        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view is EditText || view is ScrollView)) {

            view.setOnTouchListener { v, event ->
                val input = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                input.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                false
            }
        }
        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {

            for (i in 0 until view.childCount) {

                val innerView = view.getChildAt(i)

                setHideKeyboardOnTouch(context, innerView)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

}
