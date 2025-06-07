package com.devstudio.receipto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.MaterialTheme
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.devstudio.receipto.Routes.RECEIPTS_LIST
import com.devstudio.receipto.Routes.SETTINGS
import com.devstudio.receipto.ui.SettingsScreen
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
            selected = currentRoute == RECEIPTS_LIST,
            onClick = { navController.navigate(RECEIPTS_LIST) },
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
            selected = currentRoute == SETTINGS,
            onClick = { navController.navigate(SETTINGS) },
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
    const val EDIT_RECEIPT_WITH_ID = "edit_receipt/{receiptId}"
    const val ADD_RECEIPT = "add_receipt"
    const val SETTINGS = "setting"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: ReceiptViewModel = viewModel()
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
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            val currentRoute =
                navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute == RECEIPTS_LIST || currentRoute == SETTINGS) {
                BottomNavigationBar(navController)
            }
        }, floatingActionButton = {
            val currentRoute =
                navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute == RECEIPTS_LIST) {
                FloatingActionButton(
                    onClick = { navController.navigate(Routes.ADD_RECEIPT) },
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
            startDestination = RECEIPTS_LIST,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(RECEIPTS_LIST) {
                ReceiptsScreen(navController = navController, viewModel = viewModel)
            }
            composable(Routes.ADD_RECEIPT) {
                // It's a new receipt, so pass null for receiptId
                EditReceiptScreen(navController = navController, viewModel = viewModel, receiptId = null)
            }
            composable(Routes.EDIT_RECEIPT_WITH_ID) { backStackEntry ->
                val receiptId = backStackEntry.arguments?.getString("receiptId")
                // viewModel.loadReceipt is called from EditReceiptScreen's LaunchedEffect
                EditReceiptScreen(navController = navController, viewModel = viewModel, receiptId = receiptId)
            }
            composable(SETTINGS) {
                SettingsScreen()
            }
        }
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
