package com.devstudio.receipto.navigation

object AppDestinations {
    // Main bottom bar routes
    const val RECEIPTS_LIST_ROUTE = "receipts_list"
    const val SETTINGS_ROUTE = "settings_main" // Changed from "setting" to be more specific

    // Other top-level or feature entry routes
    const val ADD_RECEIPT_ROUTE = "add_receipt"
    const val EDIT_RECEIPT_ROUTE = "edit_receipt" // Base for edit_receipt/{receiptId}
    const val EDIT_RECEIPT_WITH_ID_ARG = "receiptId"
    const val EDIT_RECEIPT_WITH_ID_ROUTE = "$EDIT_RECEIPT_ROUTE/{$EDIT_RECEIPT_WITH_ID_ARG}"

    // Settings sub-routes
    const val CATEGORIES_ROUTE = "categories"
    // Add other settings-related routes here as they are developed
    // e.g., const val PROFILE_SETTINGS_ROUTE = "profile_settings"
    // e.g., const val CURRENCY_SETTINGS_ROUTE = "currency_settings"
}
