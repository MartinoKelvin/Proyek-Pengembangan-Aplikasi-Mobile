package com.example.cakapAi.presentation.screens.practice

import androidx.compose.animation.*
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
    onCompleted: (isSuccess: Boolean, score: Int) -> Unit
) {
    val viewModel: PracticeViewModel = koinViewModel(
        parameters = { parametersOf(level.id, level.title, level.type.name) }
    )
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF071224))
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
                    val progress = (state.currentQuestionIndex.toFloat() / state.questions.size)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = Color(0xFF10B981),
                        trackColor = Color(0xFF1E293B)
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
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A30)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = question.prompt,
                                    color = Color.White,
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
                                                color = if (isSelected) Color(0xFF0EA5E9) else Color(0xFF1E293B),
                                                shape = RoundedCornerShape(16.dp)
                                            ),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A30))
                                    ) {
                                        Text(
                                            text = option,
                                            color = Color.White,
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
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = Color(0xFF0EA5E9),
                                        unfocusedBorderColor = Color(0xFF1E293B)
                                    ),
                                    placeholder = { Text("Ketik jawaban di sini", color = Color.Gray) }
                                )
                                if (question.options.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        question.options.forEach { option ->
                                            val isSelected = state.typedAnswer == option
                                            OutlinedButton(
                                                onClick = { 
                                                    viewModel.updateTypedAnswer(option)
                                                    viewModel.speak(option)
                                                },
                                                colors = ButtonDefaults.outlinedButtonColors(
                                                    containerColor = if (isSelected) Color(0xFF0EA5E9) else Color.Transparent,
                                                    contentColor = Color.White
                                                ),
                                                border = BorderStroke(1.dp, if (isSelected) Color(0xFF0EA5E9) else Color.Gray)
                                            ) {
                                                Text(option)
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
                                        Text("Mendengarkan...", color = Color.White)
                                    }
                                    if (state.spokenText.isNotEmpty()) {
                                        Text("Kamu mengucapkan: \"${state.spokenText}\"", color = Color.LightGray)
                                    }
                                    if (state.errorMessage != null) {
                                        Text(state.errorMessage!!, color = Color.Red, fontSize = 12.sp)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedTextField(
                                            value = state.spokenText,
                                            onValueChange = { viewModel.updateSpokenTextFallback(it) },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White,
                                                focusedBorderColor = Color(0xFF0EA5E9),
                                                unfocusedBorderColor = Color(0xFF1E293B)
                                            ),
                                            placeholder = { Text("Ketik jawaban fallback di sini", color = Color.Gray) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Bottom Feedback Panel
                    if (state.isAnswerChecked) {
                        val isCorrect = state.isCurrentAnswerCorrect == true
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isCorrect) Color(0xFF10B981).copy(alpha = 0.2f) else Color.Red.copy(alpha = 0.2f))
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
                                        color = Color.White,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                                Text(
                                    text = question.explanation,
                                    color = Color.LightGray,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { viewModel.nextQuestion() },
                                    modifier = Modifier.fillMaxWidth().height(50.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isCorrect) Color(0xFF10B981) else Color.Red
                                    )
                                ) {
                                    Text("Lanjut", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
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
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9))
                            ) {
                                Text("Cek Jawaban", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Latihan Selesai!", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A30)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Jawaban Benar", color = Color.Gray, fontSize = 16.sp)
                Text("$correctCount / $totalCount", color = Color(0xFF10B981), fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text("XP Didapat", color = Color.Gray, fontSize = 16.sp)
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
            Text("Kembali ke Peta", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}
