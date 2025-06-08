package com.devstudio.receipto.utils

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import com.google.android.material.datepicker.MaterialDatePicker

private fun Context.findActivity(): FragmentActivity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is FragmentActivity) return context
        context = context.baseContext
    }
    return null
}

@Composable
actual fun PlatformSpecificDatePicker(
    initialTimestamp: Long?,
    onDateSelected: (DateSelectionStatus) -> Unit
) {
    val context = LocalContext.current
    val activity = context.findActivity()

    if (activity == null) {
        onDateSelected(DateSelectionStatus.CANCELED)
        return
    }

    val fragmentManager = activity.supportFragmentManager

    val datePicker = MaterialDatePicker.Builder.datePicker()
        .setSelection(initialTimestamp ?: MaterialDatePicker.todayInUtcMilliseconds())
        .setTitleText("Select Date")
        .build()

    datePicker.addOnPositiveButtonClickListener { selectedTimestamp ->
        onDateSelected(DateSelectionStatus.SELECTED(CommonPair(selectedTimestamp, selectedTimestamp)))
    }
    datePicker.addOnNegativeButtonClickListener {
        onDateSelected(DateSelectionStatus.CANCELED)
    }
    datePicker.addOnCancelListener {
        onDateSelected(DateSelectionStatus.CANCELED)
    }
    datePicker.show(fragmentManager, "MaterialDatePicker")
}
