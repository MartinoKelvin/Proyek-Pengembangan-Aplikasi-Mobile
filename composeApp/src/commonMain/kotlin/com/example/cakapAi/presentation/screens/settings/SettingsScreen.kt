package com.example.cakapAi.presentation.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel

/**
 * Settings Screen utilizing dynamic theme schemes and reactive ViewModel state logic
 * for customizing sound, notifications, and Light/Dark Theme Modes.
 */
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var notificationEnabled by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pengaturan Aplikasi",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            // Screen Body Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "Atur preferensi belajar kamu di CakapAI.",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )

                // First Card: Controls preferences
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. Notification Toggle Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Notifikasi",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Aktifkan pengingat belajar",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            }
                            Switch(
                                checked = notificationEnabled,
                                onCheckedChange = { notificationEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))

                        // 2. Sound Toggle Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Suara",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Aktifkan efek suara saat belajar",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            }
                            Switch(
                                checked = soundEnabled,
                                onCheckedChange = { soundEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))

                        // 3. Theme Mode Selection Column
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Tema Tampilan",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Gunakan tema terang atau gelap pilihanmu",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            when (val state = uiState) {
                                is SettingsUiState.Loading -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = MaterialTheme.colorScheme.primary,
                                            strokeWidth = 2.dp
                                        )
                                    }
                                }
                                is SettingsUiState.Success -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        // Light Mode Option Card
                                        ThemeOptionButton(
                                            label = "Light Mode",
                                            isSelected = !state.isDarkMode,
                                            onClick = { viewModel.setDarkMode(false) },
                                            modifier = Modifier.weight(1f)
                                        )
                                        // Dark Mode Option Card
                                        ThemeOptionButton(
                                            label = "Dark Mode",
                                            isSelected = state.isDarkMode,
                                            onClick = { viewModel.setDarkMode(true) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                                is SettingsUiState.Error -> {
                                    Text(
                                        text = "Gagal memuat preferensi tema",
                                        color = MaterialTheme.colorScheme.error,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Second Card: App Version Row
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Versi Aplikasi",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "CakapAI v1.0.0",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Reusable premium select option button for dynamic theme schemes.
 */
@Composable
fun ThemeOptionButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    }

    Surface(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        contentColor = contentColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}
