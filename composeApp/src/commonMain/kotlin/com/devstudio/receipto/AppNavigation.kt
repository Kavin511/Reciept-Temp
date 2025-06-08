package com.devstudio.receipto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// import androidx.lifecycle.viewmodel.compose.viewModel // Replaced with KMP ViewModel factory
import com.devstudio.receipto.presentation.receipt.rememberReceiptViewModel // KMP ViewModel factory
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.savedstate.read
import com.devstudio.receipto.Routes.RECEIPTS_LIST
import com.devstudio.receipto.Routes.SETTINGS
// import cafe.adriel.voyager.navigator.Navigator // Removed Voyager import
// import cafe.adriel.voyager.transitions.SlideTransition // Removed Voyager import
import com.devstudio.receipto.navigation.AppDestinations // Import new AppDestinations
import com.devstudio.receipto.ui.SettingsScreen // SettingsScreen will be directly called again
import com.devstudio.receipto.ui.screens.CategoriesScreen
import com.devstudio.receipto.ui.screens.EditReceiptScreen
import com.devstudio.receipto.ui.screens.ReceiptsScreen

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        NavigationBarItem(
            selected = currentRoute == AppDestinations.RECEIPTS_LIST_ROUTE, // Use new constant
            onClick = { navController.navigate(AppDestinations.RECEIPTS_LIST_ROUTE) }, // Use new constant
            icon = {
                Icon(
                    imageVector = Icons.Default.MailOutline,
                    contentDescription = "Receipts"
                )
            },
            label = { Text("Receipts", fontSize = 12.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            )
        )
        NavigationBarItem(
            selected = currentRoute == AppDestinations.SETTINGS_ROUTE, // Use new constant
            onClick = { navController.navigate(AppDestinations.SETTINGS_ROUTE) }, // Use new constant
            icon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            },
            label = { Text("Settings", fontSize = 12.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            )
        )
    }
}

object Routes {
    const val RECEIPTS_LIST = "receipts_list"
    const val EDIT_RECEIPT = "edit_receipt"
    const val EDIT_RECEIPT_WITH_ID = "edit_receipt/{receiptId}" // Defined in AppDestinations
    const val ADD_RECEIPT = "add_receipt" // Defined in AppDestinations
    const val SETTINGS = "setting" // Defined in AppDestinations
}
// Removed old Routes object

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: ReceiptViewModel = rememberReceiptViewModel() // Use KMP ViewModel factory
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    uiState.message?.let { message ->
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessage()
        }
    }
    uiState.error?.let { error ->
        AlertDialog(
            onDismissRequest = { viewModel.clearMessage() },
            title = { Text("Error", color = MaterialTheme.colorScheme.onErrorContainer) },
            text = { Text(error, color = MaterialTheme.colorScheme.onErrorContainer) },
            confirmButton = {
                Button(onClick = { viewModel.clearMessage() }) {
                    Text("OK")
                }
            },
            containerColor = MaterialTheme.colorScheme.errorContainer,
            textContentColor = MaterialTheme.colorScheme.onErrorContainer,
            titleContentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val topBarTitle = when (currentRoute) {
        RECEIPTS_LIST -> "Receipts"
        SETTINGS -> "Settings"
        Routes.ADD_RECEIPT -> "Add Receipt"
        Routes.EDIT_RECEIPT_WITH_ID -> "Edit Receipt"
        else -> ""
    }

    val showAppBar = currentRoute in listOf(
        RECEIPTS_LIST,
        SETTINGS,
        Routes.ADD_RECEIPT,
        Routes.EDIT_RECEIPT_WITH_ID
    )

    Scaffold(
        topBar = {
            if (showAppBar) {
                TopAppBar(
                    title = { Text(topBarTitle) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    navigationIcon = {
                        if (currentRoute != RECEIPTS_LIST) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (currentRoute == AppDestinations.RECEIPTS_LIST_ROUTE || currentRoute == AppDestinations.SETTINGS_ROUTE) { // Use new constants
                BottomNavigationBar(navController)
            }
        }, floatingActionButton = {
            if (currentRoute == AppDestinations.RECEIPTS_LIST_ROUTE) { // Use new constant
                FloatingActionButton(
                    onClick = { navController.navigate(AppDestinations.ADD_RECEIPT_ROUTE) }, // Use new constant
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                    modifier = Modifier.size(60.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Receipt",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }, floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = AppDestinations.RECEIPTS_LIST_ROUTE, // Use new constant
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(AppDestinations.RECEIPTS_LIST_ROUTE) { // Use new constant
                ReceiptsScreen(navController = navController, viewModel = viewModel)
            }
            composable(AppDestinations.ADD_RECEIPT_ROUTE) { // Use new constant
                // It's a new receipt, so pass null for receiptId
                EditReceiptScreen(
                    navController = navController,
                    viewModel = viewModel,
                    receiptId = null
                )
            }
            composable(AppDestinations.EDIT_RECEIPT_WITH_ID_ROUTE) { backStackEntry -> // Use new constant
                val receiptId = backStackEntry.arguments?.read {
                    getString(AppDestinations.EDIT_RECEIPT_WITH_ID_ARG) // Use new constant
                }
                EditReceiptScreen(
                    navController = navController,
                    viewModel = viewModel,
                    receiptId = receiptId
                )
            }
            composable(AppDestinations.SETTINGS_ROUTE) { // Use new constant
                SettingsScreen(navController = navController) // Pass NavController
            }
            composable(AppDestinations.CATEGORIES_ROUTE) { // Add CategoriesScreen destination
                CategoriesScreen(navController = navController)
            }
        }
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
