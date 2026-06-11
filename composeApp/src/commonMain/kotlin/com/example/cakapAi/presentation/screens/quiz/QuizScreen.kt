package com.example.cakapAi.presentation.screens.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cakapAi.presentation.screens.map.LevelStatus
import com.example.cakapAi.presentation.screens.map.LevelType
import com.example.cakapAi.presentation.screens.map.PathLevel
import com.example.cakapAi.presentation.screens.practice.PracticeSessionOverlay
import com.example.cakapAi.core.util.BackHandler
import kotlin.random.Random

@Composable
fun QuizScreen(
    levelId: Int,
    onNavigateBack: () -> Unit,
    onFinishQuiz: (levelId: Int, score: Int, totalQuestion: Int, accuracy: Int, isPassed: Boolean) -> Unit
) {
    var isPracticeSessionOpen by remember { mutableStateOf(false) }
    var randomPracticeLevel by remember { mutableStateOf<PathLevel?>(null) }
    var showExitConfirmDialog by remember { mutableStateOf(false) }

    val isLight = MaterialTheme.colorScheme.background.red > 0.5f
    val backgroundColor = MaterialTheme.colorScheme.background
    val emeraldAccent = MaterialTheme.colorScheme.primary
    val gradientStart = if (isLight) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color(0xFF012B1E)
    val cardColor = MaterialTheme.colorScheme.surface
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("QuizScreen")
            .background(
                Brush.verticalGradient(
                    colors = listOf(gradientStart, backgroundColor)
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "AI Practice",
                tint = emeraldAccent,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Kuis Latihan Umum",
                color = if (isLight) Color.Black else Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Tingkatkan kemampuan bahasamu dengan soal-soal latihan acak pilihan dari bank soal CakapAI.",
                color = if (isLight) Color.DarkGray else Color.LightGray,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(48.dp))
            
            Button(
                onClick = {
                    val topics = listOf("LISTENING", "SPEAKING", "READING", "GAMING", "REWARD", "VIDEO")
                    val randomTopic = topics.random()
                    randomPracticeLevel = PathLevel(
                        id = Random.nextInt(100, 1000),
                        index = 0,
                        type = try { LevelType.valueOf(randomTopic) } catch (e: Exception) { LevelType.LISTENING },
                        title = "Topik Random",
                        subtitle = "Latihan Tambahan AI",
                        status = LevelStatus.UNLOCKED,
                        xOffsetFactor = 0f
                    )
                    isPracticeSessionOpen = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("QuizButton"),
                colors = ButtonDefaults.buttonColors(containerColor = emeraldAccent),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Mulai Kuis Acak",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        BackHandler(enabled = isPracticeSessionOpen) {
            showExitConfirmDialog = true
        }

        if (isPracticeSessionOpen && randomPracticeLevel != null) {
            PracticeSessionOverlay(
                level = randomPracticeLevel!!,
                closeButtonText = "Lihat Hasil",
                onClose = {
                    showExitConfirmDialog = true
                },
                onCompleted = { isSuccess, score ->
                    val completedLevelId = randomPracticeLevel?.id ?: 999
                    isPracticeSessionOpen = false
                    randomPracticeLevel = null
                    val accuracy = if (isSuccess) 100 else 50
                    onFinishQuiz(completedLevelId, score, 5, accuracy, isSuccess)
                }
            )
        }

        if (showExitConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showExitConfirmDialog = false },
                containerColor = cardColor,
                titleContentColor = textPrimary,
                textContentColor = textPrimary,
                title = { Text(text = "Keluar dari kuis?", fontWeight = FontWeight.Bold) },
                text = { Text(text = "Progress pengerjaan saat ini akan hilang. Apakah kamu yakin ingin keluar?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showExitConfirmDialog = false
                            isPracticeSessionOpen = false
                            randomPracticeLevel = null
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = emeraldAccent),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Keluar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showExitConfirmDialog = false }
                    ) {
                        Text(
                            text = "Batal",
                            color = textSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            )
        }
    }
}
