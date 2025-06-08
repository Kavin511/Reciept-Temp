package com.devstudio.receipto.utils

import androidx.compose.runtime.Composable

sealed class DateSelectionStatus {
    data class SELECTED(val selection: CommonPair<Long, Long>) : DateSelectionStatus()
    object CANCELED : DateSelectionStatus()
}

@Composable
expect fun PlatformSpecificDatePicker(
    initialTimestamp: Long?,
    onDateSelected: (DateSelectionStatus) -> Unit
)
