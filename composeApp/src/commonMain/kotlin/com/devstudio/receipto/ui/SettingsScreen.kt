package com.devstudio.receipto.ui


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.runtime.* // Import all from runtime for remember, mutableStateOf, collectAsState, DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
// import cafe.adriel.voyager.navigator.LocalNavigator // Removed Voyager import
// import cafe.adriel.voyager.navigator.currentOrThrow // Removed Voyager import
// import com.devstudio.receipto.navigation.CategoriesScreen // Removed Voyager screen import
import com.devstudio.receipto.navigation.AppDestinations // Import AppDestinations
import com.devstudio.receipto.platform.getAppVersion
import com.devstudio.receipto.presentation.settings.account.AccountScreenState
import com.devstudio.receipto.presentation.settings.account.AccountViewModel
import com.devstudio.receipto.presentation.settings.account.rememberAccountViewModel
import com.devstudio.receipto.presentation.settings.currency.CurrencyScreenState
import com.devstudio.receipto.presentation.settings.currency.CurrencySelectionViewModel
import com.devstudio.receipto.presentation.settings.currency.rememberCurrencySelectionViewModel
import com.devstudio.receipto.ui.settings.CurrencySelectionBottomSheet
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
// Android specific imports for Google Sign-In launcher
import androidx.compose.runtime.LaunchedEffect
import com.devstudio.receipto.auth.AuthService // Required for Android specific part
import com.devstudio.receipto.auth.AuthResult // Required for Android specific part

// This expect/actual will provide the necessary platform specifics for launching sign-in
// and handling its result.
@Composable
expect fun HandlePlatformSignIn(
    shouldTriggerSignIn: Boolean,
    onSignInLaunched: () -> Unit,
    viewModel: AccountViewModel // Or specific callback: (AuthResult) -> Unit
)
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import coil.compose.AsyncImage


// Main Composable for the Settings Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val appVersion = getAppVersion()

    // Account ViewModel
    val accountViewModel: AccountViewModel = rememberAccountViewModel()
    val accountUiState by accountViewModel.uiState.collectAsState()
    val initiateGoogleSignIn by accountViewModel.initiateGoogleSignInEvent.collectAsState()
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // Currency ViewModel
    val currencyViewModel: CurrencySelectionViewModel = rememberCurrencySelectionViewModel()
    val currencyUiState by currencyViewModel.uiState.collectAsState()
    var showCurrencySheet by remember { mutableStateOf(false) }


    // Handle ViewModel lifecycles
    DisposableEffect(Unit) {
        onDispose {
            accountViewModel.onClear()
            currencyViewModel.onClear() // Also clear currency VM
        }
    }

    // Android-specific Google Sign-In handling
    // This Composable will be empty on non-Android platforms due to expect/actual
    HandlePlatformSignIn(
        shouldTriggerSignIn = initiateGoogleSignIn,
        onSignInLaunched = { accountViewModel.onGoogleSignInLaunched() },
        viewModel = accountViewModel // Pass VM to allow calling processGoogleSignInResult
    )


    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete Account?") },
            text = { Text("Are you sure you want to delete your account? This action cannot be undone and all your receipt data will be lost.") },
            confirmButton = {
                Button(
                    onClick = {
                        accountViewModel.deleteAccount()
                        showDeleteConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                Button(onClick = { showDeleteConfirmDialog = false }) { Text("Cancel") }
            }
        )
    }


    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Settings") },
            navigationIcon = { // Example: Using NavController to popBackStack for a back button
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            }
        )
    }) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionHeader("General")
                // "Account" item removed from here
                // "Appearance" item removed
                // "Notifications" item removed
                Spacer(modifier = Modifier.height(24.dp)) // This spacer might be redundant if "General" becomes empty or has few items.
                                                       // For now, keeping structure. If "General" has no items, header can be removed too.
                                                       // Let's assume "General" might have other items later, or we remove this header if it's truly empty.
                                                       // For this task, only specific items are removed.
                SettingsSectionHeader("Receipt Management")
                SettingsItem(
                    icon = Icons.Default.AttachMoney,
                    title = "Currency",
                    subtitle = currencyUiState.selectedCurrency?.name ?: "Select currency",
                    onClick = { showCurrencySheet = true }
                )
                SettingsItem(
                    icon = Icons.Default.Category,
                    title = "Categories",
                    subtitle = "Configure receipt categorization",
                    onClick = { navController.navigate(AppDestinations.CATEGORIES_ROUTE) } // Use constant
                )
                // "Storage" item hidden by commenting out
                /*
                SettingsItem(
                    icon = Icons.Default.Cloud,
                    title = "Storage",
                    subtitle = "Manage receipt storage options",
                    onClick = { /* TODO: navigator.push(StorageScreen) */ }
                )
                */
                Spacer(modifier = Modifier.height(24.dp))
                SettingsSectionHeader("Support")
                SettingsItem(
                    icon = Icons.Default.HelpOutline,
                    title = "Help Center",
                    subtitle = "", // No subtitle in image
                    onClick = { /* TODO: navigator.push(HelpCenterScreen) */ }
                )
                SettingsItem(
                    icon = Icons.Default.MailOutline,
                    title = "Contact Us",
                    subtitle = "", // No subtitle in image
                    onClick = { /* TODO: Open external link or mail app */ }
                )
                SettingsItem(
                    icon = Icons.Default.Description,
                    title = "Terms of Service",
                    subtitle = "", // No subtitle in image
                    onClick = { /* TODO: navigator.push(TermsScreen) */ }
                )
                SettingsItem(
                    icon = Icons.Default.Policy,
                    title = "Privacy Policy",
                    subtitle = "", // No subtitle in image
                    onClick = { /* TODO: navigator.push(PrivacyPolicyScreen) */ }
                )
                Spacer(modifier = Modifier.height(24.dp))
                AccountSection(
                    uiState = accountUiState,
                    onSignInClicked = { accountViewModel.triggerSignInWithGoogle() },
                    onSignOutClicked = { accountViewModel.signOut() },
                    onDeleteAccountClicked = { showDeleteConfirmDialog = true },
                    modifier = Modifier.fillMaxWidth()
                )
                VersionInfo("Version $appVersion")
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (showCurrencySheet) {
            CurrencySelectionBottomSheet(
                uiState = currencyUiState,
                onQueryChanged = currencyViewModel::onSearchQueryChanged,
                onCurrencySelected = { currency ->
                    currencyViewModel.onCurrencySelected(currency)
                    // showCurrencySheet = false // ViewModel updates state, which should close sheet or be handled by sheet itself
                },
                onDismiss = { showCurrencySheet = false }
            )
        }
    }
}


@Composable
fun AccountSection(
    uiState: AccountScreenState,
    onSignInClicked: () -> Unit,
    onSignOutClicked: () -> Unit,
    onDeleteAccountClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(vertical = 16.dp)) {
        Text(
            text = "Account",
            style = MaterialTheme.typography.titleMedium, // Or your SettingsSectionHeader style
            modifier = Modifier.padding(bottom = 16.dp) // Increased bottom padding
        )

        if (uiState.isLoading && uiState.currentUser == null) { // Show loading only if no user yet (initial load)
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (uiState.currentUser != null) {
            // Signed-In UI
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
                if (!uiState.currentUser.photoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = uiState.currentUser.photoUrl,
                        contentDescription = "Profile Photo",
                        modifier = Modifier.size(48.dp).clip(CircleShape) // Slightly larger icon
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile Icon",
                        modifier = Modifier.size(48.dp), // Slightly larger icon
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(uiState.currentUser.displayName ?: "N/A", style = MaterialTheme.typography.titleSmall)
                    Text(uiState.currentUser.email ?: "N/A", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Button(
                onClick = onSignOutClicked,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                // More specific loading check: are we loading AND is this specific action the one loading?
                // For simplicity, if any uiState.isLoading is true, show indicator. Refine if needed.
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Sign Out")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onDeleteAccountClicked,
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                 if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Delete Account")
                }
            }
            if (uiState.requiresReAuthentication) {
                Text(
                    "Deletion failed. Please sign out and sign back in to re-authenticate, then try deleting again.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

        } else {
            // Signed-Out UI
            Button(
                onClick = onSignInClicked,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) { // General loading for sign-in attempt
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Sign In with Google")
                }
            }
        }

        if (uiState.error != null && !uiState.isLoading && !uiState.requiresReAuthentication) { // Display general error only if not loading and not a re-auth message
            Text(
                text = uiState.error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp).fillMaxWidth()
            )
        }
    }
}


@Composable
fun SettingsSectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant, // Or a color that fits your theme for section headers
        modifier = Modifier.padding(vertical = 12.dp)
    )
}

@Composable
fun SettingsItem(
    icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant // Adjust as per your theme for subtitles
                    )
                }
            }
        }
    }
    // Add a divider after each item if needed, but not explicitly shown in the image except for implicit separation
    // Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), thickness = 0.5.dp)
}

@Composable
fun VersionInfo(version: String) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = version,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant // Adjust as per your theme
        )
    }
}