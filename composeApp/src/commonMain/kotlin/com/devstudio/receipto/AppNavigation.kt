package com.devstudio.receipto


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReceiptScree1n(
    navController: NavController, viewModel: ReceiptViewModel
) {
    val currentReceipt by viewModel.currentReceipt.collectAsState()

    // Date Picker State
    val openDatePickerDialog = remember { mutableStateOf(false) }

    currentReceipt?.let { receipt ->
        Column(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        ) {
            // Top Bar
            TopAppBar(
                title = {
                Text(
                    text = if (receipt.id.isEmpty()) "Add New Receipt" else "Edit Receipt",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.Close, // Using Close icon for 'X'
                        contentDescription = "Close", tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }, actions = {
                TextButton(onClick = {
                    viewModel.addReceipt(Receipt(), { navController.popBackStack() })
                }) {
                    Text("Save", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
            )

            // Content
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp)
                    .verticalScroll(rememberScrollState()), // Enable scrolling for content
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Receipt Name
                val textFieldColors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    cursorColor = MaterialTheme.colorScheme.onBackground,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = receipt.name,
                    onValueChange = { viewModel.updateReceipt(receipt.copy(name = it)) },
                    label = { Text("Receipt Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Amount
                OutlinedTextField(
                    value = if (receipt.amount == 0.0) "" else receipt.amount.toString(),
                    onValueChange = {
                        val amount = it.toDoubleOrNull() ?: 0.0
                        viewModel.updateReceipt(receipt.copy(amount = amount))
                    },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Date
                OutlinedTextField(
                    value = receipt.date,
                    onValueChange = { /* Read-only, handled by date picker */ },
                    label = { Text("Date") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                        .clickable { openDatePickerDialog.value = true },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Pick Date",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.clickable { openDatePickerDialog.value = true })
                    },
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Reason
                OutlinedTextField(
                    value = receipt.reason,
                    onValueChange = { viewModel.updateReceipt(receipt.copy(reason = it)) },
                    label = { Text("Reason") },
                    modifier = Modifier.fillMaxWidth()
                        .heightIn(min = 100.dp), // Allow multiline input
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Attachment Section (Conditional Display)
                if (receipt.imageUrl.isEmpty()) {
                    // State 1: No attachment
                    AttachmentInput(onPickImage = {
//                            pickImageLauncher.launch("image/*")
                    }, onTakePhoto = {
//                            takePhotoLauncher.launch(null)
                    })
                } else {
                    // State 2: Attachment preview
                    AttachmentPreview(imageUrl = receipt.imageUrl, onRemoveImage = {
                        viewModel.updateReceipt(receipt.copy(imageUrl = ""))
                    }, onChangeImage = {
//                        pickImageLauncher.launch("image/*")
                    })
                }
            }
        }
    }

    if (openDatePickerDialog.value) {
        // DatePickerDialog related code was here
    }
}

@Composable
fun AttachmentInput(
    onPickImage: () -> Unit, onTakePhoto: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().height(180.dp) // Fixed height for consistency
            .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { /* This makes the whole area clickable, but buttons are better for specific actions */ },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Image,
            contentDescription = "Add Attachment",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Add Attachment",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onPickImage,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Upload Photo", color = MaterialTheme.colorScheme.onPrimary)
            }
            Button(
                onClick = onTakePhoto,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Take Photo", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
fun AttachmentPreview(
    imageUrl: String, onRemoveImage: () -> Unit, onChangeImage: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Image(
            bitmap = ImageBitmap(1, 1), // Placeholder, replace with actual image loading
            contentDescription = "Receipt Attachment Preview",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Overlay for change/remove buttons
        Row(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)).padding(8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onChangeImage) {
                Text("Change", color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Medium) // Assuming scrim is dark, text is light
            }
            Divider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.height(20.dp).width(1.dp)
            )
            TextButton(onClick = onRemoveImage) {
                Text("Remove", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface, // Or background, depending on desired elevation effect
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
                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) // Subtle indicator
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
    const val EDIT_RECEIPT = "edit_receipt" // Base route
    const val EDIT_RECEIPT_WITH_ID = "edit_receipt/{receiptId}" // For existing
    const val ADD_RECEIPT = "add_receipt" // For new
    const val SETTINGS = "setting" // For new
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: ReceiptViewModel = viewModel()

    // Observe UI state for general app messages/errors
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Display messages/errors via Snackbar or AlertDialog
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
        // Floating action button and Bottom bar are now managed by individual screens
        // or can be lifted up here if they are always visible regardless of screen.
        // For this design, FAB and BottomNav are only on ReceiptsScreen.
        bottomBar = {
            // Only show bottom navigation on ReceiptsScreen
            val currentRoute =
                navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute == Routes.RECEIPTS_LIST || currentRoute == SETTINGS) {
                BottomNavigationBar(navController)
            }
        }, floatingActionButton = {
            // Only show FAB on ReceiptsScreen
            val currentRoute =
                navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute == Routes.RECEIPTS_LIST) {
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
            startDestination = Routes.RECEIPTS_LIST,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Routes.RECEIPTS_LIST) {
                ReceiptsScreen(navController = navController, viewModel = viewModel)
            }
            composable(Routes.ADD_RECEIPT) {
                // Initialize new receipt
                LaunchedEffect(Unit) {
                    viewModel.loadReceipt(null)
                }
                EditReceiptScreen(navController = navController, viewModel = viewModel)
            }
            composable(Routes.EDIT_RECEIPT_WITH_ID) { backStackEntry ->
                val receiptId = "0"//backStackEntry.arguments("receiptId")
                // Load existing receipt
                LaunchedEffect(receiptId) {
                    viewModel.loadReceipt(receiptId)
                }
                EditReceiptScreen(navController = navController, viewModel = viewModel)
            }
            composable(SETTINGS) {
                SettingsScreen()
            }
        }

        // Show global loading indicator from ViewModel
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
