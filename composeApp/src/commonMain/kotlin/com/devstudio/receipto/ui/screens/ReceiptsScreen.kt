package com.devstudio.receipto.ui.screens

/**
 * @Author: Kavin
 * @Date: 07/06/25
 */

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.devstudio.receipto.Receipt
import com.devstudio.receipto.ReceiptViewModel
import com.devstudio.receipto.Routes
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptsScreen(
    navController: NavController, viewModel: ReceiptViewModel
) {
    val receipts by viewModel.receipts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val showEmptyState = searchQuery.isNotBlank() && receipts.isEmpty()

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        SearchBarComponent(
            searchQuery = searchQuery,
            onSearchQueryChange = viewModel::updateSearchQuery,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (showEmptyState) {
            NoResultsFoundSection()
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(receipts, key = { it.id }) { receipt ->
                    ReceiptItem(
                        receipt = receipt,
                        onClick = { navController.navigate("${Routes.EDIT_RECEIPT}/${receipt.id}") })
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}
@Composable
fun ReceiptItem(receipt: Receipt, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick) // Make item clickable for navigation
            .padding(16.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)),
            color = MaterialTheme.colorScheme.outline // Or surfaceContainerHighest if more appropriate
        ) {
            Icon(
                imageVector = Icons.Default.MailOutline,
                contentDescription = "Receipt Icon",
                tint = MaterialTheme.colorScheme.onSurfaceVariant, // Icon on a darker surface variant
                modifier = Modifier.padding(8.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = receipt.name,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp
            )
            Text(
                text = "$%.2f".format(receipt.amount), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp
            )
            Text(
                text = receipt.date, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarComponent(
    searchQuery: String, onSearchQueryChange: (String) -> Unit, modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(8.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 0.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            BasicTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                textStyle = LocalTextStyle.current.copy(
                    color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp
                ),
                singleLine = true,
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Search receipts", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 16.sp
                        )
                    }
                    innerTextField()
                })
        }
    }
}

@Composable
fun NoResultsFoundSection() {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No results found",
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Try adjusting your search or adding new receipts.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { /* Handle Add Receipt button click */ },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(0.6f).height(50.dp)
        ) {
            Text(
                text = "Add Receipt",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
