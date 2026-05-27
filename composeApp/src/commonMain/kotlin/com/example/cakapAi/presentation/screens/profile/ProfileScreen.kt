package com.example.cakapAi.presentation.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Premium learning profile screen with dynamic support for Light and Dark Theme modes.
 */
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val userName = "Martino Kelvin"
    val email = "martinokelvin06032005@gmail.com"
    val currentLevel = "Level 2 - Explorer"
    val completedLevel = 2
    val totalLevel = 5
    val xp = 250

    val isLight = MaterialTheme.colorScheme.background.red > 0.5f
    val backgroundColor = if (isLight) Color(0xFFF0F4F8) else Color(0xFF071224)
    val cardColor = if (isLight) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else Color(0xFF0F1A30)
    val emeraldAccent = MaterialTheme.colorScheme.primary
    val skyAccent = MaterialTheme.colorScheme.secondary

    val textPrimary = if (isLight) MaterialTheme.colorScheme.onBackground else Color.White
    val textSecondary = if (isLight) MaterialTheme.colorScheme.onSurfaceVariant else Color.LightGray
    val borderStrokeColor = if (isLight) MaterialTheme.colorScheme.outline.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.1f)
    val gradientStart = if (isLight) MaterialTheme.colorScheme.primaryContainer else Color(0xFF0A1B35)
    val progressTrack = if (isLight) MaterialTheme.colorScheme.outline.copy(alpha = 0.2f) else Color(0xFF1E293B)

    Box(
        modifier = Modifier
            .fillMaxSize()
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
                IconButton(onClick = onNavigateToSettings) {
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
                // Avatar
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(emeraldAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        tint = Color.White,
                        modifier = Modifier.size(60.dp)
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

                Spacer(modifier = Modifier.height(32.dp))

                // Stats Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, borderStrokeColor)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Statistik Belajar",
                            color = textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        // Level
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(skyAccent.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Star, contentDescription = "Level", tint = skyAccent)
                            }
                            Column {
                                Text(text = "Level Saat Ini", color = textSecondary, fontSize = 12.sp)
                                Text(text = currentLevel, color = textPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }

                        // Progress
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(emeraldAccent.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Progress", tint = emeraldAccent)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Progress Level", color = textSecondary, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    LinearProgressIndicator(
                                        progress = { completedLevel.toFloat() / totalLevel },
                                        modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)),
                                        color = emeraldAccent,
                                        trackColor = progressTrack
                                    )
                                    Text(text = "$completedLevel/$totalLevel", color = textPrimary, fontSize = 12.sp)
                                }
                            }
                        }

                        // XP
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF59E0B).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("XP", color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text(text = "Total XP", color = textSecondary, fontSize = 12.sp)
                                Text(text = "$xp XP", color = textPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
