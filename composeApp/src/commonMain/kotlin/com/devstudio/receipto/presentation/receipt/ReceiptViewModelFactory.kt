package com.devstudio.receipto.presentation.receipt

import androidx.compose.runtime.Composable
import com.devstudio.receipto.ReceiptViewModel // Import the actual ViewModel

@Composable
expect fun rememberReceiptViewModel(): ReceiptViewModel
