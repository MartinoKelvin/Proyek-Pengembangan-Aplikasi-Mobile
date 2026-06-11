package com.example.cakapAi.presentation.screens.practice

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cakapAi.domain.model.PracticeQuestionType
import com.example.cakapAi.presentation.screens.map.PathLevel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PracticeSessionOverlay(
    level: PathLevel,
    onClose: () -> Unit,
    onCompleted: (isSuccess: Boolean, score: Int) -> Unit,
    closeButtonText: String = "Kembali ke Peta"
) {
    val sessionKey = remember(level) { "level_${level.id}_${kotlin.random.Random.nextInt()}" }
    val viewModel: PracticeViewModel = koinViewModel(
        key = sessionKey,
        parameters = { parametersOf(level.id, level.title, level.type.name) }
    )
    val state by viewModel.uiState.collectAsState()

    val isLight = MaterialTheme.colorScheme.background.red > 0.5f
    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = if (isLight) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
    val textPrimary = if (isLight) MaterialTheme.colorScheme.onBackground else Color.White
    val textSecondary = if (isLight) MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f) else Color.LightGray
    val borderOptionColor = if (isLight) MaterialTheme.colorScheme.outline.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline
    val inputBorderColor = if (isLight) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outline

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .systemBarsPadding()
    ) {
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF10B981))
            }
        } else if (state.isFinished) {
            PracticeResult(
                correctCount = state.correctCount,
                totalCount = state.questions.size,
                closeButtonText = closeButtonText,
                onClose = { 
                    val score = if (state.questions.isEmpty()) 0 else (state.correctCount * 100) / state.questions.size
                    onCompleted(state.lives > 0, score) 
                }
            )
        } else if (state.errorMessage != null && state.questions.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(state.errorMessage ?: "Failed to generate content", color = Color.Red, fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.retry() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text("Retry")
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onClose) {
                    Text("Kembali", color = Color.Gray)
                }
            }
        } else if (state.questions.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                    
                    // Progress Bar
                    val targetProgress = if (state.questions.isNotEmpty()) {
                        state.currentQuestionIndex.toFloat() / state.questions.size
                    } else 0f
                    val progressAnim by animateFloatAsState(
                        targetValue = targetProgress,
                        animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing),
                        label = "progressBar"
                    )
                    LinearProgressIndicator(
                        progress = { progressAnim },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = Color(0xFF10B981),
                        trackColor = if (isLight) MaterialTheme.colorScheme.outline.copy(alpha = 0.2f) else Color(0xFF1E293B)
                    )
                    
                    // Lives
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Favorite, contentDescription = "Lives", tint = Color.Red, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "${state.lives}", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                }

                if (state.isUsingOfflineFallback) {
                    Text(
                        text = "Menggunakan soal offline karena AI tidak tersedia",
                        color = Color(0xFFF59E0B),
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }

                // Question Content
                val question = state.currentQuestion

                LaunchedEffect(state.currentQuestionIndex) {
                    if (question != null && !state.isLoading && state.questions.isNotEmpty()) {
                        viewModel.speak(question.prompt)
                    }
                }

                if (question != null) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(24.dp)
                    ) {
                        Text(
                            text = question.instruction,
                            color = textPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = cardColor),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = question.prompt,
                                    color = textPrimary,
                                    fontSize = 18.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { viewModel.speak(question.prompt) },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color(0xFF0EA5E9).copy(alpha = 0.2f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Dengarkan Suara",
                                        tint = Color(0xFF0EA5E9)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        when (question.type) {
                            PracticeQuestionType.MULTIPLE_CHOICE -> {
                                question.options.forEach { option ->
                                    val isSelected = state.selectedAnswer == option
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                            .clickable { 
                                                viewModel.selectAnswer(option)
                                                viewModel.speak(option)
                                            }
                                            .border(
                                                width = 2.dp,
                                                color = if (isSelected) Color(0xFF0EA5E9) else borderOptionColor,
                                                shape = RoundedCornerShape(16.dp)
                                            ),
                                        colors = CardDefaults.cardColors(containerColor = cardColor)
                                    ) {
                                        Text(
                                            text = option,
                                            color = textPrimary,
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                }
                            }
                            PracticeQuestionType.FILL_BLANK -> {
                                OutlinedTextField(
                                    value = state.typedAnswer,
                                    onValueChange = { viewModel.updateTypedAnswer(it) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = textPrimary,
                                        unfocusedTextColor = textPrimary,
                                        focusedBorderColor = Color(0xFF0EA5E9),
                                        unfocusedBorderColor = inputBorderColor
                                    ),
                                    placeholder = { Text("Ketik jawaban di sini", color = textSecondary) }
                                )
                                if (question.options.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        question.options.chunked(2).forEach { rowOptions ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                rowOptions.forEach { option ->
                                                    val isSelected = state.typedAnswer == option
                                                    OutlinedButton(
                                                        onClick = { 
                                                            viewModel.updateTypedAnswer(option)
                                                            viewModel.speak(option)
                                                        },
                                                        modifier = Modifier.weight(1f),
                                                        colors = ButtonDefaults.outlinedButtonColors(
                                                            containerColor = if (isSelected) Color(0xFF0EA5E9) else Color.Transparent,
                                                            contentColor = if (isSelected) Color.White else textPrimary
                                                        ),
                                                        border = BorderStroke(1.dp, if (isSelected) Color(0xFF0EA5E9) else borderOptionColor)
                                                    ) {
                                                        Text(option, color = if (isSelected) Color.White else textPrimary)
                                                    }
                                                }
                                                if (rowOptions.size < 2) {
                                                    Spacer(modifier = Modifier.weight(1f))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            PracticeQuestionType.SPEAKING -> {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(CircleShape)
                                            .background(if (state.isListening) Color.Red else Color(0xFF0EA5E9))
                                            .clickable { viewModel.startListening() },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Mic,
                                            contentDescription = "Mic",
                                            tint = Color.White,
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    if (state.isListening) {
                                        Text("Mendengarkan...", color = textPrimary)
                                    }
                                    if (state.spokenText.isNotEmpty()) {
                                        Text("Kamu mengucapkan: \"${state.spokenText}\"", color = textSecondary)
                                    }
                                    if (state.errorMessage != null) {
                                        Text(state.errorMessage!!, color = Color.Red, fontSize = 12.sp)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedTextField(
                                            value = state.spokenText,
                                            onValueChange = { viewModel.updateSpokenTextFallback(it) },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = textPrimary,
                                                unfocusedTextColor = textPrimary,
                                                focusedBorderColor = Color(0xFF0EA5E9),
                                                unfocusedBorderColor = inputBorderColor
                                            ),
                                            placeholder = { Text("Ketik jawaban fallback di sini", color = textSecondary) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Bottom Feedback Panel
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                    ) {
                        this@Column.AnimatedVisibility(
                            visible = state.isAnswerChecked,
                            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            var lastIsCorrect by remember { mutableStateOf<Boolean?>(null) }
                            LaunchedEffect(state.isCurrentAnswerCorrect) {
                                if (state.isCurrentAnswerCorrect != null) {
                                    lastIsCorrect = state.isCurrentAnswerCorrect
                                }
                            }
                            val isCorrect = lastIsCorrect == true
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = if (isCorrect) Color(0xFF10B981).copy(alpha = if (isLight) 0.15f else 0.2f) 
                                                else Color.Red.copy(alpha = if (isLight) 0.15f else 0.2f),
                                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                                    )
                                    .padding(24.dp)
                            ) {
                                Column {
                                    Text(
                                        text = if (isCorrect) "Benar!" else "Belum Tepat",
                                        color = if (isCorrect) Color(0xFF10B981) else Color.Red,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (!isCorrect) {
                                        Text(
                                            text = "Jawaban benar: ${question.correctAnswer}",
                                            color = textPrimary,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                    Text(
                                        text = question.explanation,
                                        color = textSecondary,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = { viewModel.nextQuestion() },
                                        modifier = Modifier.fillMaxWidth().height(50.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isCorrect) Color(0xFF10B981) else Color.Red
                                        ),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Text("Lanjut", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }

                        this@Column.AnimatedVisibility(
                            visible = !state.isAnswerChecked,
                            enter = fadeIn(),
                            exit = fadeOut(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val isCheckEnabled = when (question.type) {
                                PracticeQuestionType.MULTIPLE_CHOICE -> state.selectedAnswer != null
                                PracticeQuestionType.FILL_BLANK -> state.typedAnswer.isNotBlank()
                                PracticeQuestionType.SPEAKING -> state.spokenText.isNotBlank()
                            }
                            Box(modifier = Modifier.padding(24.dp)) {
                                Button(
                                    onClick = { viewModel.checkAnswer() },
                                    enabled = isCheckEnabled,
                                    modifier = Modifier.fillMaxWidth().height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9)),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Text("Cek Jawaban", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PracticeResult(
    correctCount: Int,
    totalCount: Int,
    closeButtonText: String,
    onClose: () -> Unit
) {
    val isLight = MaterialTheme.colorScheme.background.red > 0.5f
    val cardColor = if (isLight) MaterialTheme.colorScheme.surfaceVariant else Color(0xFF0F1A30)
    val textPrimary = if (isLight) MaterialTheme.colorScheme.onBackground else Color.White
    val textSecondary = if (isLight) MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f) else Color.LightGray

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Latihan Selesai!", color = textPrimary, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Jawaban Benar", color = textSecondary, fontSize = 16.sp)
                Text("$correctCount / $totalCount", color = Color(0xFF10B981), fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text("XP Didapat", color = textSecondary, fontSize = 16.sp)
                Text("+${correctCount * 10}", color = Color(0xFFF59E0B), fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onClose,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(closeButtonText, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}
