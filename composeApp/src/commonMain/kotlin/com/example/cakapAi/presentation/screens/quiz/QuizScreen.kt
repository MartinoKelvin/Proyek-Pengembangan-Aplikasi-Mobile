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

/**
 * High-fidelity representation of a Quiz Question.
 */
data class QuizQuestion(
    val id: Int,
    val type: String, // e.g. "LISTENING", "READING", "GAMING", "SPEAKING", "VIDEO"
    val prompt: String,
    val options: List<String>,
    val correctAnswer: String
)

/**
 * Sleek, gamified Quiz Screen with interactive choice buttons, 3D tactility,
 * heart health system, smooth horizontal progress indicators, and sliding correct/incorrect bottom feedback panels.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun QuizScreen(
    levelId: Int,
    onNavigateBack: () -> Unit,
    onFinishQuiz: (score: Int, totalQuestion: Int, accuracy: Int, isPassed: Boolean) -> Unit
) {
    // Generate questions statically based on levelId
    val questions = remember(levelId) {
        getQuestionsForLevel(levelId)
    }

    // State Variables
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var isAnswerChecked by remember { mutableStateOf(false) }
    var lives by remember { mutableStateOf(3) }
    var correctCount by remember { mutableStateOf(0) }
    var showExitDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Smooth question transition state
    var triggerQuestionTransition by remember { mutableStateOf(true) }

    // Color definitions matching the deep ocean theme
    val backgroundColor = Color(0xFF071224)
    val cardColor = Color(0xFF0F1A30).copy(alpha = 0.9f)
    val emeraldAccent = Color(0xFF10B981)
    val skyAccent = Color(0xFF0EA5E9)
    val redAccent = Color(0xFFEF4444)

    val currentQuestion = questions.getOrNull(currentQuestionIndex)

    // Handle game-over state
    LaunchedEffect(lives) {
        if (lives <= 0) {
            delay(600) // Brief delay to show incorrect answer feedback panel
            val accuracy = (correctCount * 100) / questions.size
            val score = correctCount * 20
            onFinishQuiz(score, questions.size, accuracy, false)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0B172E), backgroundColor)
                )
            )
    ) {
        // Confirmation dialog when clicking close/exit
        if (showExitDialog) {
            Dialog(onDismissRequest = { showExitDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
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
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Progres kuis Anda saat ini akan hilang.",
                            color = Color.LightGray,
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
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
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
                        .background(Color.White.copy(alpha = 0.06f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Keluar",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Progress Bar showing question progress (e.g. 2 of 5)
                val progress = (currentQuestionIndex.toFloat()) / questions.size.toFloat()
                val animatedProgress by animateFloatAsState(
                    targetValue = progress,
                    animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)
                )
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    color = emeraldAccent,
                    trackColor = Color.White.copy(alpha = 0.08f),
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
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                            ) {
                                Text(
                                    text = currentQuestion.prompt,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
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
                                else -> Color.White.copy(alpha = 0.08f)
                            }

                            val cardBgColor = when {
                                isAnswerChecked && isSelected && isCorrectAnswer -> emeraldAccent.copy(alpha = 0.12f)
                                isAnswerChecked && isSelected && !isCorrectAnswer -> redAccent.copy(alpha = 0.12f)
                                isAnswerChecked && !isSelected && isCorrectAnswer -> emeraldAccent.copy(alpha = 0.06f)
                                isSelected -> skyAccent.copy(alpha = 0.08f)
                                else -> Color(0xFF0F1A30).copy(alpha = 0.5f)
                            }

                            val letterBgColor = when {
                                isAnswerChecked && isSelected && isCorrectAnswer -> emeraldAccent
                                isAnswerChecked && isSelected && !isCorrectAnswer -> redAccent
                                isAnswerChecked && !isSelected && isCorrectAnswer -> emeraldAccent
                                isSelected -> skyAccent
                                else -> Color.White.copy(alpha = 0.06f)
                            }

                            val letterTextColor = when {
                                isAnswerChecked && isSelected -> Color.White
                                isSelected -> Color.White
                                else -> Color.LightGray
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
                                        selectedAnswer = option
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
                    .background(Color(0xFF0B1220))
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                if (!isAnswerChecked) {
                    // "CHECK" Button (Active only when an option is selected)
                    Button(
                        onClick = {
                            if (selectedAnswer != null && currentQuestion != null) {
                                isAnswerChecked = true
                                val isCorrect = selectedAnswer == currentQuestion.correctAnswer
                                if (isCorrect) {
                                    correctCount++
                                } else {
                                    lives--
                                }
                            }
                        },
                        enabled = selectedAnswer != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = emeraldAccent,
                            disabledContainerColor = Color.White.copy(alpha = 0.06f)
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
                            color = if (selectedAnswer != null) Color(0xFF030712) else Color.Gray
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
                                    
                                    if (currentQuestionIndex + 1 < questions.size) {
                                        currentQuestionIndex++
                                        selectedAnswer = null
                                        isAnswerChecked = false
                                        triggerQuestionTransition = true
                                    } else {
                                        // End of quiz - calculate accuracy and score
                                        val accuracy = (correctCount * 100) / questions.size
                                        val score = correctCount * 20
                                        onFinishQuiz(score, questions.size, accuracy, true)
                                    }
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
                                color = Color(0xFF030712),
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
                color = Color.White,
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
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(22.dp)
                    )
                } else if (isSelected && !isCorrect) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Salah",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(22.dp)
                    )
                } else if (!isSelected && isCorrect) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Benar",
                        tint = Color(0xFF10B981).copy(alpha = 0.5f),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

/**
 * Returns a static, curated set of 5 distinct questions per level
 * to match the CakapAi foreign language curriculum in the README.
 */
fun getQuestionsForLevel(levelId: Int): List<QuizQuestion> {
    return when (levelId) {
        1 -> listOf(
            QuizQuestion(1, "MENDENGARKAN", "Apa arti dari ungkapan sapaan 'Good Morning'?", listOf("Selamat malam", "Selamat pagi", "Selamat sore"), "Selamat pagi"),
            QuizQuestion(2, "MEMAHAMI", "Bagaimana menerjemahkan kalimat 'Nice to meet you' ke bahasa Indonesia?", listOf("Senang bertemu denganmu", "Apa kabarmu hari ini", "Selamat tinggal teman"), "Senang bertemu denganmu"),
            QuizQuestion(3, "MENDENGARKAN", "Bagaimana cara mengucapkan 'Terima kasih' dalam bahasa Inggris?", listOf("You are welcome", "Excuse me", "Thank you"), "Thank you"),
            QuizQuestion(4, "MEMAHAMI", "Apa arti dari ungkapan perpisahan 'Goodbye'?", listOf("Halo", "Selamat datang", "Selamat tinggal"), "Selamat tinggal"),
            QuizQuestion(5, "MENDENGARKAN", "Terjemahkan pertanyaan sederhana 'How are you?'", listOf("Apa kabar?", "Di mana rumahmu?", "Siapa namamu?"), "Apa kabar?")
        )
        2 -> listOf(
            QuizQuestion(1, "KOSAKATA", "Apa arti kata buah-buahan 'Apple' dalam bahasa Indonesia?", listOf("Nanas", "Jeruk", "Apel"), "Apel"),
            QuizQuestion(2, "KOSAKATA", "Kata benda 'Book' memiliki arti...", listOf("Buku", "Meja", "Pena"), "Buku"),
            QuizQuestion(3, "KOSAKATA", "Terjemahkan kata kebutuhan pokok 'Water'!", listOf("Makanan", "Air", "Udara"), "Air"),
            QuizQuestion(4, "KOSAKATA", "Binatang peliharaan 'Cat' memiliki arti...", listOf("Anjing", "Kelinci", "Kucing"), "Kucing"),
            QuizQuestion(5, "KOSAKATA", "Tempat belajar 'School' diterjemahkan menjadi...", listOf("Taman", "Sekolah", "Rumah Sakit"), "Sekolah")
        )
        3 -> listOf(
            QuizQuestion(1, "TATA BAHASA", "Lengkapi kalimat pronoun berikut: 'I ___ a student.'", listOf("is", "am", "are"), "am"),
            QuizQuestion(2, "TATA BAHASA", "Pilih kata ganti orang yang tepat: '___ is reading a book.' (Merujuk ke dia perempuan)", listOf("He", "She", "They"), "She"),
            QuizQuestion(3, "TATA BAHASA", "Lengkapi present tense: 'They ___ football everyday.'", listOf("play", "plays", "playing"), "play"),
            QuizQuestion(4, "TATA BAHASA", "Bentuk lampau (past tense) dari kata kerja 'go' adalah...", listOf("went", "gone", "goes"), "went"),
            QuizQuestion(5, "TATA BAHASA", "Bentuk jamak (plural) dari kata 'child' adalah...", listOf("childs", "childrens", "children"), "children")
        )
        4 -> listOf(
            QuizQuestion(1, "PERCAKAPAN", "A: 'How is the weather today?'\nB: 'It is ___.' (Cerah)", listOf("sunny", "rainy", "snowy"), "sunny"),
            QuizQuestion(2, "PERCAKAPAN", "Bagaimana menanyakan arah 'Where is the restroom?' dalam bahasa Indonesia?", listOf("Di mana toiletnya?", "Kapan stasiun dibuka?", "Berapa harga tiket ini?"), "Di mana toiletnya?"),
            QuizQuestion(3, "PERCAKAPAN", "Terjemahkan ungkapan permohonan 'Can you help me?'", listOf("Bisakah Anda membantu saya?", "Apakah Anda tahu jalan ini?", "Permisi, jam berapa sekarang?"), "Bisakah Anda membantu saya?"),
            QuizQuestion(4, "PERCAKAPAN", "A: 'What time is it?'\nB: 'It is five ___.'", listOf("hours", "o'clock", "minutes"), "o'clock"),
            QuizQuestion(5, "PERCAKAPAN", "Bagaimana menanyakan harga barang 'How much is this?'", listOf("Di mana ini?", "Berapa harganya?", "Kapan ini selesai?"), "Berapa harganya?")
        )
        5 -> listOf(
            QuizQuestion(1, "PENGUCAPAN", "Bagaimana mengucap sopan 'Excuse me' untuk permisi?", listOf("Permisi", "Maaf", "Halo"), "Permisi"),
            QuizQuestion(2, "PENGUCAPAN", "Bagaimana membalas terima kasih dengan ramah 'No problem'?", listOf("Sama-sama", "Tidak masalah", "Tentu saja"), "Tidak masalah"),
            QuizQuestion(3, "PENGUCAPAN", "Ungkapan selamat 'Congratulations!' diterjemahkan menjadi...", listOf("Selamat!", "Semoga berhasil!", "Hati-hati!"), "Selamat!"),
            QuizQuestion(4, "PENGUCAPAN", "Kata penyambutan hangat 'Welcome' berarti...", listOf("Selamat tinggal", "Selamat datang", "Sampai jumpa"), "Selamat datang"),
            QuizQuestion(5, "PENGUCAPAN", "Ungkapan doa baik 'Have a nice day!' berarti...", listOf("Semoga sukses selalu!", "Selamat beristirahat!", "Semoga hari Anda menyenangkan!"), "Semoga hari Anda menyenangkan!")
        )
        else -> listOf(
            QuizQuestion(1, "UMUM", "Apa terjemahan dari kata 'Yes'?", listOf("Ya", "Tidak", "Mungkin"), "Ya"),
            QuizQuestion(2, "UMUM", "Apa terjemahan dari kata 'No'?", listOf("Ya", "Tidak", "Mungkin"), "Tidak"),
            QuizQuestion(3, "UMUM", "Apa terjemahan dari kata 'Hello'?", listOf("Halo", "Selamat tinggal", "Maaf"), "Halo"),
            QuizQuestion(4, "UMUM", "Apa terjemahan dari kata 'Thank you'?", listOf("Terima kasih", "Sama-sama", "Maaf"), "Terima kasih"),
            QuizQuestion(5, "UMUM", "Apa terjemahan dari kata 'Please'?", listOf("Tolong", "Silakan", "Maaf"), "Silakan")
        )
    }
}