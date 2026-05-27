package com.example.cakapAi.presentation.screens.quiz

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf



/**
 * Sleek, gamified Quiz Screen with interactive choice buttons, 3D tactility,
 * heart health system, smooth horizontal progress indicators, and sliding correct/incorrect bottom feedback panels.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun QuizScreen(
    levelId: Int,
    onNavigateBack: () -> Unit,
    onFinishQuiz: (score: Int, totalQuestion: Int, accuracy: Int, isPassed: Boolean) -> Unit,
    viewModel: QuizViewModel = koinViewModel { parametersOf(levelId) }
) {
    val uiState by viewModel.uiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    // Smooth question transition state
    var triggerQuestionTransition by remember { mutableStateOf(true) }

    // Color definitions matching the deep ocean theme (fully dynamic)
    val isLight = MaterialTheme.colorScheme.background.red > 0.5f
    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val emeraldAccent = MaterialTheme.colorScheme.primary
    val skyAccent = MaterialTheme.colorScheme.secondary
    val redAccent = MaterialTheme.colorScheme.error

    // Dynamic theme-based text and border colors to support Light Mode perfectly
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val borderStrokeColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
    val gradientStart = if (isLight) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color(0xFF0B172E)

    var showExitDialog by remember { mutableStateOf(false) }

    // Handle game-over and completion state from ViewModel state changes
    LaunchedEffect(uiState) {
        val state = uiState
        if (state is QuizUiState.Success) {
            if (state.lives <= 0) {
                delay(600) // Brief delay to show incorrect answer feedback panel
                val accuracy = if (state.questions.isNotEmpty()) (state.correctCount * 100) / state.questions.size else 0
                val score = state.correctCount * 20
                onFinishQuiz(score, state.questions.size, accuracy, false)
            } else if (state.isFinished) {
                val accuracy = if (state.questions.isNotEmpty()) (state.correctCount * 100) / state.questions.size else 0
                val score = state.correctCount * 20
                onFinishQuiz(score, state.questions.size, accuracy, true)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(gradientStart, backgroundColor)
                )
            )
    ) {
        when (val state = uiState) {
            is QuizUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = emeraldAccent,
                        strokeWidth = 4.dp
                    )
                }
            }
            is QuizUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, borderStrokeColor)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Error",
                                tint = redAccent,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Gagal Memuat Kuis",
                                color = textPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.message,
                                color = textSecondary,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            is QuizUiState.Success -> {
                val questions = state.questions
                val currentQuestionIndex = state.currentQuestionIndex
                val selectedAnswer = state.selectedAnswer
                val isAnswerChecked = state.isAnswerChecked
                val lives = state.lives
                val currentQuestion = questions.getOrNull(currentQuestionIndex)

                if (showExitDialog) {
                    Dialog(onDismissRequest = { showExitDialog = false }) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = cardColor),
                            border = BorderStroke(1.dp, borderStrokeColor)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Warning",
                                    tint = redAccent,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Keluar Kuis?",
                                    color = textPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Progres kuis Anda saat ini akan hilang.",
                                    color = textSecondary,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { showExitDialog = false },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = textPrimary),
                                        border = BorderStroke(1.dp, borderStrokeColor),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Batal")
                                    }
                                    Button(
                                        onClick = {
                                            showExitDialog = false
                                            onNavigateBack()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = redAccent),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Keluar", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding()
                        .statusBarsPadding()
                ) {
                    // 1. TOP HEADER PANEL (Close, Animated Progress Bar, Hearts)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Exit button
                        IconButton(
                            onClick = { showExitDialog = true },
                            modifier = Modifier
                                .size(36.dp)
                                .background(if (isLight) MaterialTheme.colorScheme.surfaceVariant else Color.White.copy(alpha = 0.06f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Keluar",
                                tint = if (isLight) MaterialTheme.colorScheme.onBackground else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Progress Bar showing question progress (e.g. 2 of 5)
                        val progress = if (questions.isNotEmpty()) (currentQuestionIndex.toFloat()) / questions.size.toFloat() else 0f
                        val animatedProgress by animateFloatAsState(
                            targetValue = progress,
                            animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)
                        )
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            color = emeraldAccent,
                            trackColor = if (isLight) MaterialTheme.colorScheme.outline.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.08f),
                            modifier = Modifier
                                .weight(1f)
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        // Heart Health Indicators (glowing or disabled hearts)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (i in 1..3) {
                                val isLost = i > lives
                                val heartColor by animateColorAsState(
                                    targetValue = if (isLost) Color(0xFF334155) else redAccent,
                                    animationSpec = tween(300)
                                )
                                val scale by animateFloatAsState(
                                    targetValue = if (isLost) 0.8f else 1.0f,
                                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                                )

                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Heart $i",
                                    tint = heartColor,
                                    modifier = Modifier
                                        .size(22.dp)
                                        .graphicsLayer {
                                            scaleX = scale
                                            scaleY = scale
                                        }
                                )
                            }
                        }
                    }

                    // Divider line
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))

                    if (currentQuestion != null) {
                        // Main scrollable quiz content area
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.Top
                        ) {
                            Spacer(modifier = Modifier.height(20.dp))

                            // Animated question slide-in and fade-in container
                            AnimatedVisibility(
                                visible = triggerQuestionTransition,
                                enter = slideInHorizontally(initialOffsetX = { 200 }) + fadeIn(animationSpec = tween(300)),
                                exit = fadeOut(animationSpec = tween(150))
                            ) {
                                Column {
                                    // Category Chip indicator
                                    Surface(
                                        color = skyAccent.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, skyAccent.copy(alpha = 0.25f))
                                    ) {
                                        Text(
                                            text = currentQuestion.type,
                                            color = skyAccent,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            letterSpacing = 1.sp,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Glassmorphic Question card
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .shadow(8.dp, RoundedCornerShape(20.dp)),
                                        colors = CardDefaults.cardColors(containerColor = cardColor),
                                        shape = RoundedCornerShape(20.dp),
                                        border = BorderStroke(1.dp, borderStrokeColor)
                                    ) {
                                        Text(
                                            text = currentQuestion.prompt,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = textPrimary,
                                            lineHeight = 28.sp,
                                            modifier = Modifier.padding(24.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(28.dp))

                            // Choices list with spring click animations
                            Column(
                                verticalArrangement = Arrangement.spacedBy(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                currentQuestion.options.forEachIndexed { index, option ->
                                    val letter = ('A' + index).toString()

                                    val isSelected = selectedAnswer == option
                                    val isCorrectAnswer = option == currentQuestion.correctAnswer

                                    // Determine tactile border and background colors
                                    val cardBorderColor = when {
                                        isAnswerChecked && isSelected && isCorrectAnswer -> emeraldAccent
                                        isAnswerChecked && isSelected && !isCorrectAnswer -> redAccent
                                        isAnswerChecked && !isSelected && isCorrectAnswer -> emeraldAccent
                                        isSelected -> skyAccent
                                        else -> borderStrokeColor
                                    }

                                    val cardBgColor = when {
                                        isAnswerChecked && isSelected && isCorrectAnswer -> emeraldAccent.copy(alpha = 0.12f)
                                        isAnswerChecked && isSelected && !isCorrectAnswer -> redAccent.copy(alpha = 0.12f)
                                        isAnswerChecked && !isSelected && isCorrectAnswer -> emeraldAccent.copy(alpha = 0.06f)
                                        isSelected -> skyAccent.copy(alpha = 0.08f)
                                        else -> MaterialTheme.colorScheme.surface
                                    }

                                    val letterBgColor = when {
                                        isAnswerChecked && isSelected && isCorrectAnswer -> emeraldAccent
                                        isAnswerChecked && isSelected && !isCorrectAnswer -> redAccent
                                        isAnswerChecked && !isSelected && isCorrectAnswer -> emeraldAccent
                                        isSelected -> skyAccent
                                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                    }

                                    val letterTextColor = when {
                                        isAnswerChecked && isSelected -> Color.White
                                        isSelected -> Color.White
                                        else -> textSecondary
                                    }

                                    // Tactile option row
                                    OptionTactileRow(
                                        letter = letter,
                                        text = option,
                                        cardBgColor = cardBgColor,
                                        cardBorderColor = cardBorderColor,
                                        letterBgColor = letterBgColor,
                                        letterTextColor = letterTextColor,
                                        isAnswerChecked = isAnswerChecked,
                                        isSelected = isSelected,
                                        isCorrect = isCorrectAnswer,
                                        enabled = !isAnswerChecked,
                                        onClick = {
                                            if (!isAnswerChecked) {
                                                viewModel.selectAnswer(option)
                                            }
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(40.dp))
                        }
                    }

                    // 2. DYNAMIC BOTTOM PANEL (Check / Continue / Sliding Feedback)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 24.dp, vertical = 20.dp)
                    ) {
                        if (!isAnswerChecked) {
                            // "CHECK" Button (Active only when an option is selected)
                            Button(
                                onClick = {
                                    viewModel.checkAnswer()
                                },
                                enabled = selectedAnswer != null,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = emeraldAccent,
                                    disabledContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                Text(
                                    text = "PERIKSA JAWABAN",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    color = if (selectedAnswer != null) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                )
                            }
                        } else {
                            // CONTINUE button (Transitions to the next question or finishes the quiz)
                            val isCorrect = selectedAnswer == currentQuestion?.correctAnswer

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isCorrect) "Luar biasa! Benar 🎉" else "Jawaban kurang tepat 😢",
                                        color = if (isCorrect) emeraldAccent else redAccent,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = if (isCorrect) "Pertahankan performa Anda!" else "Kunci: ${currentQuestion?.correctAnswer}",
                                        color = Color.LightGray,
                                        fontSize = 12.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            // Trigger slide out question animation
                                            triggerQuestionTransition = false
                                            delay(150)
                                            viewModel.nextQuestion()
                                            triggerQuestionTransition = true
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (isCorrect) emeraldAccent else redAccent),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .width(130.dp)
                                        .height(52.dp)
                                ) {
                                    Text(
                                        text = "LANJUT",
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp
                                    )
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
fun OptionTactileRow(
    letter: String,
    text: String,
    cardBgColor: Color,
    cardBorderColor: Color,
    letterBgColor: Color,
    letterTextColor: Color,
    isAnswerChecked: Boolean,
    isSelected: Boolean,
    isCorrect: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    // Tactile bounce scale on press
    var isPressed by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    // Shake animation trigger for wrong answer
    val triggerShake = isAnswerChecked && isSelected && !isCorrect
    val shakeOffset = remember { Animatable(0f) }
    
    LaunchedEffect(triggerShake) {
        if (triggerShake) {
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 300
                    -12f at 50
                    12f at 100
                    -8f at 150
                    8f at 200
                    -4f at 250
                }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
                translationX = shakeOffset.value
            }
            .clip(RoundedCornerShape(18.dp))
            .background(cardBgColor)
            .border(BorderStroke(1.5.dp, cardBorderColor), RoundedCornerShape(18.dp))
            .clickable(enabled = enabled) {
                onClick()
            }
            .padding(horizontal = 18.dp, vertical = 15.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Round Letter Badge on the Left (A, B, C, D)
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(letterBgColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter,
                    color = letterTextColor,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Main choice text
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            // Right checkmark or cross icon when checked
             if (isAnswerChecked) {
                if (isSelected && isCorrect) {
                     Icon(
                         imageVector = Icons.Default.CheckCircle,
                         contentDescription = "Benar",
                         tint = MaterialTheme.colorScheme.primary,
                         modifier = Modifier.size(22.dp)
                     )
                } else if (isSelected && !isCorrect) {
                     Icon(
                         imageVector = Icons.Default.Cancel,
                         contentDescription = "Salah",
                         tint = MaterialTheme.colorScheme.error,
                         modifier = Modifier.size(22.dp)
                     )
                } else if (!isSelected && isCorrect) {
                     Icon(
                         imageVector = Icons.Default.CheckCircle,
                         contentDescription = "Benar",
                         tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                         modifier = Modifier.size(22.dp)
                     )
                }
             }
        }
    }
}
