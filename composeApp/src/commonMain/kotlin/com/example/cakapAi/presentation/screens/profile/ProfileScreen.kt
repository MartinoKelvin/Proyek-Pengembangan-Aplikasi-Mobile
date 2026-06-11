package com.example.cakapAi.presentation.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Premium learning profile screen with dynamic support for Light and Dark Theme modes.
 */
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val userName = state.userName
    val email = state.email
    val currentLevel = state.currentLevelTitle
    val completedLevel = state.completedLevelCount
    val totalLevel = state.totalLevelsCount

    var showEditDialog by remember { mutableStateOf(false) }
    var editName by remember(userName) { mutableStateOf(userName) }
    var editEmail by remember(email) { mutableStateOf(email) }

    val isLight = MaterialTheme.colorScheme.background.red > 0.5f
    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = if (isLight) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
    val emeraldAccent = MaterialTheme.colorScheme.primary
    val skyAccent = MaterialTheme.colorScheme.secondary

    val textPrimary = if (isLight) MaterialTheme.colorScheme.onBackground else Color.White
    val textSecondary = if (isLight) MaterialTheme.colorScheme.onSurfaceVariant else Color.LightGray
    val borderStrokeColor = if (isLight) MaterialTheme.colorScheme.outline.copy(alpha = 0.12f) else MaterialTheme.colorScheme.outline
    val gradientStart = if (isLight) MaterialTheme.colorScheme.primaryContainer else Color(0xFF012B1E)
    val progressTrack = if (isLight) MaterialTheme.colorScheme.outline.copy(alpha = 0.2f) else Color(0xFF1E293B)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("ProfileScreen")
            .background(
                Brush.verticalGradient(
                    colors = listOf(gradientStart, backgroundColor)
                )
            )
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = textPrimary
                    )
                }
                Text(
                    text = "Profil Pengguna",
                    color = textPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                IconButton(
                    onClick = onNavigateToSettings,
                    modifier = Modifier.testTag("SettingsButton")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = textPrimary
                    )
                }
            }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar with premium dual-color glowing ring and initials
                val initials = if (userName.isNotBlank()) {
                    userName.trim().split("\\s+".toRegex())
                        .take(2)
                        .map { it.first().uppercaseChar() }
                        .joinToString("")
                } else {
                    "U"
                }

                Box(
                    modifier = Modifier
                        .size(108.dp)
                        .border(
                            BorderStroke(
                                3.dp,
                                Brush.linearGradient(listOf(emeraldAccent, skyAccent))
                            ),
                            CircleShape
                        )
                        .padding(5.dp)
                        .clip(CircleShape)
                        .background(emeraldAccent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = emeraldAccent,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Name and Email
                Text(
                    text = userName,
                    color = textPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = email,
                    color = textSecondary,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showEditDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = emeraldAccent.copy(alpha = 0.1f), contentColor = emeraldAccent),
                    border = BorderStroke(1.dp, emeraldAccent.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profil",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Edit Profil",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // modern Grid-based Learning Stats Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Statistik Belajar",
                        color = textPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Level Card (Full Width)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, borderStrokeColor)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(skyAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Star, contentDescription = "Level", tint = skyAccent, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Level Saat Ini", color = textSecondary, fontSize = 11.sp)
                        Text(text = currentLevel, color = textPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Progress Card (Full width at bottom)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, borderStrokeColor)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(emeraldAccent.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Progress", tint = emeraldAccent, modifier = Modifier.size(20.dp))
                            }
                            Column {
                                Text(text = "Progress Level", color = textSecondary, fontSize = 11.sp)
                                Text(text = "Tahap Belajar", color = textPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            LinearProgressIndicator(
                                progress = { completedLevel.toFloat() / totalLevel },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = emeraldAccent,
                                trackColor = progressTrack
                            )
                            Text(
                                text = "$completedLevel/$totalLevel",
                                color = textPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        if (showEditDialog) {
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = {
                    Text(
                        text = "Edit Profil",
                        color = textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Nama Lengkap") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = textPrimary,
                                unfocusedTextColor = textPrimary,
                                focusedLabelColor = emeraldAccent,
                                unfocusedLabelColor = textSecondary,
                                focusedBorderColor = emeraldAccent,
                                unfocusedBorderColor = borderStrokeColor
                            )
                        )

                        OutlinedTextField(
                            value = editEmail,
                            onValueChange = { editEmail = it },
                            label = { Text("Alamat Email") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = textPrimary,
                                unfocusedTextColor = textPrimary,
                                focusedLabelColor = emeraldAccent,
                                unfocusedLabelColor = textSecondary,
                                focusedBorderColor = emeraldAccent,
                                unfocusedBorderColor = borderStrokeColor
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (editName.isNotBlank() && editEmail.isNotBlank()) {
                                viewModel.updateProfile(editName.trim(), editEmail.trim())
                                showEditDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = emeraldAccent),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Simpan", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showEditDialog = false
                            editName = userName
                            editEmail = email
                        }
                    ) {
                        Text("Batal", color = textSecondary)
                    }
                },
                containerColor = if (isLight) MaterialTheme.colorScheme.surface else Color(0xFF0F1A30),
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}
