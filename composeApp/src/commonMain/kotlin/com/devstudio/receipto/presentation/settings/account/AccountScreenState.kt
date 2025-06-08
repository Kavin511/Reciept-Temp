package com.devstudio.receipto.presentation.settings.account

import com.devstudio.receipto.auth.PlatformUser

data class AccountScreenState(
    val currentUser: PlatformUser? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val requiresReAuthentication: Boolean = false // For operations like delete account
)
