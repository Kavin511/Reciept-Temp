package com.devstudio.receipto.ui.screens

/**
 * @Author: Kavin
 * @Date: 07/06/25
 */

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import com.devstudio.receipto.core.rememberCameraLauncher
import com.devstudio.receipto.core.rememberImagePickerLauncher
// import com.devstudio.receipto.platform.ByteArrayParcelable // Will need to create this expect/actual // Removed
import com.devstudio.receipto.platform.platformByteArrayToImageBitmap // Corrected import
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.devstudio.receipto.Receipt
import com.devstudio.receipto.ReceiptViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReceiptScreen(
    navController: NavController,
    viewModel: ReceiptViewModel,
    receiptId: String?
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(receiptId) {
        viewModel.loadReceipt(receiptId)
    }

    val currentReceipt by viewModel.currentReceipt.collectAsState()
    var localReceipt by remember { mutableStateOf(currentReceipt ?: Receipt()) }

    LaunchedEffect(currentReceipt) {
        currentReceipt?.let {
            // Preserve newImageByteArray if it was set while currentReceipt was null (new receipt)
            val newBytes = localReceipt.newImageByteArray
            localReceipt = it.copy().apply {
                if (newBytes != null && it.imageUrl.isEmpty()) { // Only keep new bytes if no imageUrl yet from loaded receipt
                    newImageByteArray = newBytes
                }
            }
        }
    }

    val imagePickerLauncher = rememberImagePickerLauncher { result ->
        if (result.byteArray != null) {
            localReceipt = localReceipt.copy(newImageByteArray = result.byteArray, imageUrl = null) // Clear old URL if new image is picked
        } else if (result.error != null) {
            println("Image pick error: ${result.error}")
            // TODO: Show error message to user (e.g., via a Snackbar or Toast)
        }
    }

    val cameraLauncher = rememberCameraLauncher { result ->
        if (result.byteArray != null) {
            localReceipt = localReceipt.copy(newImageByteArray = result.byteArray, imageUrl = null)
        } else if (result.error != null) {
            println("Camera capture error: ${result.error}")
            // TODO: Show error message to user
        }
    }

    val openDatePickerDialog = remember { mutableStateOf(false) }
    val openReminderDatePickerDialog = remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearMessage()
        }
    }

    // Use localReceipt for UI, but ensure currentReceipt from ViewModel is observed for initial load
    // and potential external updates if any (though direct modification is now via localReceipt).
    // This ensures that when currentReceipt is loaded/updated from the ViewModel, localReceipt reflects that.
    val receiptToDisplay = currentReceipt // Still needed to trigger recomposition when ViewModel's currentReceipt changes.

    Box(modifier = Modifier.fillMaxSize()) { // Added Box to overlay CircularProgressIndicator
        receiptToDisplay?.let { receipt -> // Now, operate on localReceipt for modifications
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Edit Receipt",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF2C2C2C)
                    ),
                    scrollBehavior = scrollBehavior
                )
            },
            containerColor = Color(0xFF1A1A1A)
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Name
                Text(
                    text = "Name",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                TextField(
                    value = localReceipt.name,
                    onValueChange = { newName -> localReceipt = localReceipt.copy(name = newName) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF3A3A3A),
                        unfocusedContainerColor = Color(0xFF3A3A3A),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Amount
                Text(
                    text = "Amount",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                TextField(
                    value = if (localReceipt.amount == 0.0) "" else localReceipt.amount.toString(),
                    onValueChange = {
                        val amount = it.toDoubleOrNull() ?: 0.0
                        localReceipt = localReceipt.copy(amount = amount)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF3A3A3A),
                        unfocusedContainerColor = Color(0xFF3A3A3A),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Date
                Text(
                    text = "Date",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                TextField(
                    value = localReceipt.date,
                    onValueChange = { /* Date is selected via dialog */ },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { openDatePickerDialog.value = true },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Pick Date",
                            tint = Color.Gray,
                            modifier = Modifier.clickable { openDatePickerDialog.value = true }
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF3A3A3A),
                        unfocusedContainerColor = Color(0xFF3A3A3A),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Reminder Date
                Text(
                    text = "Reminder Date",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                TextField(
                    value = localReceipt.reminderDate ?: "",
                    onValueChange = { /* Reminder Date is selected via dialog */ },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { openReminderDatePickerDialog.value = true },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Pick Reminder Date",
                            tint = Color.Gray,
                            modifier = Modifier.clickable { openReminderDatePickerDialog.value = true }
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF3A3A3A),
                        unfocusedContainerColor = Color(0xFF3A3A3A),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Reason
                Text(
                    text = "Reason",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                TextField(
                    value = localReceipt.reason,
                    onValueChange = { newReason -> localReceipt = localReceipt.copy(reason = newReason) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF3A3A3A),
                        unfocusedContainerColor = Color(0xFF3A3A3A),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))

                // Attachment Section
                Text(
                    text = "Attachment",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                val hasExistingImage = !localReceipt.imageUrl.isNullOrBlank()
                val hasNewImage = localReceipt.newImageByteArray != null

                if (hasNewImage || hasExistingImage) {
                    AttachmentPreview(
                        imageByteArray = localReceipt.newImageByteArray,
                        imageUrl = localReceipt.imageUrl,
                        onRemoveImage = {
                            localReceipt = localReceipt.copy(imageUrl = null, newImageByteArray = null)
                        },
                        onChangeImage = {
                            imagePickerLauncher() // Simple re-trigger gallery pick
                        }
                    )
                } else {
                    AttachmentInput(
                        onPickImage = { imagePickerLauncher() },
                        onTakePhoto = { cameraLauncher() }
                    )
                }
                Spacer(modifier = Modifier.height(48.dp))

                // Bottom Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.deleteReceipt(localReceipt.id) {
                                navController.popBackStack()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4A4A4A),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(
                            text = "Delete",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Button(
                        onClick = {
                            val currentImageBytes = localReceipt.newImageByteArray
                            if (currentImageBytes != null) {
                                viewModel.uploadImage(currentImageBytes) { uploadedUrl ->
                                    localReceipt = localReceipt.copy(imageUrl = uploadedUrl, newImageByteArray = null)
                                    saveReceipt(viewModel, localReceipt, receiptId, navController)
                                }
                            } else {
                                saveReceipt(viewModel, localReceipt, receiptId, navController)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(
                            text = "Save",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)) // Semi-transparent background
                    .clickable(enabled = false, onClick = {}), // Block interactions
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }


    if (openDatePickerDialog.value) {
        // DatePickerDialog implementation
    }

    if (openReminderDatePickerDialog.value) {
        // DatePickerDialog implementation
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

fun saveReceipt(
    viewModel: ReceiptViewModel,
    receipt: Receipt,
    receiptId: String?,
    navController: NavController
) {
    if (receiptId == null) {
        viewModel.addReceipt(receipt) { navController.popBackStack() }
    } else {
        viewModel.updateReceipt(receipt) { navController.popBackStack() }
    }
}

@Composable
fun AttachmentPreview(
    imageByteArray: ByteArray?, // New: to hold byte array for preview
    imageUrl: String?,      // Existing: for URL-based images
    onRemoveImage: () -> Unit,
    onChangeImage: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        if (imageByteArray != null) {
            val bitmap = remember(imageByteArray) { platformByteArrayToImageBitmap(imageByteArray) }
            Image(
                bitmap = bitmap,
                contentDescription = "Newly selected receipt attachment",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Receipt Attachment Preview",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                // Optional: Add placeholder, error image
                // placeholder = painterResource(R.drawable.placeholder),
                // error = painterResource(R.drawable.error_image)
            )
        } else {
            // Optional: Display a placeholder if neither is available
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Image, "No image", modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        // Overlay for change/remove buttons
        Row(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)).padding(8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onChangeImage) {
                Text(
                    "Change",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Medium
                ) // Assuming scrim is dark, text is light
            }
            Divider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.height(20.dp).width(1.dp)
            )
            TextButton(onClick = onRemoveImage) {
                Text(
                    "Remove",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
