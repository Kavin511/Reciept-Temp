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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp


// Main Composable for the Settings Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClicked: () -> Unit = {},
    onAccountClicked: () -> Unit = {},
    onAppearanceClicked: () -> Unit = {},
    onNotificationsClicked: () -> Unit = {},
    onCurrencyClicked: () -> Unit = {},
    onCategoriesClicked: () -> Unit = {},
    onStorageClicked: () -> Unit = {},
    onHelpCenterClicked: () -> Unit = {},
    onContactUsClicked: () -> Unit = {},
    onTermsOfServiceClicked: () -> Unit = {},
    onPrivacyPolicyClicked: () -> Unit = {},
) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Settings") },
            navigationIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.padding(horizontal = 16.dp).clickable { onBackClicked() })
            },
        )
    }) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionHeader("General")
                SettingsItem(
                    icon = Icons.Default.AccountCircle,
                    title = "Account",
                    subtitle = "Manage your account details",
                    onClick = onAccountClicked
                )
                SettingsItem(
                    icon = Icons.Default.ColorLens,
                    title = "Appearance",
                    subtitle = "Customize app appearance",
                    onClick = onAppearanceClicked
                )
                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    subtitle = "Configure notification preferences",
                    onClick = onNotificationsClicked
                )
                Spacer(modifier = Modifier.height(24.dp))
                SettingsSectionHeader("Receipt Management")
                SettingsItem(
                    icon = Icons.Default.AttachMoney,
                    title = "Currency",
                    subtitle = "Set default currency for receipts",
                    onClick = onCurrencyClicked
                )
                SettingsItem(
                    icon = Icons.Default.Category,
                    title = "Categories",
                    subtitle = "Configure receipt categorization",
                    onClick = onCategoriesClicked
                )
                SettingsItem(
                    icon = Icons.Default.Cloud,
                    title = "Storage",
                    subtitle = "Manage receipt storage options",
                    onClick = onStorageClicked
                )
                Spacer(modifier = Modifier.height(24.dp))
                SettingsSectionHeader("Support")
                SettingsItem(
                    icon = Icons.Default.HelpOutline,
                    title = "Help Center",
                    subtitle = "", // No subtitle in image
                    onClick = onHelpCenterClicked
                )
                SettingsItem(
                    icon = Icons.Default.MailOutline,
                    title = "Contact Us",
                    subtitle = "", // No subtitle in image
                    onClick = onContactUsClicked
                )
                SettingsItem(
                    icon = Icons.Default.Description,
                    title = "Terms of Service",
                    subtitle = "", // No subtitle in image
                    onClick = onTermsOfServiceClicked
                )
                SettingsItem(
                    icon = Icons.Default.Policy,
                    title = "Privacy Policy",
                    subtitle = "", // No subtitle in image
                    onClick = onPrivacyPolicyClicked
                )
                Spacer(modifier = Modifier.height(24.dp))
                VersionInfo("Version 1.2.3")
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = Color.Gray, // Or a color that fits your theme for section headers
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
                        color = Color.Gray // Adjust as per your theme for subtitles
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
            color = Color.Gray // Adjust as per your theme
        )
    }
}